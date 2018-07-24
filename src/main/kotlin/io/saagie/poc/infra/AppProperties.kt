package io.saagie.poc.infra

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(ignoreInvalidFields = true)
data class AppProperties(
        val dataiku: URLProperty = URLProperty(),
        val knime: URLProperty = URLProperty(),
        val trifacta: URLProperty = URLProperty(),
        val dsx: URLProperty = URLProperty(),
        val nifi: URLProperty = URLProperty(),
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
