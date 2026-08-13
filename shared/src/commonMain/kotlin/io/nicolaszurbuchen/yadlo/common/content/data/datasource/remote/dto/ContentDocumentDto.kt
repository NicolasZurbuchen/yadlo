package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

/**
 * One fetched content file, as served. The HTTP envelope around a document rather than the document
 * itself, which is why it is not `@Serializable`: [body] stays raw text because that is exactly what
 * the cache stores, and parsing happens once, later, where the graph is assembled.
 */
data class ContentDocumentDto(
    val body: String,
    /** The server's ETag, replayed as `If-None-Match` on the next fetch. Null when none was sent. */
    val etag: String?,
)
