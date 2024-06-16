package com.sm.domain.interfaces

import com.sm.domain.models.token.SaltedHash

interface HashingService {

    fun generate(value: String, saltLength: Int = 32): SaltedHash

    fun verify(value: String, saltedHash: SaltedHash): Boolean
}