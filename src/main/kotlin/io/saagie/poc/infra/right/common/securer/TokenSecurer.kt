package io.saagie.poc.infra.right.common.securer

import io.saagie.poc.infra.AppProperties
import io.saagie.poc.infra.right.common.Requester
import io.saagie.poc.infra.right.common.process
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@Profile("token")
class TokenSecurer(
        private val properties: AppProperties,
        private val extractor: TokenExtractor,
        private val restTemplate: RestTemplate
): AbstractSecurer() {
    // ATTRIBUTES
    private val requester = Requester(BasicSecurer(properties))

    // METHOD
    override fun getAuthorization() = "Bearer ${
        restTemplate.process(
                request = requester.get(properties.common.tokenUrl),
                returnType = extractor.tokenClass(),
                verify = { it != null },
                transform = { extractor.action()(it!!) }
        )
    }"
}
