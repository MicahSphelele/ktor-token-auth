package com.sm.domain.models.response

import kotlinx.serialization.Serializable

@Serializable
data class APIResponse<T>(
    val code: Int,
    val developerMessage: String? = null,
    val message: String,
    val success: Boolean,
    val response: T?
)