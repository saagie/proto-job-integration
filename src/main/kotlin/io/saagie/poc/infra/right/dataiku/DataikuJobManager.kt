package io.saagie.poc.infra.right.dataiku

import io.saagie.poc.domain.Dataset
import io.saagie.poc.domain.Job
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.JobStatus
import io.saagie.poc.infra.right.common.process
import io.saagie.poc.infra.right.common.execute


class DataikuJobManager(private val env: DataikuEnvironmentManager, private val project: String) : JobManager {
    // COMMANDS
    @Suppress("UNCHECKED_CAST")
    override fun getDatasets() = env.restTemplate.process(
            request = env.requester.get<Array<DatasetDTO>>(
                    url = "${env.url}/projects/$project/datasets/"
            ),
            verify = { it.isNotEmpty() && it.all { it.projectKey == project }},
            transform = { it.map(::toDataset) }
    )

    @Suppress("UNCHECKED_CAST")
    override fun getAll() = env.restTemplate.process(
            request = env.requester.get<Array<JobDTO>>(
                    url = "${env.url}/projects/$project/jobs/"
            ),
            verify = { it.isNotEmpty() && it.all { it.def.projectKey == project }},
            transform = { it.map(::toJob) }
    )

    @Suppress("UNCHECKED_CAST")
    override fun getStatus(job: Job) = env.restTemplate.process(
            request = env.requester.get<StatusDTO>(
                    url = "${env.url}/projects/$project/jobs/${job.id}/"
            ),
            verify = { it.baseStatus.state.isNotBlank() },
            transform = { JobStatus.from(it.baseStatus.state) }
    )

    override fun start(job: Job) = env.restTemplate.execute(
            request = env.requester.post(
                    url = "${env.url}/projects/$project/jobs/",
                    body = StartDTO(arrayOf(OutputDTO(project, job.target)))
            )
    )

    override fun stop(job: Job) = env.restTemplate.execute(
            request = env.requester.post(
                    url = "${env.url}/projects/$project/jobs/${job.id}/abort/"
            )
    )

    override fun import(jobDescription: String, target: String) = env.restTemplate.execute(
            request = env.requester.post(
                    url = "${env.url}/projects/$project/datasets/$target",
                    body = jobDescription
            )
    )

    @Suppress("UNCHECKED_CAST")
    override fun export(job: Job) = env.restTemplate.process(
            request = env.requester.get<String>(
                    url = "${env.url}/projects/$project/datasets/${job.target}"
            )
    )


    // TOOLS
    private fun toDataset(dto: DatasetDTO) = Dataset(
            id = dto.name,
            name = dto.smartName,
            project = dto.projectKey
    )

    private fun toJob(dto: JobDTO) = Job(
            id = dto.def.id,
            target = dto.def.name.split(" ")[1],
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
