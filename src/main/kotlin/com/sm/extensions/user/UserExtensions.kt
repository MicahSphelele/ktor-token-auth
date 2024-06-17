package com.sm.extensions.user

import com.sm.domain.models.db.UserDocument
import com.sm.domain.models.request.SignUpRequest
import com.sm.domain.models.response.signin.SignInResponse
import com.sm.domain.models.user.User

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

val UserDocument.toUser
    get(): User = User(
        firstName = firstName,
        lastName = lastName,
        email = email
    )


fun User.toSignInResponse(accessToken: String, refreshToken: String): SignInResponse =
    SignInResponse(
        user = this,
        accessToken = accessToken,
        refreshToken = refreshToken
    )