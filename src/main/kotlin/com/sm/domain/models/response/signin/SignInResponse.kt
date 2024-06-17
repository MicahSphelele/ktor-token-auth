package com.sm.domain.models.response.signin

import com.sm.domain.models.response.token.TokenResponse
import com.sm.domain.models.user.UserDTO
import kotlinx.serialization.Serializable

@Serializable
data class SignInResponse(
    val user: UserDTO,
    val tokens: TokenResponse
)