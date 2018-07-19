package io.saagie.poc.infra.right.common

import io.saagie.poc.infra.right.common.securer.BasicSecurer
import io.saagie.poc.infra.right.common.securer.TokenSecurer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.net.URI

class RequesterTest {
    // ATTRIBUTES
    private val url = "http://itsatesturl:8080"
    private val tokenUrl = "http://itsatesttokenurl:8080"
    private val token = "itsanunpredictabletoken"
    private val username = "admin"
    private val password = "admin"

    private val requester = Requester()
    private lateinit var basicRequester: Requester
    private lateinit var tokenRequester: Requester
    private lateinit var restTemplate: RestTemplate


    // METHODS
    @Before
    fun before() {
        // A service to mock token retrieval
        restTemplate = mock(RestTemplate::class.java)
        given(restTemplate.exchange(Mockito.any(), eq(String::class.java))).willReturn(ResponseEntity(token, HttpStatus.OK))


        // Specific requesters definition
        basicRequester = Requester(BasicSecurer(username, password))
        tokenRequester = Requester(TokenSecurer(
                username = username,
                password = password,
                tokenUrl = tokenUrl,
                tokenDTO = String::class.java,
                tokenExtractor = { it },
                restTemplate = restTemplate
        ))
    }


    @Test
    fun `'get' method create an appropriate request for a given url`() {
        // Given
        // When
        val request = requester.get<String>(url)

        // Then
        assertThat(request.url).isEqualTo(URI(url))
        assertThat(request.method).isEqualTo(HttpMethod.GET)
        assertThat(request.headers).isEmpty()
    }

    @Test
    fun `'get' method correctly pass all headers given in argument`() {
        // Given
        val headers = mapOf(
                "key1" to listOf("value1"), "key2" to listOf("value2a", "value2b")
        )

        // When
        val request = requester.get<String>(url, headers)

        // Then
        assertThat(request.url).isEqualTo(URI(url))
        assertThat(request.method).isEqualTo(HttpMethod.GET)
        headers.forEach { (key, value) ->
            assertThat(request.headers[key]).isEqualTo(value)
        }
    }

    @Test
    fun `'get' method with Basic Auth is producing a coherent header`() {
        // Given
        val headers = mapOf(
                "key1" to listOf("value1")
        )
        val authExpected = "Authorization" to (listOf("Basic ${encode64("$username:$password")}"))

        // When
        val request = basicRequester.get<String>(url, headers)

        // Then
        assertThat(request.url).isEqualTo(URI(url))
        assertThat(request.method).isEqualTo(HttpMethod.GET)

        (headers + authExpected).forEach { (key, value) ->
            assertThat(request.headers[key]).isEqualTo(value)
        }
    }

    @Test
    fun `'get' method with Token Auth is producing a coherent header`() {
        // Given
        val headers = mapOf(
                "key1" to listOf("value1")
        )
        val authExpected = Pair("Authorization", listOf("Bearer $token"))

        // When
        val request = tokenRequester.get<String>(url, headers)

        // Then
        assertThat(request.url).isEqualTo(URI(url))
        assertThat(request.method).isEqualTo(HttpMethod.GET)

        (headers + authExpected).forEach { (key, value) ->
            assertThat(request.headers[key]).isEqualTo(value)
        }
    }

    @Test
    fun `'post' method create an appropriate request for a given url`() {
        // Given
        val body = "body"
        val expectedHeaders = mapOf(
               "Content-Type" to listOf(MediaType.APPLICATION_JSON.toString())
        )

        // When
        val request = requester.post(url, body)

        // Then
        assertThat(request.url).isEqualTo(URI(url))
        assertThat(request.method).isEqualTo(HttpMethod.POST)
        assertThat(request.body).isNotNull()
        assertThat(request.body).isEqualTo(body)

        expectedHeaders.forEach { (key, value) ->
            assertThat(request.headers[key]).isEqualTo(value)
        }
    }

    @Test
    fun `'post' method create an appropriate request when no body is given`() {
        // Given
        // When
        val request = requester.post(url)

        // Then
        assertThat(request.url).isEqualTo(URI(url))
        assertThat(request.method).isEqualTo(HttpMethod.POST)
        assertThat(request.hasBody()).isFalse()
    }

    @Test
    fun `'post' method correctly pass all headers given in argument`() {
        // Given
        val body = "body"
        val headers = mapOf(
                "key1" to listOf("value1"), "key2" to listOf("value2a", "value2b")
        )
        val expectedHeaders = headers + ("Content-Type" to listOf(MediaType.APPLICATION_JSON.toString()))

        // When
        val request = requester.post(url, body, headers)

        // Then
        assertThat(request.url).isEqualTo(URI(url))
        assertThat(request.method).isEqualTo(HttpMethod.POST)
        assertThat(request.body).isNotNull()
        assertThat(request.body).isEqualTo(body)

        expectedHeaders.forEach { (key, value) ->
            assertThat(request.headers[key]).isEqualTo(value)
        }
    }

    @Test
    fun `'post' method correctly pass specific media type`() {
        // Given
        val body = "body"
        val mediaType = MediaType.APPLICATION_PDF
        val expectedHeaders = mapOf("Content-Type" to listOf(mediaType.toString()))

        // When
        val request = requester.post(
                url = url,
                body = body,
                mediaType = mediaType
        )

        // Then
        assertThat(request.url).isEqualTo(URI(url))
        assertThat(request.method).isEqualTo(HttpMethod.POST)
        assertThat(request.body).isNotNull()
        assertThat(request.body).isEqualTo(body)

        expectedHeaders.forEach { (key, value) ->
            assertThat(request.headers[key]).isEqualTo(value)
        }
    }

