package io.saagie.poc.infra.right.nifi

import io.saagie.poc.domain.Dataset
import io.saagie.poc.domain.Job
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.JobStatus
import io.saagie.poc.infra.right.common.process

class NifiJobManager(private val env: NifiEnvironmentManager, private val project: String) : JobManager {
    // METHODS
    override fun getDatasets() = listOf<Dataset>()

    override fun getAll() = env.restTemplate.process(
            request = env.requester.get<AllProcesorsDTO>("${env.url}/process-groups/$project/processors"),
            verify = { it?.processors?.isNotEmpty() ?: false},
            transform = { it!!.processors.map { toJob(it.component) }}
    )

    override fun getStatus(job: Job): JobStatus {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start(target: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop(job: Job) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun import(jobDescription: String, target: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun export(job: Job): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    // TOOLS
    fun toJob(dto: JobDTO) = Job(
            id = dto.id,
            target = project,
            status = toStatus(dto.state)
    )

    fun toStatus(status: String) = when (status.toUpperCase()) {
            "STOPPED" -> JobStatus.ABORTED
            "RUNNING" -> JobStatus.RUNNING
            else -> JobStatus.UNKNOWN
    }


    // DTOs
    data class AllProcesorsDTO(
            val processors: Array<ProcessorDTO> = arrayOf()
    )
    data class ProcessorDTO(
            val component: JobDTO = JobDTO()
    )
    data class JobDTO(
            val id: String = "",
            val state: String = ""
    )
}
