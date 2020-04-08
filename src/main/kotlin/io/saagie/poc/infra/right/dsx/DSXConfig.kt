package io.saagie.poc.infra.right.dsx

import io.saagie.poc.infra.right.common.securer.TokenExtractor
import io.saagie.poc.infra.right.common.securer.createTokenExtractor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("dsx")
class DSXConfig {
    @Bean
    fun tokenExtractor(): TokenExtractor = TokenDTO::class.java.createTokenExtractor { it.accessToken }

    data class TokenDTO (
            val accessToken: String = ""
    )
}
