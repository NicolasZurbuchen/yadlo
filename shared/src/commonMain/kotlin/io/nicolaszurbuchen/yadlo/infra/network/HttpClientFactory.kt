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

/**
 * One parser for the whole app, so the leniency the content contract depends on is stated once.
 * `ignoreUnknownKeys` is what makes an additive schema change safe for a build already installed:
 * a field added to the content is skipped rather than failing the file.
 */
fun createJson(): Json =
    Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

fun createHttpClient(jsonFormat: Json): HttpClient =
    HttpClient(httpClientEngine()) {
        install(ContentNegotiation) {
            json(jsonFormat)
        }
        install(Logging) {
            level = LogLevel.ALL
        }
        defaultRequest {
            url(CONTENT_BASE_URL)
        }
    }
