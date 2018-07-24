package io.saagie.poc.infra.right.glue

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.domain.Project
import io.saagie.poc.infra.AppProperties
import io.saagie.poc.infra.right.common.Requester
import io.saagie.poc.infra.right.common.securer.Securer
import org.springframework.web.client.RestTemplate

class GlueEnvironmentManager(val restTemplate: RestTemplate, private val securer: Securer, private val properties: AppProperties) : EnvironmentManager {
    // ATTRIBUTES
    internal val url = properties.glue.url
    internal val requester = Requester(securer)


    // METHODS
    override fun getProjects(): Collection<Project> = listOf(Project("DEFAULT"))

    override fun getJobManager(project: Project?) = GlueJobManager(this)

    override fun importProject(description: String, target: String) = throw UnsupportedOperationException()

    override fun exportProject(project: Project) = throw UnsupportedOperationException()
}
