package io.saagie.poc.infra.right.common.securer

import org.springframework.http.RequestEntity

class NoSecurer: Securer {
    override fun secure(request: RequestEntity.HeadersBuilder<*>): RequestEntity.HeadersBuilder<*> = request
    override fun secure(request: RequestEntity.BodyBuilder): RequestEntity.BodyBuilder = request
}
