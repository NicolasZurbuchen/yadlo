package io.nicolaszurbuchen.yadlo.feature.home.domain.usecase

import app.cash.turbine.test
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Announcement
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Figure
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Slot
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Venue
import io.nicolaszurbuchen.yadlo.common.error.AppError
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class ObserveHomeContentUseCaseTest {
    @Test
    fun invoke_statusIsReady_narrowsBundleToWhatAccueilReads() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveHomeContentUseCase(repository)

            useCase().test {
                repository.emitStatus(ContentStatus.Ready(bundle = bundle(), updateRequired = false))

                val content = awaitItem()
                assertEquals("Yadlo 2026", content.editionName)
                assertEquals(listOf("2026:fri"), content.days.map { it.id })
                assertEquals(listOf("visiteurs"), content.figures.map { it.id })
            }
        }

    @Test
    fun invoke_editionHasNoSlots_reportsNoPublishedProgramme() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveHomeContentUseCase(repository)

            useCase().test {
                repository.emitStatus(
                    ContentStatus.Ready(bundle = bundle(slots = emptyList()), updateRequired = false),
                )

                assertEquals(false, awaitItem().hasPublishedProgramme)
            }
        }

    @Test
    fun invoke_editionHasSlots_reportsAPublishedProgramme() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveHomeContentUseCase(repository)

            useCase().test {
                repository.emitStatus(ContentStatus.Ready(bundle = bundle(), updateRequired = false))

                assertEquals(true, awaitItem().hasPublishedProgramme)
            }
        }

    @Test
    fun invoke_announcementBelongsToAnotherEdition_isDroppedWhileTheEvergreenOneStays() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveHomeContentUseCase(repository)
            val announcements =
                listOf(
                    announcement(id = "current", editionId = "2026"),
                    announcement(id = "evergreen", editionId = null),
                    announcement(id = "last-year", editionId = "2025"),
                )

            useCase().test {
                repository.emitStatus(
                    ContentStatus.Ready(
                        bundle = bundle(announcements = announcements),
                        updateRequired = false,
                    ),
                )

                assertEquals(
                    listOf("current", "evergreen"),
                    awaitItem().announcements.map { it.id },
                )
            }
        }

    @Test
    fun invoke_statusIsNotReady_emitsNothing() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveHomeContentUseCase(repository)

            useCase().test {
                repository.emitStatus(ContentStatus.Unavailable(AppError.Network.Unavailable))

                expectNoEvents()
            }
        }

    private fun bundle(
        slots: List<Slot> = listOf(slot()),
        announcements: List<Announcement> = emptyList(),
    ) = ContentBundle(
        festival =
            Festival(
                name = "Yadlo",
                tagline = "Mouille ton corps, arrose ton esprit",
                currentEditionId = "2026",
                minSupportedAppVersion = null,
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
                days = listOf(day()),
                categories = emptyList(),
                happenings = listOf(happening()),
                slots = slots,
                partners = emptyList(),
                figures = listOf(Figure(id = "visiteurs", value = "6000", label = "visiteurs", provenance = Provenance.CONFIRMED)),
            ),
        announcements = announcements,
    )

    private fun day() =
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

    private fun slot() =
        Slot(
            id = "2026:dubside-fri",
            happening = happening(),
            day = day(),
            start = Instant.parse("2026-07-10T20:00:00+02:00"),
            end = Instant.parse("2026-07-10T21:00:00+02:00"),
            provenance = Provenance.CONFIRMED,
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
