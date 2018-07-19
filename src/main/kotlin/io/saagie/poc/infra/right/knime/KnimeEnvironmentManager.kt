package io.saagie.poc.infra.right.knime

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.Project
import io.saagie.poc.infra.AppProperties
import io.saagie.poc.infra.right.common.Requester
import io.saagie.poc.infra.right.common.backtrackSearch
import io.saagie.poc.infra.right.common.process
import io.saagie.poc.infra.right.common.securer.BasicSecurer
import io.saagie.poc.infra.right.common.correctURL
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@Profile("knime")
class KnimeEnvironmentManager(val restTemplate: RestTemplate, private val properties: AppProperties) : EnvironmentManager {
    // ATTRIBUTES
    internal val url = properties.knime.url
    internal val requester = Requester(
            BasicSecurer(properties.knime.username, properties.knime.password)
    )


    // METHODS
    @Suppress("UNCHECKED_CAST")
    override fun getProjects(): Collection<Project> {
        // Making the request to the KNIME API
        fun getProjectInfos(name: String) = restTemplate.process(
                request = requester.get<RepositoryDTO>("$url/repository$name".correctURL()),
                verify = { it != null },
                transform = { it!!.children }
        )

        // Researching all project with a backtracking algortihm.
        return backtrackSearch("/") {
            getProjectInfos(it)
                    .filter { it.type == "Workflow" || it.type == "WorkflowGroup" }
                    .map { it.path }
                    .toSet()
        }.minus("/").map(::Project)
    }

    override fun getJobManager(project: Project?): JobManager = KnimeJobManager(this, project!!.id)

    override fun importProject(description: String, target: String) = throw UnsupportedOperationException()
    override fun exportProject(project: Project): String = throw UnsupportedOperationException()


    // DTOs
    data class ProjectDTO (
            val path: String = "",
            val type: String = ""
    )
    data class RepositoryDTO (
            val children: Array<ProjectDTO> = arrayOf()
    )
}
