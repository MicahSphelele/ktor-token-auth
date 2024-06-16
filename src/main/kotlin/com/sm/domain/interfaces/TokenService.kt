package com.sm.domain.interfaces

import com.sm.domain.models.token.TokenClaim
import com.sm.domain.models.token.TokenConfig

interface TokenService {

    fun generate(config: TokenConfig, vararg claims: TokenClaim) : String
}