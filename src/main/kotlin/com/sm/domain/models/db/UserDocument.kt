package com.sm.domain.models.db

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class UserDocument(
    @Suppress("PropertyName")
    @SerialName("_id")
    var _id: String = ObjectId.get().toHexString(),
    var firstName: String = "",
    var lastName: String,
    var email: String = "",
    var password: String = "",
    var salt: String = "",
    var createDate: Long = System.currentTimeMillis(),
) {
    val fullName: String
        get() = "$firstName $lastName"
}
