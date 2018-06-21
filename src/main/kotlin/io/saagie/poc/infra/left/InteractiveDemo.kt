package io.saagie.poc.infra.left

import io.saagie.poc.domain.EnvironmentManager
import io.saagie.poc.domain.Job
import io.saagie.poc.domain.JobManager
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("demo")
class InteractiveDemo(private val envManager: EnvironmentManager) : CommandLineRunner {
    override fun run(vararg args: String?) {
        fun <T> display(list: List<T>) {
            (1..list.size).forEach {
                println("$it : ${list[it - 1]}")
            }
        }

        var jobs = listOf<Job>()
        lateinit var jobManager: JobManager
        println("\nThe DSS demonstration tool is now operational !!")
        do {
            print("\nCommand : ")
            val params = readLine()!!.split(" ").map(String::trim)
            try {
                when (params[0]) {
                    "projects" -> {
                        display(envManager.getProjects().toList().reversed())
                    }
                    "use" -> {
                        jobManager = envManager.getJobManager(params[1])
                        jobs = listOf()
                        println("The project has been switched to ${params[1]}")
                    }
                    "datasets" -> {
                        display(jobManager.getDatasets().toList().reversed())
                    }
                    "jobs" -> {
                        jobs = jobManager.getAll().toList().reversed()
                        display(jobs)
                    }
                    "status" -> {
                        val i = params[1].toInt()
                        println("Status of Job #$i : ${jobManager.getStatus(jobs[i - 1])}")
                    }
                    "start" -> {
                        val i = params[1].toInt()
                        jobManager.start(jobs[i - 1])
                        println("The job has been started !")
                    }
                    "stop" -> {
                        val i = params[1].toInt()
                        jobManager.stop(jobs[i - 1])
                        println("The job has been stopped !")
                    }
                }
            } catch (exc : Exception) {
                println("-/!\\- Exception thrown ! The app has failed (or your command is wrong...) -/!\\-")
                exc.printStackTrace()
                println("-------------")
            }
        } while (!arrayOf("quit", "exit").contains(params[0]))

        println("\nEnd of the DSS demonstration tool !")
    }
}
