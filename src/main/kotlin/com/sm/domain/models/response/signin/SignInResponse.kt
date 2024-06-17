package com.sm.domain.models.response.signin

import com.sm.domain.models.user.User
import kotlinx.serialization.Serializable

@Serializable
data class SignInResponse(val user: User, val accessToken: String, val refreshToken: String)