    @Test
    fun `'post' method with Basic Auth is producing a coherent header`() {
        // Given
        val body = "body"
        val headers = mapOf(
                "key1" to listOf("value1")
        )
        val expectedHeaders = headers + mapOf(
                "Content-Type" to listOf(MediaType.APPLICATION_JSON.toString()),
                "Authorization" to listOf("Basic ${encode64("$username:$password")}")
        )

        // When
        val request = basicRequester.post(url, body, headers)

        // Then
        assertThat(request.url).isEqualTo(URI(url))
        assertThat(request.method).isEqualTo(HttpMethod.POST)
        assertThat(request.body).isNotNull()
        assertThat(request.body).isEqualTo(body)

        expectedHeaders.forEach { (key, value) ->
            assertThat(request.headers[key]).isEqualTo(value)
        }
    }

    @Test
    fun `'post' method with Token Auth is producing a coherent header`() {
        // Given
        val body = "body"
        val headers = mapOf(
                "key1" to listOf("value1")
        )
        val expectedHeaders = headers + mapOf(
                "Content-Type" to listOf(MediaType.APPLICATION_JSON.toString()),
                "Authorization" to listOf("Bearer $token")
        )

        // When
        val request = tokenRequester.post(url, body, headers)

        // Then
        assertThat(request.url).isEqualTo(URI(url))
        assertThat(request.method).isEqualTo(HttpMethod.POST)
        assertThat(request.body).isNotNull()
        assertThat(request.body).isEqualTo(body)

        expectedHeaders.forEach { (key, value) ->
            assertThat(request.headers[key]).isEqualTo(value)
        }
    }

    @Test
    fun `'put' method create an appropriate request for a given url`() {
        // Given
        val body = "body"
        val expectedHeaders = mapOf(
                "Content-Type" to listOf(MediaType.APPLICATION_JSON.toString())
        )

        // When
        val request = requester.put(url, body)

        // Then
        assertThat(request.url).isEqualTo(URI(url))
        assertThat(request.method).isEqualTo(HttpMethod.PUT)
        assertThat(request.body).isNotNull()
        assertThat(request.body).isEqualTo(body)

        expectedHeaders.forEach { (key, value) ->
            assertThat(request.headers[key]).isEqualTo(value)
        }
    }

    @Test
    fun `'put' method correctly pass all headers given in argument`() {
        // Given
        val body = "body"
        val headers = mapOf(
                "key1" to listOf("value1"), "key2" to listOf("value2a", "value2b")
        )
        val expectedHeaders = headers + ("Content-Type" to listOf(MediaType.APPLICATION_JSON.toString()))

        // When
        val request = requester.put(url, body, headers)

        // Then
        assertThat(request.url).isEqualTo(URI(url))
        assertThat(request.method).isEqualTo(HttpMethod.PUT)
        assertThat(request.body).isNotNull()
        assertThat(request.body).isEqualTo(body)

        expectedHeaders.forEach { (key, value) ->
            assertThat(request.headers[key]).isEqualTo(value)
        }
    }

    @Test
    fun `'put' method correctly pass specific media type`() {
        // Given
        val body = "body"
        val mediaType = MediaType.APPLICATION_PDF
        val expectedHeaders = mapOf("Content-Type" to listOf(mediaType.toString()))

        // When
        val request = requester.put(
                url = url,
                body = body,
                mediaType = mediaType
        )

        // Then
        assertThat(request.url).isEqualTo(URI(url))
        assertThat(request.method).isEqualTo(HttpMethod.PUT)
        assertThat(request.body).isNotNull()
        assertThat(request.body).isEqualTo(body)

        expectedHeaders.forEach { (key, value) ->
            assertThat(request.headers[key]).isEqualTo(value)
        }
    }

    @Test
    fun `'put' method with Basic Auth is producing a coherent header`() {
        // Given
        val body = "body"
        val headers = mapOf(
                "key1" to listOf("value1")
        )
        val expectedHeaders = headers + mapOf(
                "Content-Type" to listOf(MediaType.APPLICATION_JSON.toString()),
                "Authorization" to listOf("Basic ${encode64("$username:$password")}")
        )

        // When
        val request = basicRequester.put(url, body, headers)

        // Then
        assertThat(request.url).isEqualTo(URI(url))
        assertThat(request.method).isEqualTo(HttpMethod.PUT)
        assertThat(request.body).isNotNull()
        assertThat(request.body).isEqualTo(body)

        expectedHeaders.forEach { (key, value) ->
            assertThat(request.headers[key]).isEqualTo(value)
        }
    }

    @Test
    fun `'put' method with Token Auth is producing a coherent header`() {
        // Given
        val body = "body"
        val headers = mapOf(
                "key1" to listOf("value1")
        )
        val expectedHeaders = headers + mapOf(
                "Content-Type" to listOf(MediaType.APPLICATION_JSON.toString()),
                "Authorization" to listOf("Bearer $token")
        )

        // When
        val request = tokenRequester.put(url, body, headers)

        // Then
        assertThat(request.url).isEqualTo(URI(url))
        assertThat(request.method).isEqualTo(HttpMethod.PUT)
        assertThat(request.body).isNotNull()
        assertThat(request.body).isEqualTo(body)

        expectedHeaders.forEach { (key, value) ->
            assertThat(request.headers[key]).isEqualTo(value)
        }
    }
}
