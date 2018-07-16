package io.saagie.poc.infra.right.dsx

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.domain.JobManager
import io.saagie.poc.infra.right.common.generateBasicAuthKey
import io.saagie.poc.infra.right.common.process
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Profile
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.net.URI

@Component
@Profile("dsx")
class DSXEnvironmentManager(val restTemplate: RestTemplate) : EnvironmentManager {
    // ATTRIBUTES
    @Value("\${dsx.url}")
    lateinit var url: String

    @Value("\${dsx.username}")
    lateinit var username: String

    @Value("\${dsx.password}")
    lateinit var password: String


    // METHODS
    @Suppress("UNCHECKED_CAST")
    override fun getProjects() = restTemplate.process(
            request = RequestEntity.get(URI("${url}/api/v2/filemgmt/view/"))
                    .header("Authorization", "Bearer ${getToken()}")
                    .build() as RequestEntity<Array<String>>,
            verify = { !(it?.isEmpty() ?: true) },
            transform = { it!!.filter { it.reversed().startsWith("/") }}
    )

    override fun getJobManager(project: String?) = DSXJobManager(this, project!!)

    override fun importProject(description: String, target: String) = throw UnsupportedOperationException()

    override fun exportProject(id: String) = throw UnsupportedOperationException()


    // TOOLS
    @Suppress("UNCHECKED_CAST")
    @Cacheable("dsxTokenCache", sync = true)
    internal fun getToken() = restTemplate.process(
            request = RequestEntity.get(URI("${url}/v1/preauth/validateAuth"))
                    .header("Authorization", "Basic ${generateAuthKey()}")
                    .build() as RequestEntity<TokenDTO>,

            verify = { it != null },
            transform = { it!!.accessToken }
    )

    /**
     * DSX is using a token based auth, but the authentification request
     * is currently using basic auth. to retrieve the token.
     */
    private fun generateAuthKey() = generateBasicAuthKey(username, password)


    // DTOs
    data class TokenDTO (
            val accessToken: String = ""
    )

    data class ProjectDTO (
            val projectKey: String = ""
    )
}
