package io.saagie.poc.infra.right.knime

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.infra.AppProperties
import io.saagie.poc.infra.right.common.Requester
import io.saagie.poc.infra.right.common.process
import io.saagie.poc.infra.right.common.securer.BasicSecurer
import io.saagie.poc.infra.right.common.toProperURL
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException

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
    override fun getProjects(): Collection<String> {
        // Making the request to the KNIME API
        fun requestProject(name: String) = restTemplate.process(
                request = requester.get<RepositoryDTO>("$url/repository$name".toProperURL()),
                verify = { !(it?.children?.isEmpty() ?: true) },
                transform = { it!! }
        )

        // Gather all subprojects from project tree, one by one.
        fun recursiveSearch(name: String): Collection<String>  {
            var children = listOf<String>()
            try {
                val dto = requestProject(name)

                children = dto.children!!
                        .filter { it.type == "Workflow" || it.type == "WorkflowGroup" }
                        .map { it.path }
                children = children.fold(children.toSet()) {
                    set, child -> set + recursiveSearch(child)
                }.toList()
            }
            catch (exc: ResponseStatusException) {
                if (exc.status != HttpStatus.NO_CONTENT) throw exc
            }
            return children
        }

        return recursiveSearch("/").sorted()
    }

    override fun getJobManager(project: String?) = KnimeJobManager(this, project!!)

    override fun importProject(description: String, target: String) = throw UnsupportedOperationException()
    override fun exportProject(id: String)= throw UnsupportedOperationException()


    // DTOs
    data class ProjectDTO (
            val path: String = "",
            val type: String = ""
    )
    data class RepositoryDTO (
            val children: Array<ProjectDTO>? = null
    )
}
