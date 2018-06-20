package io.saagie.poc.domain

interface JobManager {
    fun getAll(): Collection<Job>
    fun get(id: String) = getAll().firstOrNull { it.id == id }
    fun getStatus(job: Job): JobStatus
    fun start(target: String)
    fun start(job: Job) = start(job.datasetId)
    fun stop(job: Job)
}