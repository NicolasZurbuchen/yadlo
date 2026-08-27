package io.nicolaszurbuchen.yadlo.core.content.data.datasource.local

interface ContentLocalDataSource {
    suspend fun read(path: String): CachedDocument?

    /**
     * [fetchedAt] is passed in rather than read from a clock here: the clock is injected at the
     * composition root, which is what keeps every time-dependent behaviour testable off-season.
     */
    suspend fun write(
        path: String,
        body: String,
        etag: String?,
        fetchedAt: Long,
    )

    /** Only for an edition change. A refresh never clears the cache — it replaces one document. */
    suspend fun clear()
}
