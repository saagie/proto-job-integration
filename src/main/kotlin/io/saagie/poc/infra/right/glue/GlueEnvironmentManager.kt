package io.saagie.poc.infra.right.glue

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.domain.Project
import io.saagie.poc.infra.AppProperties
import io.saagie.poc.infra.right.common.AWSRequester
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@Profile("glue")
class GlueEnvironmentManager(val restTemplate: RestTemplate, private val properties: AppProperties) : EnvironmentManager {
    // ATTRIBUTES
    internal val url = properties.glue.url
    internal val awsRequester = AWSRequester(properties)


    // METHODS
    override fun getProjects(): Collection<Project> = listOf(Project("DEFAULT"))

    override fun getJobManager(project: Project?) = GlueJobManager(this)

    override fun importProject(description: String, target: String) = throw UnsupportedOperationException()

    override fun exportProject(project: Project) = throw UnsupportedOperationException()
}
