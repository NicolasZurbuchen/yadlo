package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.nicolaszurbuchen.yadlo.common.error.AppError
import io.nicolaszurbuchen.yadlo.common.error.AppException
import io.nicolaszurbuchen.yadlo.infra.network.CONTENT_BASE_URL
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ContentApiImplTest {
    // region conditional GET

    @Test
    fun fetchDocument_withAnEtag_sendsItAsIfNoneMatch() =
        runTest {
            var captured: HttpRequestData? = null
            val api = apiRespondingWith(body = "{}") { captured = it }

            api.fetchDocument(path = FESTIVAL_PATH, etag = "\"abc123\"")

            assertEquals("\"abc123\"", captured?.headers?.get(HttpHeaders.IfNoneMatch))
        }

    @Test
    fun fetchDocument_withoutAnEtag_sendsNoIfNoneMatch() =
        runTest {
            // The first fetch of a document has nothing to be conditional on. Sending an empty or
            // absent-but-present header would make the server answer 304 against nothing held.
            var captured: HttpRequestData? = null
            val api = apiRespondingWith(body = "{}") { captured = it }

            api.fetchDocument(path = FESTIVAL_PATH, etag = null)

            assertNull(captured?.headers?.get(HttpHeaders.IfNoneMatch))
        }

    @Test
    fun fetchDocument_notModified_returnsNull() =
        runTest {
            val api = apiRespondingWith(body = "", status = HttpStatusCode.NotModified)

            assertNull(api.fetchDocument(path = ANNOUNCEMENTS_PATH, etag = "\"abc123\""))
        }

    // endregion

    // region success

    @Test
    fun fetchDocument_success_returnsTheRawBodyAndTheEtag() =
        runTest {
            val api = apiRespondingWith(body = """{"schemaVersion":1}""", etag = "\"v2\"")

            val result = api.fetchDocument(path = FESTIVAL_PATH, etag = null)

            assertEquals("""{"schemaVersion":1}""", result?.body)
            assertEquals("\"v2\"", result?.etag)
        }

    @Test
    fun fetchDocument_serverSendsNoEtag_returnsANullEtagRatherThanFailing() =
        runTest {
            // GitHub Pages sends one, but a proxy in between may strip it. Losing the ETag costs a
            // full body on the next fetch; treating it as an error would cost the content entirely.
            val api = apiRespondingWith(body = "{}", etag = null)

            val result = api.fetchDocument(path = FESTIVAL_PATH, etag = null)

            assertEquals("{}", result?.body)
            assertNull(result?.etag)
        }

    @Test
    fun fetchDocument_resolvesThePathAgainstTheContentRoot() =
        runTest {
            var captured: HttpRequestData? = null
            val api = apiRespondingWith(body = "{}") { captured = it }

            api.fetchDocument(path = editionPath("2026"), etag = null)

            assertEquals("${CONTENT_BASE_URL}editions/2026/edition.json", captured?.url.toString())
        }

    // endregion

    // region failure

    @Test
    fun fetchDocument_notFound_throwsWithTheStatusCode() =
        runTest {
            val api = apiRespondingWith(body = "", status = HttpStatusCode.NotFound)

            val exception = assertFailsWith<AppException> { api.fetchDocument(path = editionPath("1999"), etag = null) }

            assertEquals(AppError.Network.Http(code = 404), exception.error)
        }

    @Test
    fun fetchDocument_serverError_throwsWithTheStatusCode() =
        runTest {
            val api = apiRespondingWith(body = "", status = HttpStatusCode.InternalServerError)

            val exception = assertFailsWith<AppException> { api.fetchDocument(path = FESTIVAL_PATH, etag = null) }

            assertEquals(AppError.Network.Http(code = 500), exception.error)
        }

    // endregion

    private fun apiRespondingWith(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
        etag: String? = "\"etag\"",
        onRequest: (HttpRequestData) -> Unit = {},
    ): ContentApi {
        val engine =
            MockEngine { request ->
                onRequest(request)
                respond(
                    content = body,
                    status = status,
                    headers = etag?.let { headersOf(HttpHeaders.ETag, it) } ?: headersOf(),
                )
            }
        val client =
            HttpClient(engine) {
                defaultRequest { url(CONTENT_BASE_URL) }
            }
        return ContentApiImpl(client)
    }
}
