package io.saagie.poc.infra.right.dsx

import io.saagie.poc.domain.Dataset
import io.saagie.poc.domain.Job
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.JobStatus
import io.saagie.poc.infra.right.common.process
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import java.net.URI

class DSXJobManager(private val env: DSXEnvironmentManager, private val project: String) : JobManager {
    // METHODS
    override fun getDatasets() = listOf<Dataset>()

    @Suppress("UNCHECKED_CAST")
    override fun getAll() = env.restTemplate.process(
            request = RequestEntity.get(URI("${env.url}/api/v2/filemgmt/view/$project"))
                    .header("Authorization", "Bearer ${env.getToken()}")
                    .build() as RequestEntity<Array<String>>,
            verify = { !(it?.isEmpty() ?: true) },
            transform = {
                it!!.filter { it.reversed().startsWith(".jar".reversed()) }.map(::toJob)
            }
    )

    @Suppress("UNCHECKED_CAST")
    override fun getStatus(job: Job) = env.restTemplate.process(
            request = RequestEntity.get(URI("${env.url}/api/v1/spark/status?jobId=${job.id}"))
                    .header("Authorization", "Bearer ${env.getToken()}")
                    .build() as RequestEntity<StatusDTO>,
            verify = { it != null },
            transform = { toStatus(it!!.status) }
    )

    override fun start(target: String) = env.restTemplate.process(
            request = RequestEntity.post(URI("${env.url}/api/v1/spark/submit"))
                    .header("Authorization", "Bearer ${env.getToken()}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(StartDTO(target))
    )

    override fun start(job: Job) = start(job.id)

    override fun stop(job: Job) = env.restTemplate.process(
            request = RequestEntity.post(URI("${env.url}/api/v1/spark/cancel?jobId=${job.id}"))
                    .header("Authorization", "Bearer ${env.getToken()}")
                    .build()
    )

    override fun import(jobDescription: String, config: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun export(job: Job): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    // TOOLS
    private fun toJob(id: String) = Job(id)
    private fun toStatus(status: String) = when (status.toUpperCase()) {
        "COMPLETED" -> JobStatus.DONE
        "FAILED" -> JobStatus.FAILED
        "RUNNING" -> JobStatus.RUNNING
        "TERMINATING" -> JobStatus.ABORTED
        else -> JobStatus.UNKNOWN
    }


    // DTOs
    data class StatusDTO(
            val status: String = ""
    )
    data class StartDTO(
            val appPath: String = ""
    )
}
