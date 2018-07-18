package io.saagie.poc.infra.right.common.securer

import io.saagie.poc.infra.right.common.Requester
import io.saagie.poc.infra.right.common.process
import org.springframework.web.client.RestTemplate

open class TokenSecurer<T>(
        private val username: String,
        private val password: String,
        private val tokenUrl: String,
        private val tokenDTO: Class<T>,
        private val tokenExtractor: (T) -> String,
        private val restTemplate: RestTemplate
): AbstractSecurer() {
    // ATTRIBUTE
    private val requester = Requester(BasicSecurer(username, password))

    // METHOD
    override fun getAuthorization(): String {
        val token = restTemplate.process(
                request = requester.get(tokenUrl),
                returnType = tokenDTO,
                verify = { it != null },
                transform = { tokenExtractor(it!!) }
        )

        return "Bearer $token"
    }
}
