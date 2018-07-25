package io.saagie.poc.infra.right.common

import io.saagie.poc.infra.right.common.securer.NoSecurer
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.util.LinkedMultiValueMap
import java.net.URI
import java.util.*

class AWSRequester(
        val baseUrl: String = "https://glue.us-east-1.amazonaws.com"
) {
    // ATTRIBUTES
    var requester = Requester(NoSecurer())
    /**
     * Date retrieval (Needs to be 2 hours less than as retrieved...)
     */
    var date = Date(Date().time.minus(7200000L))

    // -- Request related
    var method = "POST"
    var service = "glue"
    var region = "us-east-1"
    var host = "$service.$region.amazonaws.com"
    var canonicalUri = "/"
    var requestParameters = ""
    var endpoint = "$baseUrl$canonicalUri?$requestParameters"
    var contentType = "application/x-amz-json-1.1"
    var signedHeaders = "content-type;host;x-amz-date;x-amz-target"
    var body = "{}"

    // -- Credentials related
    var algorithm = "AWS4-HMAC-SHA256"
    var publicKey = "AKIAJUEB6C3GIVV3EESA"
    var secretKey = "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"
    var aws = "aws4_request"


    // METHOD
    fun <T> createRequest(target: String): RequestEntity<T> {
        // Creating signature
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
        return RequestEntity(headers, HttpMethod.resolve(method)!!, URI(endpoint))
    }


    // TOOLS
    // AWS signature with IAM - Step 1
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
}

fun main(args: Array<String>) {
    var requester = AWSRequester()
    var request = requester.createRequest<String>("AWSGlue.GetJobs")

    request.headers.forEach { (key, values) ->
        println("$key: $values")
    }
    println("\n---------\n")

//    requester = AWSRequester().apply {
//        method = "GET"
//        service = "iam"
//        host = "iam.amazonaws.com"
//        requestParameters = "Action=ListUsers&Version=2010-05-08"
//        contentType = "application/x-www-form-urlencoded; charset=utf-8"
//        signedHeaders = "content-type;host;x-amz-date"
//        body = ""
//        date = Date(Date.UTC(115, 7, 30, 10, 36, 0))
//
//        publicKey = "AKIDEXAMPLE"
//        secretKey = "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY"
//    }
//
//    val dateExemple = Date(Date.UTC(115, 7, 30, 10, 36, 0))
//    val txt = requester.createCanonicalRequest(dateExemple, "AWSGlue.GetJobs")
//    println("$txt\n${txt.hashSHA256()}")
//    println("\n---------\n")
//
//    val txt2 = requester.createStringToSign(dateExemple, "")
//    println(txt2)
//    println("\n---------\n")
//
//    val txt3 = requester.createSigningKey(dateExemple, "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")
//    println(txt3.toHexa())
//    println("\n---------\n")
//
//    val signature = txt2.sign(txt3)
//    println(signature.toHexa())
//    println("\n---------\n")
//
//    request = requester.createRequest<String>("AWSGlue.GetJobs")
//    request.headers.forEach { (key, values) -> println("$key: $values") }
}



