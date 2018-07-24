package io.saagie.poc.infra.right.common.securer

import org.springframework.context.annotation.Profile
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component

@Component
@Profile("none")
class NoSecurer: Securer {
    override fun secure(request: RequestEntity.HeadersBuilder<*>): RequestEntity.HeadersBuilder<*> = request
    override fun secure(request: RequestEntity.BodyBuilder): RequestEntity.BodyBuilder = request
}
