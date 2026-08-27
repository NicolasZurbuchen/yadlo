package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.api

import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto.ContentDocumentDto

/** Fetched first at launch: it names `currentEditionId`, so no edition path exists until it lands. */
const val FESTIVAL_PATH = "festival.json"

/** Its own small file with its own ETag, which is what makes polling it during LIVE affordable. */
const val ANNOUNCEMENTS_PATH = "announcements.json"

fun editionPath(editionId: String): String = "editions/$editionId/edition.json"

interface ContentApi {
    /**
     * A conditional GET. Returns null when the server answered 304 and the caller's cached copy is
     * still current — a few hundred bytes of headers instead of the whole bundle.
     *
     * Anything other than success or 304 throws, so null means *not modified* and nothing else.
     */
    suspend fun fetchDocument(
        path: String,
        etag: String?,
    ): ContentDocumentDto?
}
