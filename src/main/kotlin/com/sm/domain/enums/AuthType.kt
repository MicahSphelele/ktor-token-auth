package com.sm.domain.enums

enum class AuthType(val type: String) {
    JWT_AUTH_ACCESS_TOKEN(type = "jwt-auth-access-token"),
    JWT_AUTH_REFRESH_TOKEN(type = "jwt-auth-refresh-token")
}