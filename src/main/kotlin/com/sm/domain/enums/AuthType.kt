package com.sm.domain.enums

enum class AuthType(val type: String, val tokenType: String) {
    JWT_AUTH_ACCESS_TOKEN(type = "jwt-auth-access-token", tokenType = "access_token"),
    JWT_AUTH_REFRESH_TOKEN(type = "jwt-auth-refresh-token", tokenType = "refresh_token")
}