package io.saagie.poc.infra.right.knime

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.infra.right.common.generateBasicAuthKey
import io.saagie.poc.infra.right.common.process
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.net.URI

@Component
@Profile("knime")
class KnimeEnvironmentManager(val restTemplate: RestTemplate) : EnvironmentManager {
    companion object {
        val logger = LoggerFactory.getLogger(KnimeEnvironmentManager::class.java)
    }

    // ATTRIBUTES
    @Value("\${knime.url}")
    lateinit var url: String

    @Value("\${knime.username}")
    lateinit var username: String

    @Value("\${knime.password}")
    lateinit var password: String


    // METHODS
    @Suppress("UNCHECKED_CAST")
    override fun getProjects() = restTemplate.process(
            request = RequestEntity.get(URI("${url}/repository/"))
                    .header("Authorization", "Basic ${generateAuthKey()}")
                    .build() as RequestEntity<RepositoryDTO>,
            verify = { !(it?.children?.isEmpty() ?: true) },
            transform = { it!!.children.map { it.path } }
    )

    override fun getJobManager(project: String?) = KnimeJobManager(this, project!!)


    // TOOLS
    /**
     * Knime is using basic auth.
     */
    fun generateAuthKey() = generateBasicAuthKey(username, password)


    // DTOs
    data class ProjectDTO (
            val path: String = ""
    )
    data class RepositoryDTO (
            val children: Array<ProjectDTO> = arrayOf()
    )
}
