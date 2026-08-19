package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

import io.nicolaszurbuchen.yadlo.app.design.uimodel.toDietaryTags
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.slotLiveStateAt
import io.nicolaszurbuchen.yadlo.common.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import io.nicolaszurbuchen.yadlo.infra.ui.formatAsDayOfMonth
import io.nicolaszurbuchen.yadlo.infra.ui.formatAsTimeOfDay
import io.nicolaszurbuchen.yadlo.infra.ui.formatMoney
import io.nicolaszurbuchen.yadlo.infra.ui.monthName
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.happening_booking_action
import yadlo.shared.generated.resources.happening_booking_required
import yadlo.shared.generated.resources.happening_fact_equipment_not_provided
import yadlo.shared.generated.resources.happening_fact_equipment_provided
import yadlo.shared.generated.resources.happening_fact_supervised
import yadlo.shared.generated.resources.happening_link_website
import yadlo.shared.generated.resources.happening_price_deposit
import yadlo.shared.generated.resources.price_free
import yadlo.shared.generated.resources.slot_state_ending
import yadlo.shared.generated.resources.slot_state_over
import yadlo.shared.generated.resources.slot_state_running
import yadlo.shared.generated.resources.slot_state_starts_in_minutes

/**
 * One Happening, flattened into the fiche template.
 *
 * As on the Programme, everything is built inside this single function — a UiMapper file may hold
 * nothing but the State-to-UiModel extension, so there are no helpers to lift the repetition out.
 * `blank` is a local value rather than a function for exactly that reason, and it doubles as the
 * shape both early exits return.
 */
