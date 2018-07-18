package io.saagie.poc.infra.right.common

import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import java.util.*

/**
 * Encode a given text into a Base64 String
 */
fun encode64(txt: String) = String(
        Base64.getEncoder().encode(txt.toByteArray())
)

/**
 * Generate the header key for basic authentification
 * based on an username and a password.
 */
fun generateBasicAuthKey(username: String, password: String = "") = encode64("$username:$password")

/**
 * Process the incoming request, checks the response's body (if there's any),
 * and transforms it, into a more valuable result.
 */
fun <T, R> RestTemplate.process(
        request: RequestEntity<T>,
        returnType: Class<T>,
        transform: (T?) -> R,
        verify: (T?) -> Boolean = { true }
) : R {
    // Processing request
    val response = this.exchange(request, returnType)

    // Checking response status and body
    if (!response.statusCode.is2xxSuccessful) {
        throw ResponseStatusException(response.statusCode)
    }
    if (!verify(response.body)) {
        throw ResponseStatusException(HttpStatus.NO_CONTENT)
    }

    // Creating expected result
    return transform(response.body)
}

inline fun <reified T, R> RestTemplate.process(
        request: RequestEntity<T>,
        noinline transform: (T?) -> R,
        noinline verify: (T?) -> Boolean = { true }
) = this.process(request, T::class.java, transform, verify)

/**
 * Process the incoming request without any transformation on the response's body.
 * (Pretty useful for POST, PUT, DELETE methods...)
 */
inline fun <reified T> RestTemplate.process(request: RequestEntity<T>, noinline verify: (T?) -> Boolean = { true })
        = this.process(request, {}, verify)


/**
 * Transforms a sentence into an URK-ready String.
 */
fun String.toProperURL() = this.replace(" ", "%20")
