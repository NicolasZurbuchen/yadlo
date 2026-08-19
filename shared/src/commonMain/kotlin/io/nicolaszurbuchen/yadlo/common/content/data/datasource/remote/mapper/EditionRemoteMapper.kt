package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.mapper

import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.CategoryDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.EditionDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.FestivalDayDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.FigureDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.HappeningDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.ImageDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.LinkDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.MenuGroupDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.MoneyDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.PartnerDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.PartnerTierDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.PriceDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.SlotDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.VenueDto
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Figure
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Image
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Link
import io.nicolaszurbuchen.yadlo.common.content.domain.model.MenuGroup
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Money
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Partner
import io.nicolaszurbuchen.yadlo.common.content.domain.model.PartnerTier
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Price
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Slot
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Venue
import io.nicolaszurbuchen.yadlo.common.error.AppError
import io.nicolaszurbuchen.yadlo.common.error.AppException
import io.nicolaszurbuchen.yadlo.infra.network.CONTENT_BASE_URL

private const val KIND_ARTIST = "artist"
private const val KIND_ACTIVITY = "activity"
private const val KIND_STAND = "stand"

/**
 * Turns the published bundle into the resolved graph the screens read: a Happening carries its
 * Category, a Slot carries its Happening and its FestivalDay. The joins happen once, here.
 *
 * **A reference that does not resolve rejects the whole bundle.** A Slot whose happening is missing
 * is not a Slot with a hole to render around — it is evidence the file is not internally consistent,
 * and a festival app that quietly drops rows is worse than one that says it could not read the
 * programme. `validate.js` is what keeps this from ever firing in production.
 */
fun EditionDto.toDomain(): Edition {
    val mappedCategories = categories.map { it.toDomain() }
    val mappedDays = days.map { it.toDomain() }
    val categoriesById = mappedCategories.associateBy { it.id }

    val mappedHappenings = happenings.map { it.toDomain(categoriesById) }
    val happeningsById = mappedHappenings.associateBy { it.id }
    val daysById = mappedDays.associateBy { it.id }

    return Edition(
        id = id,
        year = year,
        name = name,
        venue = venue.toDomain(),
        days = mappedDays,
        categories = mappedCategories,
        happenings = mappedHappenings,
        slots = slots.map { it.toDomain(happeningsById, daysById) },
        partners = partners.map { it.toDomain() },
        figures = figures.map { it.toDomain() },
    )
}

private fun VenueDto.toDomain(): Venue =
    Venue(
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        provenance = provenance.toProvenanceEnum("venue.provenance"),
    )

private fun FestivalDayDto.toDomain(): FestivalDay =
    FestivalDay(
        id = id,
        name = name,
        date = date,
        start = start.toInstantValue("day[$id].start"),
        end = end.toInstantValue("day[$id].end"),
        provenance = provenance.toProvenanceEnum("day[$id].provenance"),
    )

private fun CategoryDto.toDomain(): Category =
    Category(
        id = id,
        name = name,
        order = order,
    )

private fun HappeningDto.toDomain(categoriesById: Map<String, Category>): Happening {
    val resolvedCategory =
        categoriesById[category]
            ?: throw AppException(AppError.Content.UnresolvedReference("happening[$id].category", category))
    val resolvedImages = images.map { it.toDomain() }
    val resolvedProvenance = provenance.toProvenanceEnum("happening[$id].provenance")

    return when (kind) {
        KIND_ARTIST -> {
            val payload = artist ?: throw AppException(AppError.Content.MalformedField("happening[$id].artist", "absent"))
            Happening.Artist(
                id = id,
                name = name,
                category = resolvedCategory,
                description = description,
                images = resolvedImages,
                provenance = resolvedProvenance,
                genres = payload.genres,
                links = payload.links.map { it.toDomain() },
            )
        }

        KIND_ACTIVITY -> {
            val payload = activity ?: throw AppException(AppError.Content.MalformedField("happening[$id].activity", "absent"))
            Happening.Activity(
                id = id,
                name = name,
                category = resolvedCategory,
                description = description,
                images = resolvedImages,
                provenance = resolvedProvenance,
                genres = payload.genres,
                price = payload.price?.toDomain(id),
                bookingRequired = payload.bookingRequired,
                bookingUrl = payload.bookingUrl,
                equipmentProvided = payload.equipmentProvided,
                suitability = payload.suitability,
                supervised = payload.supervised,
            )
        }

        KIND_STAND -> {
            val payload = stand ?: throw AppException(AppError.Content.MalformedField("happening[$id].stand", "absent"))
            Happening.Stand(
                id = id,
                name = name,
                category = resolvedCategory,
                description = description,
                images = resolvedImages,
                provenance = resolvedProvenance,
                offering = payload.offering,
                links = payload.links.map { it.toDomain() },
                menu = payload.menu.map { it.toDomain(id) },
            )
        }

        else -> {
            throw AppException(AppError.Content.MalformedField("happening[$id].kind", kind))
        }
    }
}

