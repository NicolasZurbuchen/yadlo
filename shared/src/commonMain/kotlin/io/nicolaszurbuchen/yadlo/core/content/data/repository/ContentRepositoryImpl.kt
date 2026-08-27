package io.nicolaszurbuchen.yadlo.core.content.data.repository

import io.nicolaszurbuchen.yadlo.core.content.data.datasource.local.ContentLocalDataSource
import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.ContentRemoteDataSource
import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.api.ANNOUNCEMENTS_PATH
import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.api.FESTIVAL_PATH
import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.api.editionPath
import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto.AnnouncementsDto
import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto.CurrentEditionDto
import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto.EditionDto
import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto.FestivalDto
import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto.SchemaVersionDto
import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.mapper.toDomain
import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.core.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.core.error.AppError
import io.nicolaszurbuchen.yadlo.core.error.AppException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.time.Clock

/**
 * Holds the resolved content graph and keeps it current.
 *
 * **The cache is the only source the bundle is ever built from.** A fetch's job is to update the
 * cache; publishing then re-reads it. That is what makes the schema gate below hold — a document
 * this build cannot read is never written, so anything in the cache is by definition parseable —
 * and it means the warm-start path and the post-fetch path are the same code rather than two
 * readings of the same file that can drift.
 *
 * **Nothing here may throw its way out.** Content arrives from a server that ships on its own
 * schedule and is read by builds older and newer than itself, so a document this build cannot make
 * sense of is a normal Tuesday, not an exceptional condition. Every outcome is a [ContentStatus]:
 * the stale bundle with the update flag raised, or the error screen. A festival app that dies on
 * launch because one paragraph grew a field is worse than one showing last week's programme.
 */
class ContentRepositoryImpl(
    private val remoteDataSource: ContentRemoteDataSource,
    private val localDataSource: ContentLocalDataSource,
    private val json: Json,
    private val clock: Clock,
) : ContentRepository {
    private val status = MutableStateFlow<ContentStatus>(ContentStatus.Loading)

    override fun observeStatus(): StateFlow<ContentStatus> = status.asStateFlow()

    override suspend fun refresh() {
        // The cache goes out first and unconditionally: it is what makes the app work on the beach,
        // and nothing may block the UI on the network.
        publishFromCache(updateRequired = false)

        val allCached =
            try {
                fetchEveryDocument()
            } catch (e: CancellationException) {
                throw e
            } catch (e: AppException) {
                // A network failure with a warm cache is not a failure the visitor needs to see.
                if (status.value !is ContentStatus.Ready) status.value = ContentStatus.Unavailable(e.error)
                return
            } catch (_: SerializationException) {
                // A cache an older build wrote and this one reads differently. Publishing below
                // decides what the visitor sees; it is not this line's call and never a crash.
                false
            }

        publishFromCache(updateRequired = !allCached)

        // Nothing cached, and nothing that arrived could be cached. Left on Loading the visitor
        // watches a spinner that will never resolve, which is the one outcome worse than an error
        // screen — the same reason an edition that never arrives is an error rather than silence.
        if (status.value is ContentStatus.Loading) {
            status.value =
                ContentStatus.Unavailable(
                    AppError.Content.MalformedField(field = FESTIVAL_PATH, detail = "unreadable by this build"),
                )
        }
    }

    /**
     * Returns false when at least one document was refused as unreadable, which is the soft-update
     * signal. Refusing at write time rather than parse time is what implements "keeps its cache":
     * the newer bytes are simply never stored.
     */
    private suspend fun fetchEveryDocument(): Boolean {
        val festivalCached = fetchDocument(FESTIVAL_PATH) { json.decodeFromString<FestivalDto>(it) }

        // The edition path does not exist until festival.json names it, which is why the order is
        // fixed rather than three parallel fetches. Only the one field is read: working out which
        // file to fetch next has no business depending on whether the rest of this one parses.
        val festivalBody = localDataSource.read(FESTIVAL_PATH)?.body
        val editionCached =
            festivalBody?.let { body ->
                val editionId = json.decodeFromString<CurrentEditionDto>(body).currentEditionId
                fetchDocument(editionPath(editionId)) { json.decodeFromString<EditionDto>(it) }
            } ?: true

        val announcementsCached = fetchDocument(ANNOUNCEMENTS_PATH) { json.decodeFromString<AnnouncementsDto>(it) }

        return festivalCached && editionCached && announcementsCached
    }

    /**
     * [parse] is the document read whole and thrown away. It costs one extra parse of a file that
     * actually changed — a 304 never gets here — and it buys the invariant the class doc claims:
     * that the cache holds nothing this build cannot read. Without it a single published field this
     * build does not understand overwrites a working festival with bytes that fail on every cold
     * start after, and the visitor's app is bricked until the *content* is fixed.
     */
    private suspend fun fetchDocument(
        path: String,
        parse: (String) -> Unit,
    ): Boolean {
        val cachedEtag = localDataSource.read(path)?.etag
        val document = remoteDataSource.fetchDocument(path, cachedEtag) ?: return true

        // Declaring a newer schema and simply not parsing are the same refusal wearing two faces:
        // the published file says something this build cannot read. The version is the polite
        // warning, the parse is the one that cannot be forgotten when content is edited.
        try {
            if (json.decodeFromString<SchemaVersionDto>(document.body).schemaVersion > SUPPORTED_SCHEMA_VERSION) return false
            parse(document.body)
        } catch (_: SerializationException) {
            return false
        }

        localDataSource.write(
            path = path,
            body = document.body,
            etag = document.etag,
            fetchedAt = clock.now().toEpochMilliseconds(),
        )
        return true
    }

    private suspend fun publishFromCache(updateRequired: Boolean) {
        val bundle =
            try {
                readCachedBundle()
            } catch (e: AppException) {
                status.value = ContentStatus.Unavailable(e.error)
                return
            } catch (_: SerializationException) {
                status.value = ContentStatus.Unavailable(AppError.Content.MalformedField(field = "cache", detail = "unparseable"))
                return
            }

        if (bundle != null) status.value = ContentStatus.Ready(bundle, updateRequired)
    }

    private suspend fun readCachedBundle(): ContentBundle? {
        val festivalBody = localDataSource.read(FESTIVAL_PATH)?.body ?: return null
        val festival = json.decodeFromString<FestivalDto>(festivalBody).toDomain()

        // Festival.json naming an edition that never arrives is an unresolved reference like any
        // other, not an absent cache: without this the status would sit on Loading forever, which
        // is the one outcome worse than an error screen.
        val editionBody =
            localDataSource.read(editionPath(festival.currentEditionId))?.body
                ?: throw AppException(
                    AppError.Content.UnresolvedReference(field = "festival.currentEditionId", id = festival.currentEditionId),
                )
        val edition = json.decodeFromString<EditionDto>(editionBody).toDomain()

        // Annonces are the one part the app can do without: a missing feed is a missing block on
        // Accueil, not a festival that cannot be shown.
        val announcements =
            localDataSource.read(ANNOUNCEMENTS_PATH)
                ?.body
                ?.let { json.decodeFromString<AnnouncementsDto>(it).toDomain() }
                .orEmpty()

        return ContentBundle(festival = festival, edition = edition, announcements = announcements)
    }

    private companion object {
        /**
         * The highest schema this build knows how to read. Content changes additively and the
         * parser ignores unknown keys, so this only ever moves for a break that could not be made
         * additive — see DECISIONS.md.
         */
        const val SUPPORTED_SCHEMA_VERSION = 1
    }
}
