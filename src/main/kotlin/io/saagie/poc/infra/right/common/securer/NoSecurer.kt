package io.saagie.poc.infra.right.common.securer

class NoSecurer: AbstractSecurer() {
    override fun getAuthorization(): String = ""
}
