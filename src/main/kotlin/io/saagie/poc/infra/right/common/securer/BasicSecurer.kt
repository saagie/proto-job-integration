package io.saagie.poc.infra.right.common.securer

import io.saagie.poc.infra.AppProperties
import io.saagie.poc.infra.right.common.encode64
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("basic")
class BasicSecurer(private val properties: AppProperties): AbstractSecurer() {
    // METHOD
    override fun getAuthorization() = "Basic ${encode64("${properties.common.username}:${properties.common.password}")}"
}
