package io.saagie.poc.infra.right.common.securer

import org.springframework.http.RequestEntity

interface Securer {
    fun secure(request: RequestEntity.HeadersBuilder<*>) : RequestEntity.HeadersBuilder<*>
    fun secure(request: RequestEntity.BodyBuilder) : RequestEntity.BodyBuilder
}
