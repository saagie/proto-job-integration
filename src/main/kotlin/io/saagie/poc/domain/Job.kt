package io.saagie.poc.domain

data class Job (
        val id: String = "",
        val target: String = "",
        val status: JobStatus = JobStatus.UNKNOWN,
        val updates: Int = 0
)
