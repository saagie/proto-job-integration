package io.saagie.poc.infra.right.dataiku

import io.saagie.poc.domain.Dataset
import io.saagie.poc.domain.Job
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.JobStatus
import io.saagie.poc.infra.right.common.process
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import java.net.URI


class DataikuJobManager(private val env: DataikuEnvironmentManager, private val project: String) : JobManager {
    // COMMANDS
    @Suppress("UNCHECKED_CAST")
    override fun getDatasets() = env.restTemplate.process(
        request = RequestEntity.get(URI("${env.url}/projects/$project/datasets/"))
                .header("Authorization", "Basic ${env.generateAuthKey()}")
                .build() as RequestEntity<Array<DatasetDTO>>,

        verify = {
            !(it?.isEmpty() ?: true)
            && !(it?.any { it.projectKey != project} ?: true)
        },
        transform = { it!!.map(::toDataset) }
    )

    @Suppress("UNCHECKED_CAST")
    override fun getAll() = env.restTemplate.process(
            request = RequestEntity.get(URI("${env.url}/projects/$project/jobs/"))
                    .header("Authorization", "Basic ${env.generateAuthKey()}")
                    .build() as RequestEntity<Array<JobDTO>>,

            verify = {
                !(it?.isEmpty() ?: true)
                && !(it?.any { it.def.projectKey != project } ?: true)
            },
            transform = { it!!.map(::toJob) }
    )

    @Suppress("UNCHECKED_CAST")
    override fun getStatus(job: Job) = env.restTemplate.process(
            request = RequestEntity.get(URI("${env.url}/projects/$project/jobs/${job.id}/"))
                    .header("Authorization", "Basic ${env.generateAuthKey()}")
                    .build() as RequestEntity<StatusDTO>,

            verify = { !(it?.baseStatus?.state?.isBlank() ?: true) },
            transform = { JobStatus.from(it!!.baseStatus.state) }
    )

    override fun start(target: String) = env.restTemplate.process(
            request = RequestEntity.post(URI("${env.url}/projects/$project/jobs/"))
                    .header("Authorization", "Basic ${env.generateAuthKey()}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(StartDTO(arrayOf(
                            OutputDTO(projectKey = project, id = target)
                    )))
    )

    override fun stop(job: Job) = env.restTemplate.process(
            request = RequestEntity.post(URI("${env.url}/projects/$project/jobs/${job.id}/abort/"))
                    .header("Authorization", "Basic ${env.generateAuthKey()}")
                    .build()
    )


    // TOOLS
    private fun toDataset(dto: DatasetDTO) = Dataset(
            id = dto.name,
            name = dto.smartName,
            project = dto.projectKey
    )

    private fun toJob(dto: JobDTO) = Job(
            id = dto.def.id,
            datasetId = dto.def.name.split(" ")[1],
            status = JobStatus.from(dto.state)
    )


    // DTOs
    // -- GetDatasets()
    data class DatasetDTO(
            val name: String = "",
            val smartName: String = "",
            val projectKey: String = ""
    )

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
