package com.sm.extensions

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.sm.domain.interfaces.HashingService
import com.sm.domain.interfaces.TokenService
import com.sm.domain.models.request.RefreshRequest
import com.sm.domain.models.request.SignInRequest
import com.sm.domain.models.request.SignUpRequest
import com.sm.domain.models.response.APIResponse
import com.sm.domain.models.token.SaltedHash
import com.sm.domain.models.token.TokenClaim
import com.sm.domain.models.token.TokenConfig
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext


suspend fun PipelineContext<Unit, ApplicationCall>.signup(
    hashingService: HashingService,
    request: SignUpRequest
) {

    val saltedHash = hashingService.generate(value = request.password)

    call.respond(
        HttpStatusCode.Created,
        message = APIResponse(
            code = HttpStatusCode.Created.value,
            message = "Account creation success",
            success = true,
            response = mapOf(
                "hash" to saltedHash.hash,
                "salt" to saltedHash.salt
            )
        )
    )
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
            message = "Authentication success",
            success = true,
            response = mapOf(
                "accessToken" to accessToken,
                "refreshToken" to refreshToken
            )
        ))
}

suspend fun PipelineContext<Unit, ApplicationCall>.refreshToken(
    request: RefreshRequest,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
) {

    val decodedJWT = JWT.require(Algorithm.HMAC256(tokenConfig.secret))
        .build()
        .verify(request.refreshToken)

    val tokenEmail = decodedJWT.getClaim("email").asString()

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