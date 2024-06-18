package com.sm.domain.enums

enum class Environment(val value: String) {
    DATABASE_NAME(value = System.getenv("db_name") ?: ""),
    DATABASE_URI(value = System.getenv("db_uri") ?: ""),
    JWT_ISSUER(value = System.getenv("jwt_issuer") ?: ""),
    JWT_AUDIENCE(value = System.getenv("jwt_audience") ?: ""),
    JWT_SECRET(value = System.getenv("jwt_secret") ?: ""),
    JWT_REALM(value = System.getenv("jwt_realm") ?: "");
}