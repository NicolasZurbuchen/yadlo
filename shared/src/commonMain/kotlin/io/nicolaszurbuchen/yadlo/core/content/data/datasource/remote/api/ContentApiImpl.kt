package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto.ContentDocumentDto
import io.nicolaszurbuchen.yadlo.core.error.AppError
import io.nicolaszurbuchen.yadlo.core.error.AppException

class ContentApiImpl(
    private val client: HttpClient,
) : ContentApi {
    override suspend fun fetchDocument(
        path: String,
        etag: String?,
    ): ContentDocumentDto? {
        val response =
            client.get(path) {
                etag?.let { header(HttpHeaders.IfNoneMatch, it) }
            }

        return when {
            response.status == HttpStatusCode.NotModified -> {
                null
            }

            response.status.isSuccess() -> {
                ContentDocumentDto(
                    body = response.bodyAsText(),
                    etag = response.headers[HttpHeaders.ETag],
                )
            }

            else -> {
                throw AppException(AppError.Network.Http(response.status.value))
            }
        }
    }
}
