package io.saagie.poc.domain

interface EnvironmentManager {
    fun getProjects() : Collection<String>

    fun getJobManager(project: String? = null) : JobManager

    fun importProject(description: String, target: String = "")

    fun exportProject(id: String): String
}
