package com.sm.extensions.user

import com.sm.domain.models.db.UserDocument
import com.sm.domain.models.request.SignUpRequest
import com.sm.domain.models.response.signin.SignInResponse
import com.sm.domain.models.response.token.TokenResponse
import com.sm.domain.models.user.UserDTO

fun SignUpRequest.toUserDocument(
    hashedPassword: String,
    hashedSalt: String
): UserDocument = UserDocument(
    firstName = firstName,
    lastName = lastName,
    email = email,
    password = hashedPassword,
    salt = hashedSalt
)

val UserDocument.toUserDTO
    get(): UserDTO = UserDTO(
        firstName = firstName,
        lastName = lastName,
        email = email
    )


fun UserDTO.toSignInResponse(accessToken: String, refreshToken: String): SignInResponse =
    SignInResponse(
        user = this,
        tokens = TokenResponse(accessToken = accessToken, refreshToken = refreshToken),
    )