package io.saagie.poc.infra

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(ignoreInvalidFields = true)
data class AppProperties(
        val dataiku: URLProperty = URLProperty(),
        val knime: URLProperty = URLProperty(),
        val trifacta: URLProperty = URLProperty(),
        val dsx: URLProperty = URLProperty(),
        val nifi: URLProperty = URLProperty(),
        val glue: URLProperty = URLProperty(),
        val aws: AWSProperties = AWSProperties(),
        val common: CommonProperties = CommonProperties()
)

class URLProperty {
    var url: String = ""
}

class CommonProperties {
    var username: String = ""
    var password: String = ""
    var tokenUrl: String = ""
    var project: String = ""
    var job: String = ""
}

class AWSProperties {
    var url = "https://glue.us-east-1.amazonaws.com"
    var service = "glue"
    var region = "us-east-1"
    var algorithm = "AWS4-HMAC-SHA256"
    var publicKey = "AKIAJUEB6C3GIVV3EESA"
    var secretKey = "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"
}
