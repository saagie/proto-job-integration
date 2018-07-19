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
            request = env.requester.get<AllProcessorsDTO>("${env.url}/process-groups/$project/processors"),
            verify = { it?.processors?.isNotEmpty() ?: false},
            transform = { it!!.processors.map { it.component.toJob() }}
    )

    override fun get(id: String) = env.restTemplate.process(
            request = env.requester.get<ProcessorDTO>("${env.url}/processors/$id"),
            verify = { it != null },
            transform = { it!!.component.toJob() }
    )

    override fun getStatus(job: Job) = get(job.id).status

    override fun start(target: String) = env.restTemplate.process(
            request = env.requester.put(
                    url = "${env.url}/processors/$target",
                    body = StartStopJobDTO("RUNNING")
            )
    )
    override fun start(job: Job) = start(job.id)

    override fun stop(job: Job) = env.restTemplate.process(
            request = env.requester.put(
                    url = "${env.url}/processors/${job.id}",
                    body = StartStopJobDTO("STOPPED")
            )
    )

    override fun import(jobDescription: String, target: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun export(job: Job): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    // TOOLS
    private fun JobDTO.toJob() = Job(
            id = this.id,
            target = project,
            status = this.state.toStatus()
    )

    private fun String.toStatus() = when (this.toUpperCase()) {
            "STOPPED"  -> JobStatus.DONE
            "DISABLED" -> JobStatus.ABORTED
            "RUNNING"  -> JobStatus.RUNNING
            else -> JobStatus.UNKNOWN
    }


    // DTOs
    // -- GET
    data class AllProcessorsDTO(
            val processors: Array<ProcessorDTO> = arrayOf()
    )
    data class ProcessorDTO(
            val component: JobDTO = JobDTO()
    )
    data class JobDTO(
            val id: String = "",
            val state: String = ""
    )
    data class RevisionDTO

    // -- PUT
    data class StartStopJobDTO(
            val status: StatusDTO = StatusDTO()
    ) {
        constructor(id: String, runStatus: String): this(StatusDTO(runStatus))
    }
    data class StatusDTO(
            val component: JobDTO = JobDTO(),
            val revision: RevisionDTO = RevisionDTO(),
            val runStatus: String = ""
    )
}
