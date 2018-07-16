package io.saagie.poc.infra.right.dataiku

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.infra.right.common.generateBasicAuthKey
import io.saagie.poc.infra.right.common.process
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


    // METHODS
    @Suppress("UNCHECKED_CAST")
    override fun getProjects() = restTemplate.process(
            request = RequestEntity.get(URI("$url/projects/"))
            .header("Authorization", "Basic ${generateAuthKey()}")
            .build() as RequestEntity<Array<ProjectDTO>>,

            verify = { !(it?.isEmpty() ?: true) },
            transform = { it!!.map { it.projectKey }.toList() }
    )

    override fun getJobManager(project: String?) = DataikuJobManager(this, project!!)

    override fun importProject(description: String, target: String) = throw UnsupportedOperationException()

    @Suppress("UNCHECKED_CAST")
    override fun exportProject(id: String) = restTemplate.process(
            request = RequestEntity.get(URI("$url/projects/$id/export"))
                    .header("Authorization", "Basic ${generateAuthKey()}")
                    .build() as RequestEntity<String>,

            verify = { !(it?.isBlank() ?: true) },
            transform = { it!! }
    )


    // TOOLS
    /**
     * Dataiku is using basic auth, with a simple apikey as username and no password.
     */
    internal fun generateAuthKey() = generateBasicAuthKey(apikey)


    // DTOs
    data class ProjectDTO (
            val projectKey: String = ""
    )
}