fun HappeningState.toUiModel(): HappeningUiModel {
    val blank =
        HappeningUiModel(
            // Loading and missing are read off the State's two fields rather than off a null
            // detail, which on its own cannot tell "not yet" from "no longer".
            isLoading = !isLoaded,
            isMissing = isLoaded && detail == null,
            title = "",
            categoryId = "",
            categoryLabel = "",
            imageUrl = null,
            description = null,
            tags = emptyList(),
            dietary = emptyList(),
            slots = emptyList(),
            price = null,
            booking = null,
            facts = emptyList(),
            menu = emptyList(),
            links = emptyList(),
            wishlisted = null,
        )

    val loaded = detail ?: return blank

    return blank.copy(
        title = loaded.name,
        imageUrl = loaded.imageUrl,
        dietary = loaded.dietary.toDietaryTags(),
        categoryId = loaded.categoryId,
        // Written out above the title, in the Category's own colour but never only in it.
        categoryLabel = loaded.categoryName.uppercase(),
        description = loaded.description,
        tags = loaded.tags,
        slots =
            loaded.slots.map { slot ->
                val state = slotLiveStateAt(now = now, start = slot.start, end = slot.end)

                HappeningSlotUiModel(
                    id = slot.id,
                    dayName = slot.dayName,
                    // Off the FestivalDay's own opening instant, never off the Slot's start: the
                    // Silent Party runs to 02:00 and its row says samedi, not dimanche.
                    dayNumber = slot.dayStart.formatAsDayOfMonth(FESTIVAL_TIME_ZONE),
                    monthName = slot.dayStart.monthName(FESTIVAL_TIME_ZONE),
                    timeText =
                        "${slot.start.formatAsTimeOfDay(FESTIVAL_TIME_ZONE)} – " +
                            slot.end.formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                    // The same words as the Programme row this fiche was opened from, built the
                    // same way. Both mappers write them out because a UiMapper may hold nothing
                    // but its own State-to-UiModel function, so there is nowhere shared to put it
                    // that is not a UiText-returning helper in `infra/ui` — which would be a
                    // formatting concern pretending to be a copy decision.
                    stateLabel =
                        when (state) {
                            SlotLiveStateUiModel.Upcoming -> {
                                null
                            }

                            is SlotLiveStateUiModel.StartingSoon -> {
                                UiText.Resource(
                                    Res.string.slot_state_starts_in_minutes,
                                    listOf(state.startsIn.inWholeMinutes.coerceAtLeast(1).toString()),
                                )
                            }

                            is SlotLiveStateUiModel.Running -> {
                                UiText.Resource(Res.string.slot_state_running)
                            }

                            is SlotLiveStateUiModel.Ending -> {
                                UiText.Resource(
                                    Res.string.slot_state_ending,
                                    listOf(state.endsIn.inWholeMinutes.coerceAtLeast(1).toString()),
                                )
                            }

                            SlotLiveStateUiModel.Over -> {
                                UiText.Resource(Res.string.slot_state_over)
                            }
                        },
                    state = state,
                    planned = slot.planned,
                )
            },
        price =
            loaded.price?.let { price ->
                HappeningPriceUiModel(
                    // Free is a tier saying "Gratuit" rather than an absent list, so the section
                    // renders as one shape and a free activity still answers the question out loud.
                    tiers =
                        if (price.free || price.tiers.isEmpty()) {
                            listOf(
                                HappeningPriceTierUiModel(
                                    label = null,
                                    amount = UiText.Resource(Res.string.price_free),
                                ),
                            )
                        } else {
                            price.tiers.map { tier ->
                                HappeningPriceTierUiModel(
                                    label = tier.label,
                                    amount =
                                        UiText.Raw(
                                            formatMoney(tier.amount.amount, tier.amount.currency) +
                                                tier.per?.let { " / $it" }.orEmpty(),
                                        ),
                                )
                            }
                        },
                    deposit =
                        price.deposit?.let { deposit ->
                            UiText.Resource(
                                Res.string.happening_price_deposit,
                                listOf(formatMoney(deposit.amount.amount, deposit.amount.currency)),
                            )
                        },
                    depositNote = price.deposit?.note,
                )
            },
        booking =
            if (loaded.bookingRequired) {
                HappeningBookingUiModel(
                    label =
                        if (loaded.bookingUrl != null) {
                            UiText.Resource(Res.string.happening_booking_action)
                        } else {
                            // Nothing to tap. Saying so is still better than silence: someone who
                            // turns up without a ticket has lost the evening, not a tap.
                            UiText.Resource(Res.string.happening_booking_required)
                        },
                    url = loaded.bookingUrl,
                )
            } else {
                null
            },
        facts =
            listOfNotNull(
                loaded.suitability?.let { UiText.Raw(it) },
                when (loaded.equipmentProvided) {
                    true -> UiText.Resource(Res.string.happening_fact_equipment_provided)
                    false -> UiText.Resource(Res.string.happening_fact_equipment_not_provided)
                    null -> null
                },
                // Only the positive. "Non encadré" on a slackline is a warning the content never
                // wrote, and an absent flag means unknown rather than unsupervised.
                if (loaded.supervised == true) UiText.Resource(Res.string.happening_fact_supervised) else null,
            ),
        menu =
            loaded.menu.map { group ->
                HappeningMenuGroupUiModel(
                    id = group.id,
                    name = group.name,
                    description = group.description,
                    source = group.source,
                    items =
                        group.items.map { item ->
                            HappeningMenuItemUiModel(
                                name = item.name,
                                priceText = item.price?.let { formatMoney(it.amount, it.currency) },
                                description = item.description,
                                // An item's marks describe that item alone, so they are written
                                // beside it rather than folded into the stand's own tags.
                                dietary = item.marks.toDietaryTags(),
                            )
                        },
                )
            },
        links =
            loaded.links.map { link ->
                HappeningLinkUiModel(
                    // Brand names are not copy and do not translate, so they are written here
                    // rather than kept in strings.xml. An unknown type falls back to what the
                    // content authored, which is the point of `type` staying a String.
                    label =
                        when (link.type) {
                            "website" -> UiText.Resource(Res.string.happening_link_website)
                            "instagram" -> UiText.Raw("Instagram")
                            "facebook" -> UiText.Raw("Facebook")
                            "tiktok" -> UiText.Raw("TikTok")
                            "youtube" -> UiText.Raw("YouTube")
                            "soundcloud" -> UiText.Raw("SoundCloud")
                            "spotify" -> UiText.Raw("Spotify")
                            else -> UiText.Raw(link.type)
                        },
                    url = link.url,
                )
            },
        wishlisted = loaded.wishlisted,
    )
}
