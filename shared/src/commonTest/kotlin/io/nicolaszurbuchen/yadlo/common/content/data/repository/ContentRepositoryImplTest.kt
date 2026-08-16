package io.nicolaszurbuchen.yadlo.common.content.data.repository

import io.nicolaszurbuchen.yadlo.common.content.data.datasource.local.CachedDocument
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.local.ContentLocalDataSource
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.ContentRemoteDataSource
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.ContentDocumentDto
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.error.AppError
import io.nicolaszurbuchen.yadlo.common.error.AppException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

private const val FESTIVAL = "festival.json"
private const val EDITION = "editions/2026/edition.json"
private const val ANNOUNCEMENTS = "announcements.json"

class ContentRepositoryImplTest {
    // region cold start

    @Test
    fun refresh_noCacheAndNoSignal_reportsUnavailableRatherThanSpinning() =
        runTest {
            val repository =
                repository(
                    remote = StubRemote(failure = AppException(AppError.Network.Unavailable)),
                )

            repository.refresh()

            val status = repository.observeStatus().value
            assertIs<ContentStatus.Unavailable>(status)
            assertEquals(AppError.Network.Unavailable, status.error)
        }

    @Test
    fun refresh_coldStart_fetchesFestivalThenTheEditionItNamesThenAnnouncements() =
        runTest {
            // The edition path does not exist until festival.json names it, which is the whole
            // reason the order is fixed rather than three parallel fetches.
            val remote = StubRemote(documents = publishedDocuments())
            val repository = repository(remote = remote)

            repository.refresh()

            assertEquals(listOf(FESTIVAL, EDITION, ANNOUNCEMENTS), remote.requestedPaths)
        }

    @Test
    fun refresh_coldStart_publishesTheResolvedBundle() =
        runTest {
            val repository = repository(remote = StubRemote(documents = publishedDocuments()))

            repository.refresh()

            val status = repository.observeStatus().value
            assertIs<ContentStatus.Ready>(status)
            assertEquals("2026", status.bundle.festival.currentEditionId)
            assertEquals("DJ ALF", status.bundle.edition.slots.single().happening.name)
            assertEquals(1, status.bundle.announcements.size)
        }

    // endregion

    // region warm start

    @Test
    fun refresh_warmCache_publishesBeforeTheNetworkIsEvenTried() =
        runTest {
            // What makes the app work on the beach. The fetch here never completes successfully,
            // and the visitor still sees the programme.
            val local = StubLocal(publishedDocuments().mapValues { (_, doc) -> doc.body })
            val repository =
                repository(
                    remote = StubRemote(failure = AppException(AppError.Network.Unavailable)),
                    local = local,
                )

            repository.refresh()

            assertIs<ContentStatus.Ready>(repository.observeStatus().value)
        }

    @Test
    fun refresh_replaysTheCachedEtagSoAnUnchangedFileCostsHeaders() =
        runTest {
            val local = StubLocal(publishedDocuments().mapValues { (_, doc) -> doc.body }, etag = "\"v1\"")
            val remote = StubRemote(documents = emptyMap())
            val repository = repository(remote = remote, local = local)

            repository.refresh()

            assertEquals(listOf<String?>("\"v1\"", "\"v1\"", "\"v1\""), remote.sentEtags.toList())
        }

    @Test
    fun refresh_notModified_keepsTheCachedCopy() =
        runTest {
            val local = StubLocal(publishedDocuments().mapValues { (_, doc) -> doc.body })
            // Every fetch answers 304, so nothing is written and the bundle still resolves.
            val repository = repository(remote = StubRemote(documents = emptyMap()), local = local)

            repository.refresh()

            assertIs<ContentStatus.Ready>(repository.observeStatus().value)
            assertTrue(local.writes.isEmpty())
        }

    // endregion

    // region schema gate

    @Test
    fun refresh_documentDeclaresANewerSchema_keepsTheCacheAndAsksForAnUpdate() =
        runTest {
            val local = StubLocal(publishedDocuments().mapValues { (_, doc) -> doc.body })
            val newer =
                publishedDocuments() + (ANNOUNCEMENTS to ContentDocumentDto(body = announcementsJson(schemaVersion = 2), etag = null))
            val repository = repository(remote = StubRemote(documents = newer), local = local)

            repository.refresh()

            val status = repository.observeStatus().value
            assertIs<ContentStatus.Ready>(status)
            assertTrue(status.updateRequired)
            assertTrue(local.writes.none { it == ANNOUNCEMENTS })
        }

