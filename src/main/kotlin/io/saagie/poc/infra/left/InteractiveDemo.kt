package io.saagie.poc.infra.left

import io.saagie.poc.domain.Job
import io.saagie.poc.domain.JobManager
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("demo")
class InteractiveDemo(private val jobManager: JobManager) : CommandLineRunner {
    override fun run(vararg args: String?) {
        var jobs = listOf<Job>()
        var cmd : String

        println("\nThe DSS demonstration tool is now operational !!")
        do {
            print("Command : ")
            cmd = readLine()!!
            val args = cmd.split(" ").map(String::trim)
            try {
                when (args[0]) {
                    "all" -> {
                        jobs = jobManager.getAll().toList().reversed()
                        (1..jobs.size).forEach {
                            println("$it : ${jobs[it - 1]}")
                        }
                    }
                    "status" -> {
                        val i = args[1].toInt()
                        println("Status of Job #$i : ${jobManager.getStatus(jobs[i - 1])}")
                    }
                    "start" -> {
                        val i = args[1].toInt()
                        jobManager.start(jobs[i - 1])
                        println("The job has been started !")
                    }
                    "stop" -> {
                        val i = args[1].toInt()
                        jobManager.stop(jobs[i - 1])
                        println("The job has been stopped !")
                    }
                }
            } catch (exc : Exception) {
                println("-/!\\- Exception thrown ! The app has failed (or your command is wrong...) -/!\\-")
                exc.printStackTrace()
                println("-------------")
            }
            println()
        } while (!arrayOf("quit", "exit").contains(cmd))
        println("End of the DSS demonstration tool !")
    }
}
