package com.sm.domain.models.token

data class SaltedHash(val hash: String, val salt: String)