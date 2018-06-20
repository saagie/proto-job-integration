package io.saagie.poc.domain

data class Job (
        val id: String = "",
        val datasetId: String = "",
        val status: JobStatus = JobStatus.UNKNOWN
)
