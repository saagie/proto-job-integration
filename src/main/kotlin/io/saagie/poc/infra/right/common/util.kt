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
 * Transforms a sentence into an URL-ready String.
 */
fun String.correctURL() = this.replace(" ", "%20")


/**
 * Provide a simple implementation of a backtracking algorithm.
 * This function will retrieve all elements form a tree-like structures by following
 * the instructions provided by the 'operation' argument.
 */
fun <T> backtrackSearch(initial: T, operation: (T) -> Set<T>): Set<T> =
        operation(initial).fold(setOf(initial)) {
            set, child -> set + backtrackSearch(child, operation)
        }

/**
 * Process the incoming request, checks the response's body (if there's any),
 * and transforms it, into a more valuable result.
 */
fun <T, R> RestTemplate.process(
        request: RequestEntity<T>,
        returnType: Class<T>,
        verify: (T) -> Boolean = { true },
        transform: (T) -> R
) : R {
    // Processing request
    val response = this.exchange(request, returnType)

    // Checking response status and body
    if (!response.statusCode.is2xxSuccessful) {
        throw ResponseStatusException(response.statusCode)
    }
    if (response.body == null || !verify(response.body!!)) {
        throw ResponseStatusException(HttpStatus.NO_CONTENT)
    }

    // Creating expected result
    return transform(response.body!!)
}

inline fun <reified T, R> RestTemplate.process(
        request: RequestEntity<T>,
        noinline verify: (T) -> Boolean = { true },
        noinline transform: (T) -> R
) = this.process(request, T::class.java, verify, transform)

inline fun <reified T> RestTemplate.process(
        request: RequestEntity<T>,
        noinline verify: (T) -> Boolean = { true }
) = this.process(request, verify) { it }

/**
 * Process the incoming request without any transformation on the response's body.
 * (Pretty useful for POST, PUT, DELETE methods...)
 */
inline fun <reified T> RestTemplate.execute(request: RequestEntity<T>, noinline verify: (T) -> Boolean = { true })
        = this.process(request, verify) {}
