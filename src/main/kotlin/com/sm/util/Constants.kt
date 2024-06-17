package com.sm.util

object Constants {
    val DATABASE_NAME = System.getenv("db_name") ?: ""
    val DATABASE_URI = System.getenv("db_uri") ?: ""
    val JWT_ISSUER = System.getenv("jwt_issuer") ?: ""
    val JWT_AUDIENCE = System.getenv("jwt_audience") ?: ""
    val JWT_SECRET = System.getenv("jwt_secret") ?: ""
    val JWT_REALM = System.getenv("jwt_realm") ?: ""
}