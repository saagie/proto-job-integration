package io.saagie.poc.infra.right.common.securer

interface TokenExtractor {
    fun action(): (Any) -> String
    fun tokenClass() : Class<*>
}

fun <T> Class<T>.createTokenExtractor(action: (T) -> String): TokenExtractor = object: TokenExtractor {
    @Suppress("UNCHECKED_CAST")
    override fun action(): (Any) -> String = { action(it as T) }
    override fun tokenClass() = this@createTokenExtractor
}
