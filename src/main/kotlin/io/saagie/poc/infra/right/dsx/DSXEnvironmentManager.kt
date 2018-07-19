package io.saagie.poc.infra.right.dsx

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.Project
import io.saagie.poc.infra.AppProperties
import io.saagie.poc.infra.right.common.Requester
import io.saagie.poc.infra.right.common.process
import io.saagie.poc.infra.right.common.securer.TokenSecurer
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@Profile("dsx")
class DSXEnvironmentManager(val restTemplate: RestTemplate, private val properties: AppProperties) : EnvironmentManager {
    // ATTRIBUTES
    internal val url = properties.dsx.url

    /**
     * DSX is using a token based auth, but the authentification request
     * is currently using basic auth. to retrieve the token.
     */
    internal val requester = Requester(TokenSecurer<TokenDTO>(
            username = properties.dsx.username,
            password = properties.dsx.password,
            tokenUrl = "$url/v1/preauth/validateAuth",
            tokenDTO = TokenDTO::class.java,
            tokenExtractor = { it.accessToken },
            restTemplate = restTemplate
    ))


    // METHODS
    @Suppress("UNCHECKED_CAST")
    override fun getProjects() = restTemplate.process(
            request = requester.get<Array<String>>("${url}/api/v2/filemgmt/view/"),
            verify = { !(it?.isEmpty() ?: true) },
            transform = { it!!.filter { it.reversed().startsWith("/") }.map(::Project)}
    )

    override fun getJobManager(project: Project?): JobManager = DSXJobManager(this, project!!.id)

    override fun importProject(description: String, target: String) = throw UnsupportedOperationException()

    override fun exportProject(project: Project): String = throw UnsupportedOperationException()


    // DTOs
    data class TokenDTO (
            val accessToken: String = ""
    )

    data class ProjectDTO (
            val projectKey: String = ""
    )
}
