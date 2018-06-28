package io.saagie.poc.domain

/**
 * Aims at normalizing the description of a given job's current state.
 * (Note that it isn't mandatory to use all of these status in the same adapter).
 */
enum class JobStatus {
    NOT_STARTED, RUNNING, FAILED, ABORTED, DONE, UNKNOWN;

    companion object {
        fun from(s: String) =
                JobStatus.values().filter { it.name == s.toUpperCase() }.firstOrNull() ?: JobStatus.UNKNOWN
    }
}
