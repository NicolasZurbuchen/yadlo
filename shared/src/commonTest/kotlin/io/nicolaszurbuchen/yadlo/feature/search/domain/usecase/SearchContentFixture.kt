package io.nicolaszurbuchen.yadlo.feature.search.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FaqEntry
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.MenuGroup
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Partner
import io.nicolaszurbuchen.yadlo.common.content.domain.model.PartnerTier
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Venue
import kotlin.time.Instant

/**
 * The corpus the search tests are written against — deliberately the 2026 edition in miniature: an
 * artist with genres, an activity with a suitability line, and a stand with a menu, because those
 * are the three shapes a query can reach a Happening through.
 */
internal fun searchable(
    festival: Festival = festival(),
    happenings: List<Happening> = emptyList(),
    days: List<FestivalDay> = emptyList(),
    partners: List<PartnerTier> = emptyList(),
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
                    categories = listOf(MUSIQUE, EAU, RESTAURATION, CREATEURS),
                    happenings = happenings,
                    slots = emptyList(),
                    partners = partners,
                    figures = emptyList(),
                ),
            announcements = emptyList(),
        ),
    updateRequired = false,
)

internal fun festival(block: Festival.() -> Festival = { this }) =
    Festival(
        name = "Yadlo",
        tagline = "Mouille ton corps, arrose ton esprit",
        website = "https://www.yadlo.ch/",
        currentEditionId = "2026",
        minSupportedAppVersion = null,
        social = emptyList(),
    ).block()

internal fun artist(
    id: String,
    name: String = id,
    description: String? = null,
    genres: List<String> = emptyList(),
) = Happening.Artist(
    id = id,
    name = name,
    category = MUSIQUE,
    description = description,
    images = emptyList(),
    provenance = Provenance.CONFIRMED,
    genres = genres,
    links = emptyList(),
)

internal fun activity(
    id: String,
    name: String = id,
    description: String? = null,
    genres: List<String> = emptyList(),
    suitability: String? = null,
) = Happening.Activity(
    id = id,
    name = name,
    category = EAU,
    description = description,
    images = emptyList(),
    provenance = Provenance.CONFIRMED,
    genres = genres,
    price = null,
    bookingRequired = false,
    bookingUrl = null,
    equipmentProvided = null,
    suitability = suitability,
    supervised = null,
)

internal fun stand(
    id: String,
    name: String = id,
    category: Category = RESTAURATION,
    description: String? = null,
    offering: String? = null,
    dishes: List<Pair<String, String?>> = emptyList(),
) = Happening.Stand(
    id = id,
    name = name,
    category = category,
    description = description,
    images = emptyList(),
    provenance = Provenance.CONFIRMED,
    offering = offering,
    links = emptyList(),
    menu =
        if (dishes.isEmpty()) {
            emptyList()
        } else {
            listOf(
                MenuGroup(
                    id = "plats",
                    name = "Plats",
                    description = null,
                    source = null,
                    items =
                        dishes.map { (dishName, dishDescription) ->
                            MenuGroup.Item(
                                name = dishName,
                                price = null,
                                description = dishDescription,
                                marks = emptyList(),
                                provenance = Provenance.UNVERIFIED,
                            )
                        },
                ),
            )
        },
)

internal fun question(
    id: String,
    text: String,
    answer: String = "…",
) = FaqEntry(id = id, question = text, answer = answer, provenance = Provenance.CONFIRMED)

internal fun day(id: String) =
    FestivalDay(
        id = id,
        name = id,
        date = "2026-07-10",
        start = Instant.parse("2026-07-10T14:00:00Z"),
        end = Instant.parse("2026-07-11T00:00:00Z"),
        provenance = Provenance.CONFIRMED,
    )

internal fun tier(member: String) =
    PartnerTier(
        id = "principaux",
        name = "Principaux",
        order = 1,
        members = listOf(Partner(id = member, name = member, url = null, logo = null)),
        provenance = Provenance.CONFIRMED,
    )

internal val MUSIQUE = Category(id = "musique", name = "Musique", order = 1)
internal val EAU = Category(id = "eau", name = "Sports d'eau", order = 2)
internal val RESTAURATION = Category(id = "restauration", name = "Restauration", order = 6)
internal val CREATEURS = Category(id = "createurs", name = "Créateurs", order = 7)
