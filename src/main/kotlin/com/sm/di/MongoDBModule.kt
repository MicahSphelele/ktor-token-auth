package com.sm.di

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.sm.domain.enums.Environment
import org.koin.dsl.module

val mongoDBModule =  module {

    single {
        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(Environment.DATABASE_URI.value))
            .build()
        MongoClient
            .create(settings)
            .getDatabase(databaseName = Environment.DATABASE_NAME.value)
    }
}