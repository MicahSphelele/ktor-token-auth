package com.sm.extensions.user

import com.sm.domain.models.db.UserDocument
import com.sm.domain.models.request.SignUpRequest

fun SignUpRequest.toUserDocument(
    hashedPassword: String,
    hashedSalt: String
): UserDocument {
    return UserDocument(
        firstName = firstName,
        lastName = lastName,
        email = email,
        password = hashedPassword,
        salt = hashedSalt
    )
}