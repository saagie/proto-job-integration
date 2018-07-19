package io.saagie.poc.infra.right.nifi

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.Project
import io.saagie.poc.infra.AppProperties
import io.saagie.poc.infra.right.common.Requester
import io.saagie.poc.infra.right.common.backtrackSearch
import io.saagie.poc.infra.right.common.process
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@Profile("nifi")
class NifiEnvironmentManager(val restTemplate: RestTemplate, private val properties: AppProperties): EnvironmentManager {
    // ATTRIBUTES
    internal val url = properties.nifi.url
    internal val requester = Requester()


    // METHODS
    override fun getProjects(): Collection<Project> {
        fun getProjectIntels(project: ProjectDTO) = restTemplate.process(
                request = requester.get<AllProcessGroupDTO>("$url/process-groups/${project.id}/process-groups"),
                verify = { it != null },
                transform = { it!!.processGroups.map { it.component }.toSet() }
        )

        return backtrackSearch(
                initial = ProjectDTO("root", "DEFAULT"),
                operation = ::getProjectIntels
        ).map(::toProject)
    }

    override fun getJobManager(project: Project?): JobManager = NifiJobManager(this, project!!.id)

    override fun importProject(description: String, target: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun exportProject(project: Project): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    // TOOL
    fun toProject(dto: ProjectDTO) = Project(
            id = dto.id,
            name = dto.name
    )


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
