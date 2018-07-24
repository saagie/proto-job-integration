package io.saagie.poc.infra.right.dsx

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.Project
import io.saagie.poc.infra.AppProperties
import io.saagie.poc.infra.right.common.Requester
import io.saagie.poc.infra.right.common.process
import io.saagie.poc.infra.right.common.securer.Securer
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@Profile("dsx")
class DSXEnvironmentManager(val restTemplate: RestTemplate, private val securer: Securer, private val properties: AppProperties) : EnvironmentManager {
    // ATTRIBUTES
    internal val url = properties.dsx.url
    internal val requester = Requester(securer)


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
}
