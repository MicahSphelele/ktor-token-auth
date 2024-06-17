package com.sm.di

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.sm.util.Constants.DATABASE_NAME
import org.koin.dsl.module

val mongoDBModule =  module {

    single {
        MongoClient.create()
            .getDatabase(databaseName = DATABASE_NAME)
    }
}