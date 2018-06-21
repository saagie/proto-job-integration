package io.saagie.poc.infra.left

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.domain.JobManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("starter")
class Starter(private val envManager: EnvironmentManager) : CommandLineRunner {
    @Value("\${common.project}")
    lateinit var project: String

    @Value("\${common.job}")
    lateinit var jobId: String

    override fun run(vararg args: String?) {
        // Getting projects display
        display("Projects", envManager.getProjects().toList())
        val jobManager = envManager.getJobManager(project)

        // Getting datasets
        display("Datasets", jobManager.getDatasets().toList())

        // Retrieving global intel.
        val jobs = jobManager.getAll().toList()
        display("Jobs", jobs)

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

    private fun <T> display(label: String, list: List<T>) {
        println("-- $label list --")
        list.forEach(::println)
        println("---------------")
    }
}


