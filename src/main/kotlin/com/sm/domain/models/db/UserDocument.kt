package com.sm.domain.models.db

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDocument(
    @SerialName("_id")
    var id: String = "",
    var firstName: String = "",
    var lastName: String,
    var email: String = "",
    var password: String = "",
    var salt: String = "",
    var createDate: Long = System.currentTimeMillis(),
)