    @Test
    fun refresh_documentThisBuildCannotParse_keepsTheCacheRatherThanOverwritingIt() =
        runTest {
            // The failure the version number is supposed to announce and sometimes does not: content
            // edited without bumping it. Written into the cache, those bytes fail on every cold start
            // after, so the app would be bricked until the *content* was fixed.
            val local = StubLocal(publishedDocuments().mapValues { (_, doc) -> doc.body })
            val unreadable = publishedDocuments() + (EDITION to ContentDocumentDto(body = """{ "schemaVersion": 1 }""", etag = null))
            val repository = repository(remote = StubRemote(documents = unreadable), local = local)

            repository.refresh()

            val status = repository.observeStatus().value
            assertIs<ContentStatus.Ready>(status)
            assertTrue(status.updateRequired)
            assertTrue(local.writes.none { it == EDITION })
            assertEquals("DJ ALF", status.bundle.edition.slots.single().happening.name)
        }

    @Test
    fun refresh_nothingCachedAndNothingReadable_reportsUnavailableRatherThanSpinning() =
        runTest {
            // Cold start against content this build cannot read. Every document is refused, so
            // nothing is cached and there is no bundle to publish; without this the visitor watches
            // a spinner that never resolves.
            val newer = publishedDocuments().mapValues { (_, doc) -> ContentDocumentDto(body = """{ "schemaVersion": 99 }""", etag = null) }
            val repository = repository(remote = StubRemote(documents = newer))

            repository.refresh()

            val status = repository.observeStatus().value
            assertIs<ContentStatus.Unavailable>(status)
            assertIs<AppError.Content.MalformedField>(status.error)
        }

    @Test
    fun refresh_aCacheThisBuildCannotRead_reportsUnavailableRatherThanThrowing() =
        runTest {
            // Written by an older build that read festival.json differently. It reaches the fetch
            // step before anything publishes, which is where an uncaught parse failure used to take
            // the process down on launch rather than the screen.
            val local = StubLocal(mapOf(FESTIVAL to "{ }"))
            val repository = repository(remote = StubRemote(documents = emptyMap()), local = local)

            repository.refresh()

            assertIs<ContentStatus.Unavailable>(repository.observeStatus().value)
        }

    @Test
    fun refresh_everyDocumentReadable_doesNotAskForAnUpdate() =
        runTest {
            val repository = repository(remote = StubRemote(documents = publishedDocuments()))

            repository.refresh()

            val status = repository.observeStatus().value
            assertIs<ContentStatus.Ready>(status)
            assertTrue(!status.updateRequired)
        }

    // endregion

    // region partial content

    @Test
    fun refresh_annoncesMissing_stillPublishesTheFestivalAndTheEdition() =
        runTest {
            // A missing annonce feed is a missing block on Accueil, not a festival the app cannot
            // show. The other two are not optional in the same way.
            val documents = publishedDocuments() - ANNOUNCEMENTS
            val repository = repository(remote = StubRemote(documents = documents))

            repository.refresh()

            val status = repository.observeStatus().value
            assertIs<ContentStatus.Ready>(status)
            assertTrue(status.bundle.announcements.isEmpty())
        }

    @Test
    fun refresh_festivalNamesAnEditionThatNeverArrives_reportsAnUnresolvedReference() =
        runTest {
            // Not an absent cache: the two files disagree. Left as "no bundle yet" the status would
            // sit on Loading forever, which is the one outcome worse than an error screen.
            val documents = publishedDocuments() - EDITION
            val repository = repository(remote = StubRemote(documents = documents))

            repository.refresh()

            val status = repository.observeStatus().value
            assertIs<ContentStatus.Unavailable>(status)
            assertEquals(
                AppError.Content.UnresolvedReference(field = "festival.currentEditionId", id = "2026"),
                status.error,
            )
        }

    @Test
    fun refresh_bundleWithAnUnresolvableReference_reportsTheMappersOwnError() =
        runTest {
            val broken = publishedDocuments() + (EDITION to ContentDocumentDto(body = editionJsonWithMissingHappening(), etag = null))
            val repository = repository(remote = StubRemote(documents = broken))

            repository.refresh()

            val status = repository.observeStatus().value
            assertIs<ContentStatus.Unavailable>(status)
            assertEquals(
                AppError.Content.UnresolvedReference(field = "slot[2026:dj-alf-fri].happeningId", id = "dj-alf"),
                status.error,
            )
        }

    // endregion

    // region writes

    @Test
    fun refresh_stampsEachWriteWithTheInjectedClock() =
        runTest {
            val local = StubLocal()
            val repository =
                repository(
                    remote = StubRemote(documents = publishedDocuments()),
                    local = local,
                    clock = FixedClock(Instant.parse("2026-07-10T12:00:00Z")),
                )

            repository.refresh()

            assertTrue(local.writtenAt.isNotEmpty())
            assertTrue(local.writtenAt.all { it == Instant.parse("2026-07-10T12:00:00Z").toEpochMilliseconds() })
        }

