package io.nicolaszurbuchen.yadlo.infra.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Where the festival's content is published. Served by GitHub Pages straight out of `content/`
 * in this repository, so the site root is the content root and there is no `/content/` segment.
 *
 * The trailing slash is load-bearing: Ktor resolves a relative path against it, so
 * `get("editions/2026/edition.json")` only lands in the right place while it is there.
 */
const val CONTENT_BASE_URL = "https://nicolaszurbuchen.github.io/yadlo/"

fun createHttpClient(): HttpClient =
    HttpClient(httpClientEngine()) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                },
            )
        }
        install(Logging) {
            level = LogLevel.ALL
        }
        defaultRequest {
            url(CONTENT_BASE_URL)
        }
    }
