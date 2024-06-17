package com.sm.extensions

import com.sm.domain.interfaces.AuthDatasource
import com.sm.domain.interfaces.HashingService
import com.sm.domain.interfaces.TokenService
import com.sm.domain.models.request.SignInRequest
import com.sm.domain.models.request.SignUpRequest
import com.sm.domain.models.response.APIResponse
import com.sm.domain.models.token.SaltedHash
import com.sm.domain.models.token.TokenClaim
import com.sm.domain.models.token.TokenConfig
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
            request.toUserDocument(hashedPassword = saltedHash.hash, hashedSalt = saltedHash.salt)
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
            HttpStatusCode.InternalServerError,
            message = APIResponse(
                code = HttpStatusCode.InternalServerError.value,
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
) {

    val email = "sphemicah@gmail.com"

    if (email != request.email) {
        call.respond(
            HttpStatusCode.NotFound,
            message = APIResponse<String>(
                code = HttpStatusCode.NotFound.value,
                message = "Invalid email or password",
                success = false,
                response = null
            )
        )
        return
    }

    val isValidPassword = hashingService.verify(
        request.password,
        saltedHash = SaltedHash(
            hash = "81022c39f9c26411cf09222091941992e3427156ec831b5cc24b5bbc37eded8e",
            salt = "ad2e5917d11f9ce8115ba52206dae665eda02a14f0b723946c9ec3aff5446ace")
    )

    if (!isValidPassword) {
        call.respond(
            HttpStatusCode.NotFound,
            message = APIResponse<String>(
                code = HttpStatusCode.NotFound.value,
                message = "Invalid email or password",
                success = false,
                response = null
            )
        )
        return
    }

    val accessToken = tokenService.generate(
        config = tokenConfig,
        TokenClaim(name = "id", value = 1995.toString()),
        TokenClaim(name = "role", value = "admin"),
        TokenClaim(name = "email", value = email),
        TokenClaim(name = "tokenType", value = "accessToken")
    )

    val refreshToken = tokenService.generate(
        config = tokenConfig.copy(expiresIn = 24 * 60 * 60 * 1000),
        TokenClaim(name = "email", value = email),
        TokenClaim(name = "tokenType", value = "refreshToken")
    )

    call.respond(
        HttpStatusCode.OK,
        message = APIResponse(
            code = HttpStatusCode.OK.value,
            message = "Authentication success",
            success = true,
            response = mapOf(
                "accessToken" to accessToken,
                "refreshToken" to refreshToken
            )
        ))
}

suspend fun PipelineContext<Unit, ApplicationCall>.refreshToken(
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    principal: JWTPrincipal
) {
    val tokenEmail = principal.userEmail

    val email = "sphemicah@gmail.com"

    if (email != tokenEmail) {
        call.respond(
            HttpStatusCode.NotFound,
            message = APIResponse<String>(
                code = HttpStatusCode.NotFound.value,
                message = "Invalid email or password",
                success = false,
                response = null
            )
        )
        return
    }

    val accessToken = tokenService.generate(
        config = tokenConfig,
        TokenClaim("id", 1995.toString()),
        TokenClaim("role", "admin"),
        TokenClaim("email", email),
        TokenClaim("tokenType", "accessToken")
    )

    val refreshToken = tokenService.generate(
        config = tokenConfig.copy(expiresIn = 24 * 60 * 60 * 1000),
        TokenClaim("email", email),
        TokenClaim("tokenType", "refreshToken")
    )

    call.respond(
        HttpStatusCode.OK,
        message = APIResponse(
            code = HttpStatusCode.OK.value,
            message = "New tokens generated",
            success = true,
            response = mapOf(
                "accessToken" to accessToken,
                "refreshToken" to refreshToken
            )
        ))
}