private fun SlotDto.toDomain(
    happeningsById: Map<String, Happening>,
    daysById: Map<String, FestivalDay>,
): Slot =
    Slot(
        id = id,
        happening =
            happeningsById[happeningId]
                ?: throw AppException(AppError.Content.UnresolvedReference("slot[$id].happeningId", happeningId)),
        day =
            daysById[dayId]
                ?: throw AppException(AppError.Content.UnresolvedReference("slot[$id].dayId", dayId)),
        start = start.toInstantValue("slot[$id].start"),
        end = end.toInstantValue("slot[$id].end"),
        provenance = provenance.toProvenanceEnum("slot[$id].provenance"),
    )

private fun PriceDto.toDomain(happeningId: String): Price =
    Price(
        free = free,
        tiers =
            tiers.map { tier ->
                Price.Tier(
                    label = tier.label,
                    amount = Money(amount = tier.amount, currency = tier.currency),
                    per = tier.per,
                )
            },
        deposit =
            deposit?.let {
                Price.Deposit(
                    amount = Money(amount = it.amount, currency = it.currency),
                    note = it.note,
                )
            },
        provenance = provenance.toProvenanceEnum("happening[$happeningId].activity.price.provenance"),
    )

private fun MenuGroupDto.toDomain(happeningId: String): MenuGroup =
    MenuGroup(
        id = id,
        name = name,
        description = description,
        source = source,
        items =
            items.map { item ->
                MenuGroup.Item(
                    name = item.name,
                    price = item.price?.toDomain(),
                    description = item.description,
                    marks = item.marks,
                    provenance =
                        item.provenance.toProvenanceEnum(
                            "happening[$happeningId].stand.menu[$id].items[${item.name}].provenance",
                        ),
                )
            },
    )

private fun MoneyDto.toDomain(): Money =
    Money(
        amount = amount,
        currency = currency,
    )

private fun PartnerTierDto.toDomain(): PartnerTier =
    PartnerTier(
        id = id,
        name = name,
        order = order,
        members = members.map { it.toDomain() },
        provenance = provenance.toProvenanceEnum("partnerTier[$id].provenance"),
    )

private fun PartnerDto.toDomain(): Partner =
    Partner(
        id = id,
        name = name,
        url = url,
        logo = logo?.toDomain(),
    )

private fun FigureDto.toDomain(): Figure =
    Figure(
        id = id,
        value = value,
        label = label,
        provenance = provenance.toProvenanceEnum("figure[$id].provenance"),
    )

/**
 * The content authors a path relative to the content root — `shared/images/artists/alf.webp` — and
 * the domain hands out something an image loader can fetch. The join happens here, at the same
 * boundary that resolves every other reference in the bundle, so nothing above the data layer has to
 * know the content is published anywhere in particular.
 *
 * Relative to the *root* rather than to the file that carried it: `edition.json` sits two directories
 * down, and a path resolved against its own location would point inside `editions/2026/` while
 * `validate.js` and the picture bank both mean the root. An absolute src is left alone, which is what
 * lets one photograph come from somewhere else without a schema change.
 */
private fun ImageDto.toDomain(): Image =
    Image(
        url = if (src.startsWith(ABSOLUTE_PREFIX)) src else CONTENT_BASE_URL + src,
        credit = credit,
    )

/** `http://` is rejected by the validator, so https is the only absolute form that can arrive. */
private const val ABSOLUTE_PREFIX = "https://"

private fun LinkDto.toDomain(): Link =
    Link(
        type = type,
        url = url,
    )
