package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote

import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.api.ContentApi
import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto.ContentDocumentDto
import io.nicolaszurbuchen.yadlo.core.error.AppError
import io.nicolaszurbuchen.yadlo.core.error.AppException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ContentRemoteDataSourceImplTest {
    @Test
    fun fetchDocument_passesThePathAndEtagThrough() =
        runTest {
            val api = StubContentApi(result = ContentDocumentDto(body = "{}", etag = "\"v1\""))
            val dataSource = ContentRemoteDataSourceImpl(api)

            dataSource.fetchDocument(path = "festival.json", etag = "\"v0\"")

            assertEquals("festival.json", api.capturedPath)
            assertEquals("\"v0\"", api.capturedEtag)
        }

    @Test
    fun fetchDocument_notModified_staysNull() =
        runTest {
            val dataSource = ContentRemoteDataSourceImpl(StubContentApi(result = null))

            assertNull(dataSource.fetchDocument(path = "festival.json", etag = "\"v0\""))
        }

    @Test
    fun fetchDocument_preservesAnErrorTheApiAlreadyClassified() =
        runTest {
            // A 404 on an edition path is a different problem from no signal, and collapsing the
            // two into Unavailable would send someone to check their connection over a bad id.
            val api = StubContentApi(failure = AppException(AppError.Network.Http(code = 404)))
            val dataSource = ContentRemoteDataSourceImpl(api)

            val exception = assertFailsWith<AppException> { dataSource.fetchDocument(path = "x.json", etag = null) }

            assertEquals(AppError.Network.Http(code = 404), exception.error)
        }

    @Test
    fun fetchDocument_unclassifiedFailure_becomesUnavailable() =
        runTest {
            val api = StubContentApi(failure = IllegalStateException("socket closed"))
            val dataSource = ContentRemoteDataSourceImpl(api)

            val exception = assertFailsWith<AppException> { dataSource.fetchDocument(path = "x.json", etag = null) }

            assertEquals(AppError.Network.Unavailable, exception.error)
        }

    @Test
    fun fetchDocument_cancellation_isRethrownRatherThanSwallowed() =
        runTest {
            // Catching this would turn a cancelled scope into a fake network error, and the caller
            // would retry work nobody is waiting for any more.
            val api = StubContentApi(failure = CancellationException("cancelled"))
            val dataSource = ContentRemoteDataSourceImpl(api)

            assertFailsWith<CancellationException> { dataSource.fetchDocument(path = "x.json", etag = null) }
        }
}

private class StubContentApi(
    private val result: ContentDocumentDto? = null,
    private val failure: Throwable? = null,
) : ContentApi {
    var capturedPath: String? = null
    var capturedEtag: String? = null

    override suspend fun fetchDocument(
        path: String,
        etag: String?,
    ): ContentDocumentDto? {
        capturedPath = path
        capturedEtag = etag
        failure?.let { throw it }
        return result
    }
}
