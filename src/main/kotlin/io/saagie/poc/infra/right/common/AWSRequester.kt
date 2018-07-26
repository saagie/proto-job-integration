package io.saagie.poc.infra.right.common

import io.saagie.poc.infra.AppProperties
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.util.LinkedMultiValueMap
import java.net.URI
import java.util.*

class AWSRequester(private val appProperties: AppProperties) {
    // ATTRIBUTES
    // -- Request related
    private val service = appProperties.aws.service
    private val region = appProperties.aws.region
    private val host = "$service.$region.amazonaws.com"
    private val canonicalUri = "/"
    private val contentType = "application/x-amz-json-1.1"
    private val signedHeaders = "content-type;host;x-amz-date;x-amz-target"

    // -- Credentials related
    private val algorithm = appProperties.aws.algorithm
    private val publicKey = appProperties.aws.publicKey
    private val secretKey = appProperties.aws.secretKey
    private val aws = "aws4_request"


    // METHOD
    fun get(target: String, requestParameters: String = "") = createRequest(
            method = "GET",
            target = target,
            requestParameters = requestParameters
    )

    fun post(target: String, body: String = "{}", requestParameters: String = "") = createRequest(
            method = "POST",
            target = target,
            body = body,
            requestParameters = requestParameters
    )

    private fun createRequest(method: String, target: String, body: String = "", requestParameters: String = ""): RequestEntity<String> {
        val date = Date(Date().time.minus(7200000L))

        // AWS signature with IAM - Step 1 (cf. Webdoc.)
        fun createCanonicalRequest(date: Date, target: String) = listOf(
                method,
                canonicalUri,
                requestParameters,
                "content-type:$contentType\nhost:$host\nx-amz-date:${date.toFullFormat()}\nx-amz-target:$target\n",
                signedHeaders,
                body.hash()
        ).concat("\n")

        // AWS signature with IAM - Step 2
        fun createStringToSign(date: Date, target: String) = listOf(
                algorithm,
                date.toFullFormat(),
                "${date.toSimpleFormat()}/$region/$service/$aws",
                createCanonicalRequest(date, target).hash()
        ).concat("\n")

        // AWS signature with IAM - Step 3
        fun createSigningKey(date: Date, privateKey: String) = listOf(
                date.toSimpleFormat(),
                region,
                service,
                aws
        ).fold("AWS4$privateKey".getBytes()) {
            key, term -> term.sign(key)
        }

        // AWS signature with IAM - Step 4
        val signature = createStringToSign(date, target).sign(createSigningKey(date, secretKey)).toHexa()
        val credentialScope = listOf(date.toSimpleFormat(), region, service, aws).concat("/")

        // Defining headers
        val headers = mapOf(
                "x-amz-date" to listOf(date.toFullFormat()),
                "x-amz-target" to listOf(target),
                "Content-Type" to listOf(contentType),
                "Authorization" to listOf("$algorithm Credential=$publicKey/$credentialScope, SignedHeaders=$signedHeaders, Signature=$signature")
        ).entries.fold(LinkedMultiValueMap<String, String>()) { map, (key, values) ->
            map[key] = values
            map
        }

        // Returning associated request
        return RequestEntity(
                body,
                headers,
                HttpMethod.resolve(method)!!,
                URI("${appProperties.aws.url}$canonicalUri?$requestParameters")
        )
    }
}



