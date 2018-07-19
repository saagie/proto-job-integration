package io.saagie.poc.infra.left

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.domain.Project
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("starter")
class Starter(private val envManager: EnvironmentManager) : CommandLineRunner {
    // ATTRIBUTES
    @Value("\${common.project}")
    lateinit var project: String

    @Value("\${common.job}")
    lateinit var jobId: String


    // MAIN
    override fun run(vararg args: String?) {
        // Getting projects display
        display("Projects", envManager.getProjects().toList())
        val jobManager = envManager.getJobManager(Project(project))

        // Getting datasets
        display("Datasets", jobManager.getDatasets().toList())

        // Retrieving global intel.
        val jobs = jobManager.getAll().toList()
        display("Jobs", jobs)

        // Retrieving job infos.
        val job = jobs.firstOrNull { it.id == jobId }
                ?: throw IllegalArgumentException("Job $jobId doesn't exist...")
        display("Informations retrieved about job ${job.id}")

        // Starting job
        jobManager.start(job)
        display("The job ${job.id} has started.")

        // Job's current status
        val status = jobManager.getStatus(job)
        display("Status : ${status.name}")

        // Job's exportProject
        val json = jobManager.export(job)
        display("The job has been exported :\n$json")

        // Job's importProject
        jobManager.import(json)
        display("The job has been imported !")
    }


    // TOOLS
    private fun <T> display(label: String, list: List<T>) {
        println("-- $label list --")
        list.forEach(::println)
        println("---------------")
    }
    private fun display(message: String) = println("> $message\n---------------")
}


