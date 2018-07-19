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
            headers: Map<String, List<String>> = mapOf()
    )
            = RequestEntity.get(URI(url)).defineHeaders(headers).build() as RequestEntity<T>

    fun <T> post(
            url: String,
            body: T,
            headers: Map<String, List<String>> = mapOf(),
            mediaType: MediaType = MediaType.APPLICATION_JSON
    )
            = RequestEntity.post(URI(url)).defineHeaders(headers).contentType(mediaType).body(body)

    @Suppress("UNCHECKED_CAST")
    fun post(
            url: String,
            headers: Map<String, List<String>> = mapOf(),
            mediaType: MediaType = MediaType.APPLICATION_JSON
    )
            = RequestEntity.post(URI(url)).defineHeaders(headers).contentType(mediaType).build()

    fun <T> put(
            url: String,
            body: T,
            headers: Map<String, List<String>> = mapOf(),
            mediaType: MediaType = MediaType.APPLICATION_JSON
    )
            = RequestEntity.put(URI(url)).defineHeaders(headers).contentType(mediaType).body(body)


    // TOOLS
    /**
     * defineHeaders has two actions : Completing the request builder with the provided headers,
     * and add a specific "Authorization" header based on the selected Securer instance.
     */
    private fun RequestEntity.HeadersBuilder<*>.defineHeaders(headers: Map<String, List<String>>)
            = securer.secure(
                headers.entries.fold(this) {
                    req, (key, values) -> values.fold(req) {
                        req2, value -> req2.header(key, value)
                    }
                })
    private fun RequestEntity.BodyBuilder.defineHeaders(headers: Map<String, List<String>>)
            = securer.secure(
                headers.entries.fold(this) {
                    req, (key, values) -> values.fold(req) {
                        req2, value -> req2.header(key, value)
                    }
                })
}
