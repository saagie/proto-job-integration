package io.saagie.poc.domain

data class Project (
        val id: String = "",
        val name: String = ""
) {
    constructor(key: String): this(key, key)
}
