package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote

import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto.ContentDocumentDto

interface ContentRemoteDataSource {
    /** Null when the server answered 304 and the cached copy is still current. */
    suspend fun fetchDocument(
        path: String,
        etag: String?,
    ): ContentDocumentDto?
}
