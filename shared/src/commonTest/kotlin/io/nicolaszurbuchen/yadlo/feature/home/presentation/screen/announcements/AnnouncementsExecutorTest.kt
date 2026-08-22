package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.announcements

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Announcement
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Venue
import io.nicolaszurbuchen.yadlo.feature.home.domain.usecase.ObserveHomeContentUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class AnnouncementsExecutorTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onCreate_repositoryPublishesABundle_showsEveryAnnonceOfTheCurrentEdition() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(
                ContentStatus.Ready(
                    bundle =
                        bundle(
                            listOf(
                                announcement("un", editionId = "2026"),
                                announcement("evergreen", editionId = null),
                                announcement("last-year", editionId = "2025"),
                            ),
                        ),
                    updateRequired = false,
                ),
            )
            testDispatcher.scheduler.runCurrent()

            // The same narrowing Accueil applies, so a past edition's annonce is missing from both.
            assertEquals(listOf("un", "evergreen"), store.state.announcements.map { it.id })
            assertEquals(false, store.state.isLoading)
            store.dispose()
        }

    @Test
    fun onCreate_nothingPublishedYet_staysLoading() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            assertEquals(true, store.state.isLoading)
            store.dispose()
        }

    @Test
    fun announcementClicked_publishesTheUrlForThePlatformToOpen() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(AnnouncementsIntent.AnnouncementClicked("https://example.com/aftermovie"))
                assertEquals(AnnouncementsLabel.OpenUrl("https://example.com/aftermovie"), awaitItem())
            }
            store.dispose()
        }

    private fun createStore(repository: FakeContentRepository): AnnouncementsStore =
        AnnouncementsStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observeHomeContent = ObserveHomeContentUseCase(repository),
        ).create()

    private fun bundle(announcements: List<Announcement>) =
        ContentBundle(
            festival =
                Festival(
                    name = "Yadlo",
                    tagline = "Mouille ton corps, arrose ton esprit",
                    website = "https://www.yadlo.ch/",
                    currentEditionId = "2026",
                    minSupportedAppVersion = null,
                    social = emptyList(),
                ),
            edition =
                Edition(
                    id = "2026",
                    year = 2026,
                    name = "Yadlo 2026",
                    venue =
                        Venue(
                            name = "Plage de Préverenges",
                            address = "Route de la Plage, 1028 Préverenges",
                            latitude = 46.51,
                            longitude = 6.53,
                            provenance = Provenance.CONFIRMED,
                        ),
                    days = listOf(friday()),
                    categories = emptyList(),
                    happenings = listOf(happening()),
                    slots = emptyList(),
                    partners = emptyList(),
                    figures = emptyList(),
                ),
            announcements = announcements,
        )

    private fun friday() =
        FestivalDay(
            id = "2026:fri",
            name = "Vendredi",
            date = "2026-07-10",
            start = Instant.parse("2026-07-10T16:00:00+02:00"),
            end = Instant.parse("2026-07-11T02:00:00+02:00"),
            provenance = Provenance.CONFIRMED,
        )

    private fun happening() =
        Happening.Artist(
            id = "2026:dubside",
            name = "Dubside",
            category = Category(id = "musique", name = "Musique", order = 1),
            description = null,
            images = emptyList(),
            provenance = Provenance.CONFIRMED,
            genres = emptyList(),
            links = emptyList(),
        )

    private fun announcement(
        id: String,
        editionId: String?,
    ) = Announcement(
        id = id,
        publishedAt = Instant.parse("2026-06-02T12:00:00+02:00"),
        title = id,
        body = null,
        editionId = editionId,
        url = null,
        provenance = Provenance.UNVERIFIED,
    )
}
