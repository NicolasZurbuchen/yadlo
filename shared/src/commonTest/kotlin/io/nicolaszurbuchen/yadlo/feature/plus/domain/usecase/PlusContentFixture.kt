package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.MenuGroup
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Slot
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Venue
import kotlin.time.Instant

/**
 * The scaffolding eight Plus use cases would otherwise each rebuild.
 *
 * Only the parts a test is about are parameters; everything else is the 2026 shape. A test that
 * says `ready(payment = …)` is stating what it is about, and a `Venue` nobody asserts on never
 * appears in it.
 */
internal fun ready(
    festival: Festival = festival(),
    happenings: List<Happening> = emptyList(),
    days: List<FestivalDay> = emptyList(),
    slots: List<Slot> = emptyList(),
) = ContentStatus.Ready(
    bundle =
        ContentBundle(
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
                    days = days,
                    categories = listOf(RESTAURATION, CREATEURS),
                    happenings = happenings,
                    slots = slots,
                    partners = emptyList(),
                    figures = emptyList(),
                ),
            announcements = emptyList(),
        ),
    updateRequired = false,
)

/** Only the four fields with no default, so a caller names exactly the section it is testing. */
internal fun festival(block: Festival.() -> Festival = { this }) =
    Festival(
        name = "Yadlo",
        tagline = "Mouille ton corps, arrose ton esprit",
        currentEditionId = "2026",
        minSupportedAppVersion = null,
        social = emptyList(),
    ).block()

internal fun stand(
    id: String,
    name: String = id,
    category: Category = RESTAURATION,
    offering: String? = null,
    marks: List<String> = emptyList(),
    itemMarks: List<String> = emptyList(),
) = Happening.Stand(
    id = id,
    name = name,
    category = category,
    description = null,
    images = emptyList(),
    provenance = Provenance.CONFIRMED,
    offering = offering,
    marks = marks,
    links = emptyList(),
    menu =
        if (itemMarks.isEmpty()) {
            emptyList()
        } else {
            listOf(
                MenuGroup(
                    id = "plats",
                    name = "Plats",
                    description = null,
                    source = null,
                    items =
                        itemMarks.map { mark ->
                            MenuGroup.Item(
                                name = "Plat $mark",
                                price = null,
                                description = null,
                                marks = listOf(mark),
                                provenance = Provenance.UNVERIFIED,
                            )
                        },
                ),
            )
        },
)

internal fun day(
    id: String,
    name: String,
    start: String,
    end: String,
    provenance: Provenance = Provenance.CONFIRMED,
) = FestivalDay(
    id = id,
    name = name,
    date = start.take(DATE_LENGTH),
    start = Instant.parse(start),
    end = Instant.parse(end),
    provenance = provenance,
)

internal fun slot(
    id: String,
    day: FestivalDay,
    start: String,
    end: String,
) = Slot(
    id = id,
    happening = ARTIST,
    day = day,
    start = Instant.parse(start),
    end = Instant.parse(end),
    provenance = Provenance.CONFIRMED,
)

internal val RESTAURATION = Category(id = "restauration", name = "Restauration", order = 6)
internal val CREATEURS = Category(id = "createurs", name = "Créateurs", order = 7)

private val MUSIQUE = Category(id = "musique", name = "Musique", order = 1)

/** Slots need a Happening and no Plus screen reads it, so one stands in for all of them. */
private val ARTIST =
    Happening.Artist(
        id = "dubside",
        name = "Dubside",
        category = MUSIQUE,
        description = null,
        images = emptyList(),
        provenance = Provenance.CONFIRMED,
        genres = emptyList(),
        links = emptyList(),
    )

/** `2026-07-10` out of the front of an ISO instant. */
private const val DATE_LENGTH = 10
