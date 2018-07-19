package io.saagie.poc.infra

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(ignoreInvalidFields = true)
data class AppProperties(
        val dataiku: DataikuProperties = DataikuProperties(),
        val knime: DefaultProperties = DefaultProperties(),
        val trifacta: DefaultProperties = DefaultProperties(),
        val dsx: DefaultProperties = DefaultProperties(),
        val nifi: NifiProperties = NifiProperties(),
        val commonProperties: CommonProperties = CommonProperties()
)

class DataikuProperties {
    lateinit var url: String
    lateinit var apikey: String
}
class DefaultProperties {
    lateinit var url: String
    lateinit var username: String
    lateinit var password: String
}
class CommonProperties {
    lateinit var username: String
    lateinit var password: String
    lateinit var project: String
    lateinit var job: String
}
class NifiProperties {
    lateinit var url: String
}