    // endregion

    private fun repository(
        remote: ContentRemoteDataSource,
        local: ContentLocalDataSource = StubLocal(),
        clock: Clock = FixedClock(Instant.parse("2026-07-10T12:00:00Z")),
    ) = ContentRepositoryImpl(
        remoteDataSource = remote,
        localDataSource = local,
        json = Json { ignoreUnknownKeys = true },
        clock = clock,
    )
}

// region fixtures

private fun publishedDocuments(): Map<String, ContentDocumentDto> =
    mapOf(
        FESTIVAL to ContentDocumentDto(body = festivalJson(), etag = "\"f1\""),
        EDITION to ContentDocumentDto(body = editionJson(), etag = "\"e1\""),
        ANNOUNCEMENTS to ContentDocumentDto(body = announcementsJson(), etag = "\"a1\""),
    )

private fun festivalJson(): String =
    """
    {
      "schemaVersion": 1,
      "name": "Yadlo",
      "tagline": "Mouille ton corps, arrose ton esprit",
      "currentEditionId": "2026",
      "minSupportedAppVersion": null
    }
    """.trimIndent()

private fun announcementsJson(schemaVersion: Int = 1): String =
    """
    {
      "schemaVersion": $schemaVersion,
      "announcements": [
        {
          "id": "programme-2026",
          "publishedAt": "2026-06-02T12:00:00+02:00",
          "editionId": "2026",
          "title": "Le programme complet est en ligne",
          "body": null,
          "url": null,
          "provenance": "unverified"
        }
      ]
    }
    """.trimIndent()

private fun editionJson(happeningId: String = "dj-alf"): String =
    """
    {
      "schemaVersion": 1,
      "id": "2026",
      "year": 2026,
      "name": "Yadlo 2026",
      "venue": {
        "name": "Plage de Préverenges",
        "address": "Avenue de la Plage 1",
        "latitude": 46.5122,
        "longitude": 6.5347,
        "provenance": "unverified"
      },
      "days": [
        {
          "id": "2026:fri",
          "name": "Vendredi",
          "date": "2026-07-10",
          "start": "2026-07-10T16:00:00+02:00",
          "end": "2026-07-11T02:00:00+02:00",
          "provenance": "confirmed"
        }
      ],
      "categories": [{ "id": "musique", "name": "Musique", "order": 1 }],
      "happenings": [
        {
          "id": "$happeningId",
          "kind": "artist",
          "name": "DJ ALF",
          "category": "musique",
          "description": null,
          "images": [],
          "provenance": "confirmed",
          "artist": { "genres": ["House"], "links": [] }
        }
      ],
      "slots": [
        {
          "id": "2026:dj-alf-fri",
          "happeningId": "dj-alf",
          "dayId": "2026:fri",
          "start": "2026-07-10T17:00:00+02:00",
          "end": "2026-07-10T18:30:00+02:00",
          "provenance": "confirmed"
        }
      ],
      "partners": [],
      "figures": []
    }
    """.trimIndent()

/** The slot still points at `dj-alf`, but the happening declaring that id is now called something else. */
private fun editionJsonWithMissingHappening(): String = editionJson(happeningId = "someone-else")

private class StubRemote(
    private val documents: Map<String, ContentDocumentDto> = emptyMap(),
    private val failure: Throwable? = null,
) : ContentRemoteDataSource {
    val requestedPaths = mutableListOf<String>()
    val sentEtags = mutableListOf<String?>()

    override suspend fun fetchDocument(
        path: String,
        etag: String?,
    ): ContentDocumentDto? {
        requestedPaths += path
        sentEtags += etag
        failure?.let { throw it }
        return documents[path]
    }
}

private class StubLocal(
    initial: Map<String, String> = emptyMap(),
    private val etag: String? = null,
) : ContentLocalDataSource {
    private val bodies = initial.toMutableMap()
    val writes = mutableListOf<String>()
    val writtenAt = mutableListOf<Long>()

    override suspend fun read(path: String): CachedDocument? =
        bodies[path]?.let { CachedDocument(path = path, body = it, etag = etag, fetched_at = 0L) }

    override suspend fun write(
        path: String,
        body: String,
        etag: String?,
        fetchedAt: Long,
    ) {
        bodies[path] = body
        writes += path
        writtenAt += fetchedAt
    }

    override suspend fun clear() {
        bodies.clear()
    }
}

private class FixedClock(
    private val instant: Instant,
) : Clock {
    override fun now(): Instant = instant
}

// endregion
