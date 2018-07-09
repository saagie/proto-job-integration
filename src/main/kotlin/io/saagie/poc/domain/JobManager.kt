package io.saagie.poc.domain

interface JobManager {
    fun getDatasets(): Collection<Dataset>

    fun getAll(): Collection<Job>

    fun get(id: String) = getAll().firstOrNull { it.id == id }

    fun getStatus(job: Job): JobStatus

    fun start(target: String)

    fun start(job: Job) = start(job.datasetId)

    fun stop(job: Job)

    fun import(jobDescription: String, config: String)

    fun export(job: Job): String
}
