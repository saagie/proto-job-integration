package io.saagie.poc.infra.right.glue

import io.saagie.poc.domain.Dataset
import io.saagie.poc.domain.Job
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.JobStatus
import io.saagie.poc.infra.right.common.process
import io.saagie.poc.infra.right.common.fromJSON
import io.saagie.poc.infra.right.common.toJSON

class GlueJobManager(private val env: GlueEnvironmentManager): JobManager {
    // METHODS
    override fun getDatasets() = listOf<Dataset>()

    override fun getAll() = env.restTemplate.process(
            request = env.awsRequester.post("AWSGlue.GetJobs"),
            verify = { MultipleJobDTO::class.java.fromJSON(it).Jobs.isNotEmpty() },
            transform = { MultipleJobDTO::class.java.fromJSON(it).Jobs.map { it.toJob() }}
    )

    override fun get(id: String) = env.restTemplate.process(
            request = env.awsRequester.post(
                    target = "AWSGlue.GetJobRuns",
                    body = "{\"JobName\":\"$id\"}"
            ),
            verify = { MultipleJobRunDTO::class.java.fromJSON(it).JobRuns.isNotEmpty() },
            transform = { MultipleJobRunDTO::class.java.fromJSON(it).JobRuns.first().toJob() }
    )

    override fun getStatus(job: Job) = get(job.id).status

    override fun start(job: Job) = env.restTemplate.process(
            request = env.awsRequester.post(
                    target = "AWSGlue.StartJobRun",
                    body = "{\"JobName\":\"${job.id}\"}"
            ),
            transform = {}
    )

    override fun stop(job: Job) {
        val runIds = env.restTemplate.process(
                request = env.awsRequester.post(
                        target = "AWSGlue.GetJobRuns",
                        body = "{\"JobName\":\"${job.id}\"}"
                ),
                verify = { MultipleJobRunDTO::class.java.fromJSON(it).JobRuns.isNotEmpty() },
                transform = { MultipleJobRunDTO::class.java.fromJSON(it).JobRuns.map { it.Id }}
        )

        return env.restTemplate.process(
                request = env.awsRequester.post(
                        target = "AWSGlue.BatchStopJobRun",
                        body = StopDTO(job.id, runIds.toTypedArray()).toJSON()
                ),
                transform = {}
        )
    }

    override fun import(jobDescription: String, target: String) {
        val txt = prepareForImport(jobDescription)
        val request = env.awsRequester.post(
                target = "AWSGlue.CreateJob",
                body = prepareForImport(jobDescription)
        )
        println("$txt\n---")
        println(request.url)
        request.headers.entries.forEach {
            (key, values) ->
            println("$key: $values")
        }

        env.restTemplate.process(
                request = request,
                transform = {}
        )
    }

    override fun export(job: Job) = env.restTemplate.process(
            request = env.awsRequester.post(
                    target = "AWSGlue.GetJob",
                    body = "{\"JobName\":\"${job.id}\"}"
            )
    )

    // TOOLS
    private fun JobRunDTO.toJob() = Job(
            id = this.JobName,
            target = this.JobName,
            status = this.JobRunState.toStatus()
    )
    private fun JobDTO.toJob() = Job(
            id = this.Name,
            target = this.Name
    )

    private fun String.toStatus() = when (this.toUpperCase()) {
        "STARTING" -> JobStatus.NOT_STARTED
        "RUNNING" -> JobStatus.RUNNING
        "STOPPING" -> JobStatus.ABORTED
        "SUCCEEDED" -> JobStatus.DONE
        "FAILED", "TIMEOUT" -> JobStatus.FAILED
        else -> JobStatus.UNKNOWN
    }

    private fun prepareForImport(json: String) =
        json.removePrefix("{\"Job\":")
            .replace("}}", "}")
            .replace(
                    "\"CreatedOn\":.*,\"DefaultArguments\"".toRegex(),
                    "\"DefaultArguments\""
            )
            .replace(
                    "\"LastModifiedOn\":.*,\"MaxDPU\"".toRegex(),
                    "\"MaxDPU\""
            )


    // DTOs
    data class MultipleJobRunDTO(
        val JobRuns: Array<JobRunDTO>
    )
    data class MultipleJobDTO(
            val Jobs: Array<JobDTO>
    )
    data class JobRunDTO(
        val Id: String = "",
        val JobName: String = "",
        val JobRunState: String = ""
    )
    data class JobDTO(
            val Name: String = ""
    )
    data class StopDTO(
            val JobName: String = "",
            val JobRunIds: Array<String> = arrayOf()
    )
}
