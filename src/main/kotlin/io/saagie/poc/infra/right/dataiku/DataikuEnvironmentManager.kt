package io.saagie.poc.infra.right.dataiku

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.Project
import io.saagie.poc.infra.AppProperties
import io.saagie.poc.infra.right.common.Requester
import io.saagie.poc.infra.right.common.process
import io.saagie.poc.infra.right.common.securer.BasicSecurer
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@Profile("dataiku")
class DataikuEnvironmentManager(val restTemplate: RestTemplate, private val properties: AppProperties) : EnvironmentManager {
    // ATTRIBUTES
    internal val url = properties.dataiku.url

    /**
     * Dataiku is using basic auth, with a simple apikey as username and no password.
     */
    internal val requester = Requester(BasicSecurer(properties.dataiku.apikey))


    // METHODS
    override fun getProjects() = restTemplate.process(
            request = requester.get<Array<ProjectDTO>>("$url/projects/"),
            verify = { !(it?.isEmpty() ?: true) },
            transform = { it!!.map(::toProject).toList() }
    )

    override fun getJobManager(project: Project?): JobManager = DataikuJobManager(this, project!!.id)

    override fun importProject(description: String, target: String) = throw UnsupportedOperationException()

    override fun exportProject(project: Project): String = restTemplate.process(
            request = requester.get<String>("$url/projects/${project.id}/export"),
            verify = { !(it?.isBlank() ?: true) },
            transform = { it!! }
    )


    // TOOL
    fun toProject(dto: ProjectDTO) = Project(dto.projectKey)


    // DTOs
    data class ProjectDTO (
            val projectKey: String = ""
    )
}
