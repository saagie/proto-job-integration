package io.saagie.poc.infra.right.knime

import io.saagie.poc.domain.Dataset
import io.saagie.poc.domain.Job
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.JobStatus
import io.saagie.poc.infra.right.common.process
import io.saagie.poc.infra.right.common.correctURL


class KnimeJobManager(private val env: KnimeEnvironmentManager, private val project: String) : JobManager {
    // COMMANDS
    override fun getDatasets() = listOf<Dataset>()

    @Suppress("UNCHECKED_CAST")
    override fun getAll() = env.restTemplate.process(
            request = env.requester.get<ResponseDTO>("${env.url}/jobs/"),
            verify = {
                !(it?.jobs?.isEmpty() ?: true)
                && (it?.jobs?.any(::inProject) ?: false)
            },
            transform = { it!!.jobs.filter(::inProject).map(::toJob) }
    )

    @Suppress("UNCHECKED_CAST")
    override fun get(id: String) = env.restTemplate.process(
            request = env.requester.get<JobDTO>("${env.url}/jobs/${id}/"),
            verify = { it != null && inProject(it) },
            transform = { toJob(it!!) }
    )

    // Knime doesn't include a specific 'get a Job status' API request.
    override fun getStatus(job: Job) = get(job.id).status

    override fun start(target: String) = env.restTemplate.process(
            request = env.requester.post(
                url = "${env.url}/jobs/$target/".correctURL(),
                body ="{}"
            )
    )

    override fun start(job: Job) = start(job.id)

    // Knime doesn't include a specific 'stop a Job' API request.
    override fun stop(job: Job) = throw UnsupportedOperationException()

    override fun import(jobDescription: String, target: String) = env.restTemplate.process(
            request = env.requester.post(
                    url = "${env.url}/repository$target:jobs".correctURL(),
                    body = jobDescription
            )
    )

    @Suppress("UNCHECKED_CAST")
    override fun export(job: Job) = env.restTemplate.process(
            request = env.requester.get<String>("${env.url}/jobs/${job.id}/"),
            verify = { !(it?.isBlank() ?: true) },
            transform = { it!! }
    )


    // TOOLS
    private fun inProject(dto: JobDTO) = dto.workflow.startsWith(project)

    private fun toStatus(status: String) = when (status.toUpperCase()) {
            "IDLE" -> JobStatus.NOT_STARTED
            "EXECUTED" -> JobStatus.DONE
            else -> JobStatus.UNKNOWN
    }

    private fun toJob(dto: JobDTO) = Job(
            id = dto.id,
            target = dto.workflow,
            status = toStatus(dto.state)
    )


    // DTOs
    data class JobDTO (
            val id: String = "",
            val workflow: String = "",
            val name: String = "",
            val state: String = ""
    )
    data class ResponseDTO (
            val jobs: Array<JobDTO> = arrayOf()
    )
}
