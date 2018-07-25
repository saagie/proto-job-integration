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
    var service = appProperties.aws.service
    var region = appProperties.aws.region
    var host = "$service.$region.amazonaws.com"
    var canonicalUri = "/"
    var contentType = "application/x-amz-json-1.1"
    var signedHeaders = "content-type;host;x-amz-date;x-amz-target"

    // -- Credentials related
    var algorithm = appProperties.aws.algorithm
    var publicKey = appProperties.aws.publicKey
    var secretKey = appProperties.aws.secretKey
    private val aws = "aws4_request"


    // METHOD
    fun <T> get(target: String, body: String = "", requestParameters: String = "") = createRequest<T>("GET", target, body, requestParameters)
    fun <T> post(target: String, body: String = "{}", requestParameters: String = "") = createRequest<T>("POST", target, body, requestParameters)

    fun <T> createRequest(method: String, target: String, body: String = "", requestParameters: String = ""): RequestEntity<T> {
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
                headers,
                HttpMethod.resolve(method)!!,
                URI("${appProperties.aws.url}$canonicalUri?$requestParameters")
        )
    }
}

fun main(args: Array<String>) {
    var requester = AWSRequester(AppProperties())
    requester.post<String>("AWSGlue.GetJobs").headers.forEach {
        (key, values) -> println("$key: $values")
    }
    println("\n---------\n")


    requester.post<String>(target = "AWSGlue.GetJob", body = "{\"JobName\":\"Flights Conversion\"}").headers.forEach {
        (key, values) -> println("$key: $values")
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



