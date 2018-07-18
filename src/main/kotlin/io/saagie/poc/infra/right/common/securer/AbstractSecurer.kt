package io.saagie.poc.infra.right.common.securer

import org.springframework.http.RequestEntity

abstract class AbstractSecurer: Securer {
    // METHODS
    override fun secure(request: RequestEntity.HeadersBuilder<*>) : RequestEntity.HeadersBuilder<*> =
            request.header("Authorization", getAuthorization())
    override fun secure(request: RequestEntity.BodyBuilder) : RequestEntity.BodyBuilder =
            request.header("Authorization", getAuthorization())

    // TOOL
    abstract fun getAuthorization(): String
}
