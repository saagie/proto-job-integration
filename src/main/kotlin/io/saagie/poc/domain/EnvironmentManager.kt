package io.saagie.poc.domain

interface EnvironmentManager {
    fun getProjects() : Collection<Project>

    fun getJobManager(project: Project? = null) : JobManager

    fun importProject(description: String, target: String = "")

    fun exportProject(project: Project): String
}
