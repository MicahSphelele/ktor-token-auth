package com.sm.extensions

import com.sm.domain.enums.AuthType
import com.sm.domain.interfaces.AuthDatasource
import com.sm.domain.interfaces.HashingService
import com.sm.domain.interfaces.TokenService
import com.sm.domain.models.request.SignInRequest
import com.sm.domain.models.request.SignUpRequest
import com.sm.domain.models.response.APIResponse
import com.sm.domain.models.response.token.TokenResponse
import com.sm.domain.models.token.SaltedHash
import com.sm.domain.models.token.TokenClaim
import com.sm.domain.models.token.TokenConfig
import com.sm.extensions.user.toSignInResponse
import com.sm.extensions.user.toUserDTO
import com.sm.extensions.user.toUserDocument
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext


suspend fun PipelineContext<Unit, ApplicationCall>.signup(
    request: SignUpRequest,
    hashingService: HashingService,
    authDatasource: AuthDatasource
) {

    val userDocument = authDatasource.getByEmail(email = request.email)

    userDocument?.let {
        call.respond(
            HttpStatusCode.Conflict,
            message = APIResponse(
                code = HttpStatusCode.Conflict.value,
                message = "Email already in use",
                success = false,
                response = null
            )
        )
    } ?: kotlin.run {

        val saltedHash = hashingService.generate(value = request.password)

        val userDocumentAdded = authDatasource.save(
            user = request.toUserDocument(
                    hashedPassword = saltedHash.hash,
                    hashedSalt = saltedHash.salt
                )
        )

        if (userDocumentAdded.not()) {
            call.respond(
                HttpStatusCode.InternalServerError,
                message = APIResponse(
                    code = HttpStatusCode.InternalServerError.value,
                    developerMessage = "Database error occurred trying to save user details",
                    message = "Unable to save your details",
                    success = false,
                    response = null
                )
            )
            return
        }

        call.respond(
            HttpStatusCode.Created,
            message = APIResponse(
                code = HttpStatusCode.Created.value,
                message = "Your details have been successfully saved",
                success = true,
                response = null
            )
        )
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.signin(
    request: SignInRequest,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    authDatasource: AuthDatasource
) {

    val userDocument = authDatasource.getByEmail(email = request.email)

    userDocument?.let { user ->

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )

        if (!isValidPassword) {
            call.respond(
                HttpStatusCode.NotFound,
                message = APIResponse<String>(
                    code = HttpStatusCode.NotFound.value,
                    message = "Unable to authenticate please check email or password",
                    success = false,
                    response = null
                )
            )
            return
        }

        val config = tokenConfig.copy(subject = user.fullName)

        val accessToken = tokenService.generate(
            config = config,
            TokenClaim(name = "id", value = user._id),
            TokenClaim(name = "email", value = user.email),
            TokenClaim(name = "token_type", value = AuthType.JWT_AUTH_ACCESS_TOKEN.tokenType)
        )

        val refreshToken = tokenService.generate(
            config = config.copy(expiresIn = 24 * 60 * 60 * 1000),
            TokenClaim(name = "email", value = user.email),
            TokenClaim(name = "token_type", value = AuthType.JWT_AUTH_REFRESH_TOKEN.tokenType)
        )

        call.respond(
            HttpStatusCode.OK,
            message = APIResponse(
                code = HttpStatusCode.OK.value,
                message = "Authentication success",
                success = true,
                response = user.toUserDTO.toSignInResponse(
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            )
        )

    } ?: kotlin.run {
        call.respond(
            HttpStatusCode.NotFound,
            message = APIResponse<String>(
                code = HttpStatusCode.NotFound.value,
                message = "Invalid email or password",
                success = false,
                response = null
            )
        )
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.refreshToken(
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    principal: JWTPrincipal,
    authDatasource: AuthDatasource
) {

    val email = principal.email

    val userDocument = authDatasource.getByEmail(email = email)

    userDocument?.let { user ->

        val config = tokenConfig.copy(subject = user.fullName)

        val accessToken = tokenService.generate(
            config = config,
            TokenClaim(name = "id", value = user._id),
            TokenClaim(name = "email", value = user.email),
            TokenClaim(name = "tokenType", value = AuthType.JWT_AUTH_ACCESS_TOKEN.tokenType)
        )

        val refreshToken = tokenService.generate(
            config = config.copy(expiresIn = 24 * 60 * 60 * 1000),
            TokenClaim("email", user.email),
            TokenClaim(name = "tokenType", value = AuthType.JWT_AUTH_REFRESH_TOKEN.tokenType)
        )

        call.respond(
            HttpStatusCode.OK,
            message = APIResponse(
                code = HttpStatusCode.OK.value,
                message = "New tokens generated",
                success = true,
                response = TokenResponse(
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            )
        )

    } ?: kotlin.run {
        call.respond(
            HttpStatusCode.NotFound,
            message = APIResponse<String>(
                code = HttpStatusCode.NotFound.value,
                message = "Unable to retrieve user details",
                success = false,
                response = null
            )
        )
    }
}