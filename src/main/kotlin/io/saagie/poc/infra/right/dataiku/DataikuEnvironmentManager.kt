package io.saagie.poc.infra.right.dataiku

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.infra.right.common.Requester
import io.saagie.poc.infra.right.common.generateBasicAuthKey
import io.saagie.poc.infra.right.common.process
import io.saagie.poc.infra.right.common.securer.BasicSecurer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.net.URI

@Component
@Profile("dataiku")
class DataikuEnvironmentManager(val restTemplate: RestTemplate) : EnvironmentManager {
    // ATTRIBUTES
    @Value("\${dataiku.url}")
    lateinit var url: String

    @Value("\${dataiku.apikey}")
    lateinit var apikey: String

    /**
     * Dataiku is using basic auth, with a simple apikey as username and no password.
     */
    internal val requester = Requester(BasicSecurer(apikey))


    // METHODS
    override fun getProjects() = restTemplate.process(
            request = requester.get<Array<ProjectDTO>>("$url/projects/"),
            verify = { !(it?.isEmpty() ?: true) },
            transform = { it!!.map { it.projectKey }.toList() }
    )

    override fun getJobManager(project: String?) = DataikuJobManager(this, project!!)

    override fun importProject(description: String, target: String) = throw UnsupportedOperationException()

    override fun exportProject(id: String) = restTemplate.process(
            request = requester.get<String>("$url/projects/$id/export"),
            verify = { !(it?.isBlank() ?: true) },
            transform = { it!! }
    )


    // DTOs
    data class ProjectDTO (
            val projectKey: String = ""
    )
}
