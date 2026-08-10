package io.nicolaszurbuchen.yadlo.infra.platform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
