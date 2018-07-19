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
            transform = { it!!.processors.map { it.toJob() }}
    )

    override fun get(id: String) = env.restTemplate.process(
            request = env.requester.get<JobDTO>("${env.url}/processors/$id"),
            verify = { it != null },
            transform = { it!!.toJob() }
    )

    override fun getStatus(job: Job) = get(job.id).status

    override fun start(job: Job) {
        env.restTemplate.process(
                request = env.requester.put(
                        url = "${env.url}/processors/${job.id}",
                        body = StartStopJobDTO(job, "RUNNING")
                )
        )
        job.updates += 1
    }

    override fun stop(job: Job) {
        env.restTemplate.process(
                request = env.requester.put(
                        url = "${env.url}/processors/${job.id}",
                        body = StartStopJobDTO(job, "STOPPED")
                )
        )
        job.updates += 1
    }

    override fun import(jobDescription: String, target: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun export(job: Job): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    // TOOLS
    private fun JobDTO.toJob() = Job(
            id = this.component.id,
            target = project,
            status = this.component.state.toStatus(),
            updates = this.revision.version.toInt()
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
            val processors: Array<JobDTO> = arrayOf()
    )
    data class JobDTO(
            val component: JobIntelDTO = JobIntelDTO(),
            val revision: RevisionDTO = RevisionDTO()
    )
    data class JobIntelDTO(
            val id: String = "",
            val state: String = ""
    )
    data class RevisionDTO(
            val version: String = ""
    )

    // -- PUT
    data class StartStopJobDTO(
            val component: ComponentDTO = ComponentDTO(),
            val revision: RevisionDTO = RevisionDTO(),
            val status: StatusDTO = StatusDTO()
    ) {
        constructor(job: Job, newRunStatus: String): this(
                component = ComponentDTO(job.id),
                revision = RevisionDTO(job.updates.toString()),
                status = StatusDTO(newRunStatus)
        )
    }
    data class ComponentDTO(
            val id: String = ""
    )
    data class StatusDTO(
            val runStatus: String = ""
    )
}
