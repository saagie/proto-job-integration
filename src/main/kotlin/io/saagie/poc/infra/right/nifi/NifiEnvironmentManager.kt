package io.saagie.poc.infra.right.nifi

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.Project
import io.saagie.poc.infra.AppProperties
import io.saagie.poc.infra.right.common.Requester
import io.saagie.poc.infra.right.common.securer.Securer
import io.saagie.poc.infra.right.common.backtrackSearch
import io.saagie.poc.infra.right.common.process
import io.saagie.poc.infra.right.common.execute
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@Profile("nifi")
class NifiEnvironmentManager(val restTemplate: RestTemplate, private val securer: Securer, private val properties: AppProperties): EnvironmentManager {
    // ATTRIBUTES
    internal val url = properties.nifi.url
    internal val requester = Requester(securer)


    // METHODS
    override fun getProjects(): Collection<Project> {
        fun getProjectIntels(project: ProjectDTO) = restTemplate.process(
                request = requester.get<AllProcessGroupDTO>("$url/process-groups/${project.id}/process-groups"),
                transform = { it.processGroups.map { it.component }.toSet() }
        )

        return backtrackSearch(
                initial = ProjectDTO("root", "DEFAULT"),
                operation = ::getProjectIntels
        ).map(::toProject)
    }

    override fun getJobManager(project: Project?): JobManager = NifiJobManager(this, project!!.id)

    override fun importProject(description: String, target: String) = restTemplate.execute(
            request = requester.post(
                    url = "$url/process-groups/$target/process-groups",
                    body = prepareForImport(description)
            )
    )

    override fun exportProject(project: Project) = restTemplate.process(
            request = requester.get<String>("$url/process-groups/${project.id}")
    )


    // TOOL
    fun toProject(dto: ProjectDTO) = Project(
            id = dto.id,
            name = dto.name
    )

    internal fun prepareForImport(jobDescription: String): String {
        // Putting revision number at 0 (and removing a first occurence of processor's ID)
        val jobDescriptionWithRevision = jobDescription.replace(
                "\"revision\":\\{\"version\":[0-9]+},\"id\":\".*\",\"uri".toRegex(),
                "\"revision\":{\"version\":0},\"uri"
        )
        // Removing ID and parent ID (will be defined by Nifi itself)
        return jobDescriptionWithRevision.replace(
                "\"component\":\\{\"id\":\".*\",\"parentGroupId\":\".*\",\"position".toRegex(),
                "\"component\":{\"position"
        )
    }


    // DTOs
    data class AllProcessGroupDTO(
            val processGroups: Array<ProcessGroupDTO> = arrayOf()
    )
    data class ProcessGroupDTO(
            val component: ProjectDTO = ProjectDTO()
    )
    data class ProjectDTO(
            val id: String = "",
            val name: String = ""
    )
}
