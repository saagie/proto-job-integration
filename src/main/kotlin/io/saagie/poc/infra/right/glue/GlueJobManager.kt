package io.saagie.poc.infra.right.glue

import io.saagie.poc.domain.Dataset
import io.saagie.poc.domain.Job
import io.saagie.poc.domain.JobManager
import io.saagie.poc.domain.JobStatus

class GlueJobManager(private val env: GlueEnvironmentManager): JobManager {
    override fun getDatasets() = listOf<Dataset>()

    override fun getAll(): Collection<Job> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getStatus(job: Job): JobStatus {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start(job: Job) {
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
}
