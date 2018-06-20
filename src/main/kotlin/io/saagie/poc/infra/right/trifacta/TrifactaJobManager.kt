package io.saagie.poc.infra.right.trifacta

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
@Profile("trifacta")
class TrifactaJobManager(private val restTemplate: RestTemplate) : JobManager {
    // ATTRIBUTES
    @Value("\${trifacta.url}")
    lateinit var trifactaUrl: String

    @Value("\${trifacta.username}")
    lateinit var username: String

    @Value("\${trifacta.password}")
    lateinit var password: String


    // METHODS
    @Suppress("UNCHECKED_CAST")
    override fun getAll() = restTemplate.process(
            request = RequestEntity.get(URI("$trifactaUrl/jobGroups"))
                    .header("Authorization", "Basic ${generateAuthKey()}")
                    .build() as RequestEntity<JobsDTO>,

            verify = { !(it?.data?.isEmpty() ?: true) },
            transform = { it!!.data.map(::toJob) }
    )

    @Suppress("UNCHECKED_CAST")
    override fun get(id: String) = restTemplate.process(
            request = RequestEntity.get(URI("$trifactaUrl/jobGroups/$id"))
                    .header("Authorization", "Basic ${generateAuthKey()}")
                    .build() as RequestEntity<JobDTO>,

            verify = { it != null },
            transform = { toJob(it!!) }
    )

    @Suppress("UNCHECKED_CAST")
    override fun getStatus(job: Job) = restTemplate.process(
            request = RequestEntity.get(URI("$trifactaUrl/jobGroups/${job.id}/status"))
                    .header("Authorization", "Basic ${generateAuthKey()}")
                    .build() as RequestEntity<String>,
            transform = { toStatus(it!!) }
    )

    override fun start(target: String) = restTemplate.process(
            request = RequestEntity.post(URI("$trifactaUrl/jobGroups"))
                    .header("Authorization", "Basic ${generateAuthKey()}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(
                            RunDTO(IDDTO(target.toIntOrNull()))
                    )
    )

    override fun stop(job: Job) {
            throw UnsupportedOperationException()
    }


    // TOOLS
    fun generateAuthKey() = generateBasicAuthKey(username, password)

    fun toStatus(status: String) = when(status) {
        "Pending" -> JobStatus.NOT_STARTED
        "Complete" -> JobStatus.DONE
        else -> JobStatus.from(status)
    }

    fun toJob(dto: JobDTO) = Job(
            id = dto.id,
            datasetId = dto.wrangledDataset.id.toString(),
            status = toStatus(dto.status)
    )

    // DTOs
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
