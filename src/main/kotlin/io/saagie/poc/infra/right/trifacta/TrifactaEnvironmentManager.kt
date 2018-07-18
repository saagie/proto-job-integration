package io.saagie.poc.infra.right.trifacta

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.infra.right.common.Requester
import io.saagie.poc.infra.right.common.securer.BasicSecurer
import io.saagie.poc.infra.right.trifacta.TrifactaEnvironmentManager.companion.DEFAULT_PROJECT_NAME
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@Profile("trifacta")
class TrifactaEnvironmentManager(val restTemplate: RestTemplate) : EnvironmentManager {
    object companion {
        val DEFAULT_PROJECT_NAME = "DEFAULT" // Projects doesn't exist in Trifacta for now...
    }

    // ATTRIBUTES
    @Value("\${trifacta.url}")
    lateinit var url: String

    @Value("\${trifacta.username}")
    lateinit var username: String

    @Value("\${trifacta.password}")
    lateinit var password: String

    internal val requester = Requester(BasicSecurer(username, password))


    // METHODS
    override fun getProjects() = listOf(DEFAULT_PROJECT_NAME)

    override fun getJobManager(project: String?) = TrifactaJobManager(this)

    override fun importProject(description: String, target: String) = throw UnsupportedOperationException()

    override fun exportProject(id: String)= throw UnsupportedOperationException()
}
