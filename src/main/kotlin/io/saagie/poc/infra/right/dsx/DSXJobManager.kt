package io.saagie.poc.infra.right.dsx

import io.saagie.poc.domain.Dataset
import io.saagie.poc.domain.Job
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.JobStatus
import io.saagie.poc.infra.right.common.process

class DSXJobManager(private val env: DSXEnvironmentManager, private val project: String) : JobManager {
    // METHODS
    override fun getDatasets() = listOf<Dataset>()

    @Suppress("UNCHECKED_CAST")
    override fun getAll() = env.restTemplate.process(
            request = env.requester.get<Array<String>>("${env.url}/api/v2/filemgmt/view/$project"),
            verify = { !(it?.isEmpty() ?: true) },
            transform = {
                it!!.filter { it.reversed().startsWith(".jar".reversed()) }.map(::toJob)
            }
    )

    @Suppress("UNCHECKED_CAST")
    override fun getStatus(job: Job) = env.restTemplate.process(
            request = env.requester.get<StatusDTO>("${env.url}/api/v1/spark/status?jobId=${job.id}"),
            verify = { it != null },
            transform = { toStatus(it!!.status) }
    )

    override fun start(job: Job) = env.restTemplate.process(
            request = env.requester.post(
                    url = "${env.url}/api/v1/spark/submit",
                    body = StartDTO(job.id)
            )
    )

    override fun stop(job: Job) = env.restTemplate.process(
            request = env.requester.post(
                    url = "${env.url}/api/v1/spark/cancel?jobId=${job.id}"
            )
    )

    override fun import(jobDescription: String, target: String) = throw UnsupportedOperationException()
    override fun export(job: Job) = throw UnsupportedOperationException()


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
