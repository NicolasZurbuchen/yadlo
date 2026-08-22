package io.nicolaszurbuchen.yadlo.feature.home.domain.usecase

import app.cash.turbine.test
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Announcement
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Contact
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Figure
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.InfoLink
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Involvement
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Payment
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Slot
import io.nicolaszurbuchen.yadlo.common.content.domain.model.SocialLink
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Story
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Transport
import io.nicolaszurbuchen.yadlo.common.content.domain.model.TransportMode
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
    fun invoke_countsHappeningsRatherThanSlots_soAThreeDayActivityCountsOnce() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveHomeContentUseCase(repository)

            useCase().test {
                repository.emitStatus(ContentStatus.Ready(bundle = bundle(), updateRequired = false))

                val content = awaitItem()
                assertEquals(1, content.artistCount)
                assertEquals(1, content.activityCount)
            }
        }

    @Test
    fun invoke_aFigureCameFromAPastEdition_reportsTheFiguresAsUnconfirmed() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveHomeContentUseCase(repository)

            useCase().test {
                repository.emitStatus(
                    ContentStatus.Ready(
                        bundle = bundle(figureProvenance = Provenance.ARCHIVED),
                        updateRequired = false,
                    ),
                )

                assertEquals(false, awaitItem().figuresAreConfirmed)
            }
        }

    @Test
    fun invoke_everyFigureConfirmedForThisEdition_needsNoCaveat() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveHomeContentUseCase(repository)

            useCase().test {
                repository.emitStatus(ContentStatus.Ready(bundle = bundle(), updateRequired = false))

                assertEquals(true, awaitItem().figuresAreConfirmed)
            }
        }

    @Test
    fun invoke_carriesTheNetworksFromTheLiveTruthFile() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveHomeContentUseCase(repository)

            useCase().test {
                repository.emitStatus(ContentStatus.Ready(bundle = bundle(), updateRequired = false))

                assertEquals(listOf("Instagram"), awaitItem().social.map { it.name })
            }
        }

    // region the sections Accueil promotes

    @Test
    fun invoke_everyPromotedSectionPublished_reportsEachOneAvailable() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveHomeContentUseCase(repository)

            useCase().test {
                repository.emitStatus(ContentStatus.Ready(bundle = bundle(festival = fullFestival()), updateRequired = false))

                val content = awaitItem()

                assertEquals(true, content.hasStory)
                assertEquals(true, content.hasContact)
                assertEquals(true, content.hasVolunteering)
                assertEquals(true, content.hasTransport)
                assertEquals(true, content.hasPayment)
                assertEquals("https://example.ch/newsletter", content.newsletterUrl)
            }
        }

    @Test
    fun invoke_nothingPublishedYet_promotesNothing() =
        runTest {
            // The normal case for most of this app's life, and the one that has to be right: a tile
            // drawn for an unpublished section would open a screen with nothing on it.
            val repository = FakeContentRepository()
            val useCase = ObserveHomeContentUseCase(repository)

            useCase().test {
                repository.emitStatus(ContentStatus.Ready(bundle = bundle(), updateRequired = false))

                val content = awaitItem()

                assertEquals(false, content.hasStory)
                assertEquals(false, content.hasContact)
                assertEquals(false, content.hasVolunteering)
                assertEquals(false, content.hasTransport)
                assertEquals(false, content.hasPayment)
                assertEquals(null, content.newsletterUrl)
            }
        }

    @Test
    fun invoke_transportSectionPublishedWithNoModes_isNotPromoted() =
        runTest {
            // Present but empty is the state a rolled-back publish leaves behind, and it is not the
            // same as published — the screen behind the tile would have a heading and no timetable.
            val repository = FakeContentRepository()
            val useCase = ObserveHomeContentUseCase(repository)

            useCase().test {
                val festival = fullFestival().copy(transport = Transport(modes = emptyList(), provenance = Provenance.CONFIRMED))

                repository.emitStatus(ContentStatus.Ready(bundle = bundle(festival = festival), updateRequired = false))

                assertEquals(false, awaitItem().hasTransport)
            }
        }

    @Test
    fun invoke_applicationsClosedForThisEdition_stopsPromotingVolunteering() =
        runTest {
            // Recruiting is a campaign rather than a fact, so the tile has to be able to go away
            // without the involvement section being deleted.
            val repository = FakeContentRepository()
            val useCase = ObserveHomeContentUseCase(repository)

            useCase().test {
                val festival = fullFestival().copy(involvement = Involvement(volunteering = null, partnership = null))

                repository.emitStatus(ContentStatus.Ready(bundle = bundle(festival = festival), updateRequired = false))

                assertEquals(false, awaitItem().hasVolunteering)
            }
        }

    @Test
    fun invoke_linksCarryTheDonationPageButNoNewsletter_promotesNoNewsletter() =
        runTest {
            // The list is keyed by id rather than by position, and it really does hold two things.
            val repository = FakeContentRepository()
            val useCase = ObserveHomeContentUseCase(repository)

            useCase().test {
                val festival =
                    fullFestival().copy(
                        links = listOf(InfoLink(id = "don", label = "Faire un don", sublabel = null, url = "https://example.ch/don")),
                    )

                repository.emitStatus(ContentStatus.Ready(bundle = bundle(festival = festival), updateRequired = false))

                assertEquals(null, awaitItem().newsletterUrl)
            }
        }

    // endregion

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
        figureProvenance: Provenance = Provenance.CONFIRMED,
        festival: Festival = bareFestival(),
    ) = ContentBundle(
        festival = festival,
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
                happenings = listOf(happening(), activity()),
                slots = slots,
                partners = emptyList(),
                figures = listOf(Figure(id = "visiteurs", value = "6000", label = "visiteurs", provenance = figureProvenance)),
            ),
        announcements = announcements,
    )

    /**
     * The four fields the loading chain and Accueil are built on, and nothing else — which is also
     * the shape of a real `festival.json` before the association has published its practical
     * sections. Every promoted tile is absent against this fixture, on purpose.
     */
    private fun bareFestival() =
        Festival(
            name = "Yadlo",
            tagline = "Mouille ton corps, arrose ton esprit",
            website = "https://www.yadlo.ch/",
            currentEditionId = "2026",
            minSupportedAppVersion = null,
            social = listOf(SocialLink(id = "instagram", name = "Instagram", url = "https://example.ch/insta")),
        )

    /** The same file with every section Accueil can promote filled in. */
    private fun fullFestival() =
        bareFestival().copy(
            links = listOf(InfoLink(id = "newsletter", label = "Newsletter", sublabel = null, url = "https://example.ch/newsletter")),
            story = Story(foundedYear = 2015, body = "Depuis 2015.", passage = null, provenance = Provenance.CONFIRMED),
            contact =
                Contact(
                    addressLines = emptyList(),
                    phone = null,
                    emails =
                        listOf(
                            Contact.Email(id = "hello", address = "hello@example.ch", label = "Général", responsible = null),
                        ),
                    provenance = Provenance.CONFIRMED,
                ),
            transport =
                Transport(
                    modes =
                        listOf(
                            TransportMode(
                                id = "train",
                                name = "Train",
                                body = "Gare de Préverenges.",
                                facts = emptyList(),
                                links = emptyList(),
                                departures = emptyList(),
                            ),
                        ),
                    provenance = Provenance.CONFIRMED,
                ),
            payment =
                Payment(
                    headline = null,
                    summary = null,
                    methods = listOf(Payment.Method(id = "cash", name = "Espèces", accepted = false)),
                    notes = emptyList(),
                    provenance = Provenance.CONFIRMED,
                ),
            involvement =
                Involvement(
                    volunteering =
                        Involvement.Volunteering(
                            name = "Hot'Staff",
                            body = "Rejoins l'équipe.",
                            perks = emptyList(),
                            signupUrl = "https://example.ch/staff",
                            contactEmailId = "staff",
                            provenance = Provenance.CONFIRMED,
                        ),
                    partnership = null,
                ),
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

    private fun activity() =
        Happening.Activity(
            id = "2026:sup-yoga",
            name = "SUP yoga",
            category = Category(id = "eau", name = "Eau", order = 2),
            description = null,
            images = emptyList(),
            provenance = Provenance.CONFIRMED,
            genres = emptyList(),
            price = null,
            bookingRequired = false,
            bookingUrl = null,
            equipmentProvided = null,
            suitability = null,
            supervised = null,
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
