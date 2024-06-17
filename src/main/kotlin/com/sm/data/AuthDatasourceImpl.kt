package com.sm.data

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.sm.domain.interfaces.AuthDatasource
import com.sm.domain.models.db.UserDocument
import kotlinx.coroutines.flow.firstOrNull

class AuthDatasourceImpl(database: MongoDatabase) : AuthDatasource {

    private val userCollection = database.getCollection<UserDocument>(collectionName = "user")

    override suspend fun getById(id: String): UserDocument? =
        userCollection.find(Filters.eq(UserDocument::_id.name, id)).firstOrNull()

    override suspend fun getByEmail(email: String): UserDocument? =
        userCollection.find(filter = Filters.eq(UserDocument::email.name, email)).firstOrNull()

    override suspend fun save(user: UserDocument): Boolean =
        userCollection.insertOne(document = user).wasAcknowledged()
}