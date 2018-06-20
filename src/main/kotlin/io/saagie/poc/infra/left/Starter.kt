package io.saagie.poc.infra.left

import io.saagie.poc.domain.JobManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("starter")
class Starter(private val jobManager: JobManager) : CommandLineRunner {
    @Value("\${common.job}")
    lateinit var jobId: String

    override fun run(vararg args: String?) {
        // Retireving global intel.
        println("-- Jobs list --")
        val jobs = jobManager.getAll()
        jobs.forEach(::println)
        println("---------------")

        // Retrieving job infos.
        val job = jobs.firstOrNull { it.id == jobId }
                ?: throw IllegalArgumentException("Job $jobId doesn't exist...")
        println("> Informations retrieved about job ${job.id}")

        // Starting job
        jobManager.start(job)
        println("> The job ${job.id} has started.")

        // Job's current status
        val status = jobManager.getStatus(job)
        println("> Status : ${status.name}")
        println("---------------")
    }
}


