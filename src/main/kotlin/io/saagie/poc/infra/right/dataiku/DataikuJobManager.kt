package io.saagie.poc.infra.right.dataiku

import io.saagie.poc.domain.Job
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.JobStatus
import io.saagie.poc.infra.right.common.generateBasicAuthKey
import io.saagie.poc.infra.right.common.process
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.net.URI

@Component
@Profile("dataiku")
class DataikuJobManager(private val restTemplate: RestTemplate) : JobManager {
    // ATTRIBUTES
    @Value("\${dataiku.url}")
    lateinit var dataikuUrl: String

    @Value("\${dataiku.apikey}")
    lateinit var apikey: String

    @Value("\${dataiku.project}")
    lateinit var project: String


    // COMMANDS
    @Suppress("UNCHECKED_CAST")
    override fun getAll() = restTemplate.process(
            request = RequestEntity.get(URI("$dataikuUrl/projects/$project/jobs/"))
                    .header("Authorization", "Basic ${generateAuthKey()}")
                    .build() as RequestEntity<Array<JobDTO>>,

            verify = {
                !(it?.isEmpty() ?: true)
                && !(it?.any { it.def.projectKey != project } ?: true)
            },
            transform = { it!!.map { Job(
                    datasetId = it.def.name.split(" ")[1],
                    id = it.def.id,
                    status = JobStatus.from(it.state)
            )}}
    )

    @Suppress("UNCHECKED_CAST")
    override fun getStatus(job: Job) = restTemplate.process(
            request = RequestEntity.get(URI("$dataikuUrl/projects/$project/jobs/${job.id}/"))
                    .header("Authorization", "Basic ${generateAuthKey()}")
                    .build() as RequestEntity<StatusDTO>,

            verify = { !(it?.baseStatus?.state?.isBlank() ?: true) },
            transform = { JobStatus.from(it!!.baseStatus.state) }
    )

    override fun start(target: String) = restTemplate.process(
            request = RequestEntity.post(URI("$dataikuUrl/projects/$project/jobs/"))
                    .header("Authorization", "Basic ${generateAuthKey()}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(StartDTO(arrayOf(
                            OutputDTO(projectKey = project, id = target)
                    )))
    )

    override fun stop(job: Job) = restTemplate.process(
            request = RequestEntity.post(URI("$dataikuUrl/projects/$project/jobs/${job.id}/abort/"))
                    .header("Authorization", "Basic ${generateAuthKey()}")
                    .build()
    )


    // TOOLS
    /**
     * Dataiku is using basic auth, with a simple apikey as username and no password.
     */
    private fun generateAuthKey() = generateBasicAuthKey(apikey)


    // DTOs
    // -- GetAll()
    data class DefDTO(
            val id: String = "",
            val name: String = "",
            val projectKey: String = ""
    )
    data class JobDTO(
            val def: DefDTO = DefDTO(),
            val state: String = ""
    )

    // -- GetStatus
    data class BaseStatusDTO(
            val state: String = ""
    )
    data class StatusDTO(
            val baseStatus : BaseStatusDTO = BaseStatusDTO()
    )

    // -- start()
    data class OutputDTO(
            val projectKey: String = "",
            val id: String = ""
    )
    data class StartDTO (
            val outputs: Array<OutputDTO> = arrayOf()
    )
}
