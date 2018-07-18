package io.saagie.poc.infra.right.common.securer

import io.saagie.poc.infra.right.common.encode64


class BasicSecurer(private val username: String, private val password: String): AbstractSecurer() {
    // METHOD
    override fun getAuthorization(): String = "Basic ${encode64("$username:$password")}"
}
