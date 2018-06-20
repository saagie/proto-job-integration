package io.saagie.poc.domain

enum class JobStatus {
    NOT_STARTED, RUNNING, FAILED, ABORTED, DONE, UNKNOWN;

    companion object {
        fun from(s: String) =
                JobStatus.values().filter { it.name == s.toUpperCase() }.firstOrNull() ?: JobStatus.UNKNOWN
    }
}
