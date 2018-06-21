package io.saagie.poc.infra.right.trifacta

import io.saagie.poc.domain.Dataset
import io.saagie.poc.domain.Job
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.JobStatus
import io.saagie.poc.infra.right.common.process
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import java.net.URI


class TrifactaJobManager(private val env:TrifactaEnvironmentManager) : JobManager {
    // METHODS
    @Suppress("UNCHECKED_CAST")
    override fun getDatasets() = env.restTemplate.process(
            request = RequestEntity.get(URI("${env.url}/wrangledDatasets"))
                    .header("Authorization", "Basic ${env.generateAuthKey()}")
                    .build() as RequestEntity<DatasetsDTO>,

            verify = { !(it?.data?.isEmpty() ?: true) },
            transform = { it!!.data.map(::toDataset) }
    )

    @Suppress("UNCHECKED_CAST")
    override fun getAll() = env.restTemplate.process(
            request = RequestEntity.get(URI("${env.url}/jobGroups"))
                    .header("Authorization", "Basic ${env.generateAuthKey()}")
                    .build() as RequestEntity<JobsDTO>,

            verify = { !(it?.data?.isEmpty() ?: true) },
            transform = { it!!.data.map(::toJob) }
    )

    @Suppress("UNCHECKED_CAST")
    override fun get(id: String) = env.restTemplate.process(
            request = RequestEntity.get(URI("${env.url}/jobGroups/$id"))
                    .header("Authorization", "Basic ${env.generateAuthKey()}")
                    .build() as RequestEntity<JobDTO>,

            verify = { it != null },
            transform = { toJob(it!!) }
    )

    @Suppress("UNCHECKED_CAST")
    override fun getStatus(job: Job) = env.restTemplate.process(
            request = RequestEntity.get(URI("${env.url}/jobGroups/${job.id}/status"))
                    .header("Authorization", "Basic ${env.generateAuthKey()}")
                    .build() as RequestEntity<String>,
            transform = { toStatus(it!!) }
    )

    override fun start(target: String) = env.restTemplate.process(
            request = RequestEntity.post(URI("${env.url}/jobGroups"))
                    .header("Authorization", "Basic ${env.generateAuthKey()}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(
                            RunDTO(IDDTO(target.toIntOrNull()))
                    )
    )

    override fun stop(job: Job) {
            throw UnsupportedOperationException()
    }


    // TOOLS
    fun toDataset(dto: DatasetDTO) = Dataset(
            id = dto.id,
            name = dto.name,
            project = TrifactaEnvironmentManager.companion.DEFAULT_PROJECT_NAME
    )

    fun toStatus(status: String) = when(status.toUpperCase().trim()) {
        "PENDING" -> JobStatus.NOT_STARTED
        "COMPLETE" -> JobStatus.DONE
        else -> JobStatus.from(status)
    }

    fun toJob(dto: JobDTO) = Job(
            id = dto.id,
            datasetId = dto.wrangledDataset.id.toString(),
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
            val wrangledDataset: IDDTO = IDDTO()
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
