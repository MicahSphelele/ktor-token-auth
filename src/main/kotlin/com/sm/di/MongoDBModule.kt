package com.sm.di

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.sm.util.Constants.DATABASE_NAME
import com.sm.util.Constants.DATABASE_URI
import org.koin.dsl.module

val mongoDBModule =  module {

    single {
        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(DATABASE_URI))
            .build()
        MongoClient
            .create(settings)
            .getDatabase(databaseName = DATABASE_NAME)
    }
}