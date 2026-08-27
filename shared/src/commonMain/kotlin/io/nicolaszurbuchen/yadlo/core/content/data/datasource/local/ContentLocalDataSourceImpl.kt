package io.nicolaszurbuchen.yadlo.core.content.data.datasource.local

class ContentLocalDataSourceImpl(
    private val queries: CachedDocumentQueries,
) : ContentLocalDataSource {
    override suspend fun read(path: String): CachedDocument? = queries.selectByPath(path).executeAsOneOrNull()

    override suspend fun write(
        path: String,
        body: String,
        etag: String?,
        fetchedAt: Long,
    ) {
        queries.upsert(path = path, body = body, etag = etag, fetched_at = fetchedAt)
    }

    override suspend fun clear() {
        queries.deleteAll()
    }
}
