package com.sm.domain.interfaces

import com.sm.domain.models.db.UserDocument

interface AuthDatasource {

    suspend fun getById(id: String): UserDocument?

    suspend fun getByEmail(email: String): UserDocument?

    suspend fun save(user: UserDocument): Boolean
}