package io.saagie.poc.infra.right.trifacta

import io.saagie.poc.domain.Dataset
import io.saagie.poc.domain.Job
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.JobStatus
import io.saagie.poc.infra.right.common.process


class TrifactaJobManager(private val env:TrifactaEnvironmentManager) : JobManager {
    // METHODS
    @Suppress("UNCHECKED_CAST")
    override fun getDatasets() = env.restTemplate.process(
            request = env.requester.get<DatasetsDTO>("${env.url}/wrangledDatasets"),
            verify = { !(it?.data?.isEmpty() ?: true) },
            transform = { it!!.data.map(::toDataset) }
    )

    @Suppress("UNCHECKED_CAST")
    override fun getAll() = env.restTemplate.process(
            request = env.requester.get<JobsDTO>("${env.url}/jobGroups"),
            verify = { !(it?.data?.isEmpty() ?: true) },
            transform = { it!!.data.map(::toJob) }
    )

    @Suppress("UNCHECKED_CAST")
    override fun get(id: String) = env.restTemplate.process(
            request = env.requester.get<JobDTO>("${env.url}/jobGroups/$id"),
            verify = { it != null },
            transform = { toJob(it!!) }
    )

    @Suppress("UNCHECKED_CAST")
    override fun getStatus(job: Job) = env.restTemplate.process(
            request = env.requester.get<String>("${env.url}/jobGroups/${job.id}/status"),
            transform = { toStatus(it!!) }
    )

    override fun start(job: Job) = env.restTemplate.process(
            request = env.requester.post(
                    url = "${env.url}/jobGroups",
                    body = RunDTO(IDDTO(job.target.toIntOrNull()))
            )
    )

    override fun stop(job: Job) {
            throw UnsupportedOperationException()
    }

    override fun import(jobDescription: String, target: String) = env.restTemplate.process(
            request = env.requester.post(
                    url = "${env.url}/wrangledDatasets/",
                    body = jobDescription
            )
    )

    @Suppress("UNCHECKED_CAST")
    override fun export(job: Job) = env.restTemplate.process(
        request = env.requester.get<String>("${env.url}/wrangledDatasets/${job.id}"),
        verify = { it != null },
        transform = { it!! }
    )


    // TOOLS
    private fun toDataset(dto: DatasetDTO) = Dataset(
            id = dto.id,
            name = dto.name,
            project = TrifactaEnvironmentManager.companion.DEFAULT_PROJECT_NAME
    )

    private fun toStatus(status: String) = when(status.toUpperCase().trim()) {
        "PENDING" -> JobStatus.NOT_STARTED
        "COMPLETE" -> JobStatus.DONE
        else -> JobStatus.from(status)
    }

    private fun toJob(dto: JobDTO) = Job(
            id = dto.id,
            target = dto.wrangledDataset?.id.toString(),
            status = toStatus(dto.status)
    )


    // DTOs
    data class DatasetDTO(
            val id: String = "",
            val name: String = ""
    )
    data class DatasetsDTO(
            val data: Array<DatasetDTO> = arrayOf()
    )
    data class JobDTO(
            val id: String = "",
            val status: String = "",
            val wrangledDataset: IDDTO? = IDDTO()
    )
    data class JobsDTO(
            val data: Array<JobDTO> = arrayOf()
    )
    data class IDDTO(
            val id: Int? = null
    )
    data class RunDTO(
            val wrangledDataset: IDDTO = IDDTO()
    )
}
