package io.saagie.poc.infra.right.common

import io.saagie.poc.infra.right.common.securer.Securer
import io.saagie.poc.infra.right.common.securer.NoSecurer
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import java.net.URI

class Requester(private val securer: Securer = NoSecurer()) {
    // METHODS
    @Suppress("UNCHECKED_CAST")
    fun <T> get(
            url: String,
            headers: Map<String, String> = mapOf()
    )
            = RequestEntity.get(URI(url)).defineHeaders(headers).build() as RequestEntity<T>

    @Suppress("UNCHECKED_CAST")
    fun <T> post(
            url: String,
            body: T,
            headers: Map<String, String> = mapOf(),
            mediaType: MediaType = MediaType.APPLICATION_JSON
    )
            = RequestEntity.post(URI(url)).defineHeaders(headers).contentType(mediaType).body(body)


    // TOOLS
    /**
     * defineHeaders has two actions : Completing the request builder with the provided headers,
     * and add a specific "Authorization" header based on the selected Securer instance.
     */
    private fun RequestEntity.HeadersBuilder<*>.defineHeaders(headers: Map<String, String>)
            = securer.secure(
                headers.entries.fold(this) {
                    req, (key, value) -> req.header(key, value)
                })
    private fun RequestEntity.BodyBuilder.defineHeaders(headers: Map<String, String>)
            = securer.secure(
                headers.entries.fold(this) {
                    req, (key, value) -> req.header(key, value)
                })
}
