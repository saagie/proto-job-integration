package io.saagie.poc.infra.right.knime

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.infra.right.common.generateBasicAuthKey
import io.saagie.poc.infra.right.common.process
import io.saagie.poc.infra.right.common.toProperURL
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import java.net.URI

@Component
@Profile("knime")
class KnimeEnvironmentManager(val restTemplate: RestTemplate) : EnvironmentManager {
    // ATTRIBUTES
    @Value("\${knime.url}")
    lateinit var url: String

    @Value("\${knime.username}")
    lateinit var username: String

    @Value("\${knime.password}")
    lateinit var password: String


    // METHODS
    @Suppress("UNCHECKED_CAST")
    override fun getProjects(): Collection<String> {
        // Making the request to the KNIME API
        fun requestProject(name: String) = restTemplate.process(
                request = RequestEntity.get(URI("$url/repository$name".toProperURL()))
                        .header("Authorization", "Basic ${generateAuthKey()}")
                        .build() as RequestEntity<RepositoryDTO>,
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


    // TOOLS
    /**
     * Knime is using basic auth.
     */
    internal fun generateAuthKey() = generateBasicAuthKey(username, password)


    // DTOs
    data class ProjectDTO (
            val path: String = "",
            val type: String = ""
    )
    data class RepositoryDTO (
            val children: Array<ProjectDTO>? = null
    )
}
