package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.SlotScaleUiModel
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.SlotSegmentUiModel
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.loudestState
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.slotLiveStateAt
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.stateLabel
import io.nicolaszurbuchen.yadlo.core.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.infra.format.formatAsTimeOfDay
import io.nicolaszurbuchen.yadlo.infra.format.formatMoney
import io.nicolaszurbuchen.yadlo.infra.text.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.price_free
import yadlo.shared.generated.resources.price_from
import yadlo.shared.generated.resources.programme_empty_filter
import yadlo.shared.generated.resources.programme_empty_unpublished
import yadlo.shared.generated.resources.programme_scope_all
import yadlo.shared.generated.resources.programme_scope_catalogue

/**
 * Whatever the selector row is pointing at.
 *
 * **The Catalogue leaves before any day is looked at**, because everything below that point — the
 * axis the bars are measured on, the live states, the day a row belongs to — is about a timetable
 * it does not have. What every scope shares is the selector row and the Category chips, and both
 * are built above the branch so no scope can drift from another on them.
 *
 * The timetable half is one loop over the days in scope, doing exactly the same work per day
 * whether there is one of them or three. That is what makes *Tous* cheap rather than a second
 * layout: a day is already the unit this screen is built out of — the axis is a day's span, a row
 * is a Happening on a day — so three days is that unit three times.
 *
 * Everything is built inside this single function: a UiMapper file is required to hold nothing but
 * the State-to-UiModel extension, so a helper here would have to be local, which Konsist reads as
 * an extra function in the file. The pieces that would otherwise want to be helpers live elsewhere
 * on purpose — `slotLiveStateAt` and `loudestState` beside the states they return, `formatMoney` in
 * `infra/ui` where the fiche finds it too.
 */
fun ProgrammeState.toUiModel(): ProgrammeUiModel {
    val loaded =
        content ?: return ProgrammeUiModel(
            isLoading = true,
            scopes = emptyList(),
            categories = emptyList(),
            scale = null,
            sections = emptyList(),
            catalogue = emptyList(),
            emptyMessage = null,
        )

    // No Slots at all is an edition published with dates and nothing under them, which happens
    // every spring. The whole selector row goes with the list: offering five things to point at
    // when none of them has anything reads as a screen that failed to load.
    if (loaded.slots.isEmpty()) {
        return ProgrammeUiModel(
            isLoading = false,
            scopes = emptyList(),
            categories = emptyList(),
            scale = null,
            sections = emptyList(),
            catalogue = emptyList(),
            emptyMessage = UiText.Resource(Res.string.programme_empty_unpublished),
        )
    }

    // Null only in the frame between the store being built and the first bundle landing, which the
    // loading branch above has already returned for.
    val scope = selectedScope ?: ProgrammeScopeState.AllDays

    val scopes =
        listOf(
            ScopeChipUiModel(
                id = ProgrammeScopeState.Catalogue.id,
                label = UiText.Resource(Res.string.programme_scope_catalogue),
                isSelected = scope is ProgrammeScopeState.Catalogue,
            ),
            ScopeChipUiModel(
                id = ProgrammeScopeState.AllDays.id,
                label = UiText.Resource(Res.string.programme_scope_all),
                isSelected = scope is ProgrammeScopeState.AllDays,
            ),
        ) +
            loaded.days.map { day ->
                ScopeChipUiModel(
                    id = day.id,
                    // Out of the content, so a day the association calls something else keeps
                    // its name and nothing here has to translate a weekday — shortened to fit five
                    // chips on one row. Ven, Sam, Dim, which is how a French weekday is abbreviated
                    // anyway; the full name survives everywhere it has room, including Mon Yadlo's
                    // rail, where *le samedi* is what people think in.
                    label = UiText.Raw(day.name.take(DAY_ABBREVIATION_LENGTH)),
                    isSelected = scope is ProgrammeScopeState.Day && scope.id == day.id,
                )
            }

    val categories =
        loaded.categories.map {
            CategoryChipUiModel(id = it.id, name = it.name, isSelected = it.id in selectedCategoryIds)
        }

    if (scope is ProgrammeScopeState.Catalogue) {
        val entries =
            loaded.catalogue
                // The same reading of an empty selection as the timetable below: *Tout*, not a
                // filter that excludes everything.
                .filter { selectedCategoryIds.isEmpty() || it.categoryId in selectedCategoryIds }
                .map { entry ->
                    CatalogueCardUiModel(
                        id = entry.id,
                        name = entry.name,
                        categoryId = entry.categoryId,
                        categoryName = entry.categoryName,
                        description = entry.description,
                        imageUrl = entry.imageUrl,
                        genres = entry.genres,
                    )
                }

        return ProgrammeUiModel(
            isLoading = false,
            scopes = scopes,
            categories = categories,
            // Nothing here has an hour, so there is no span to write and nothing to measure on it.
            scale = null,
            sections = emptyList(),
            catalogue = entries,
            emptyMessage = if (entries.isEmpty()) UiText.Resource(Res.string.programme_empty_filter) else null,
        )
    }

    val daysInScope =
        when (scope) {
            is ProgrammeScopeState.Day -> loaded.days.filter { it.id == scope.id }
            else -> loaded.days
        }

    // One day means the chip directly above already says which, so a header would say it twice and
    // the single axis it would carry belongs in the chrome, where it has always been.
    val writesHeaders = daysInScope.size > 1

    val sections =
        daysInScope.mapNotNull { day ->
            val daySlots = loaded.slots.filter { it.dayId == day.id }

            // The axis is the day's opening hours widened to cover anything programmed outside them:
            // the beach at Préverenges is public, so the morning yoga runs from 10:00 on a day the
            // site opens at 12:00 and still has to sit on the bar rather than off the left edge of it.
            //
            // Measured across every Slot of the day, never the filtered ones — an axis that rescaled
            // when you tapped a chip would make two rows impossible to compare across a filter
            // change. And per day rather than across the weekend, which is the same rule read the
            // other way: Friday runs 16:00–02:00 and Sunday 12:00–22:00, so one axis over both would
            // squeeze every Sunday bar into the left half of its track.
            val axisStart = (listOf(day.start) + daySlots.map { it.start }).min()
            val axisEnd = (listOf(day.end) + daySlots.map { it.end }).max()
            val axisSpan = axisEnd - axisStart
            val hasAxis = axisSpan.isPositive()

            val rows =
                daySlots
                    // An empty selection is *Tout*, not a filter that excludes everything.
                    .filter { selectedCategoryIds.isEmpty() || it.categoryId in selectedCategoryIds }
                    // One row per Happening, with its hours on it — DECISIONS.md § A row is a
                    // Happening on a day. `groupBy` keeps first-seen order and the Slots arrive
                    // sorted, so the rows come out in the order their first hour starts and each
                    // row's hours are chronological.
                    .groupBy { it.happeningId }
                    .map { (happeningId, slots) ->
                        val first = slots.first()

                        val segments =
                            slots.map { slot ->
                                SlotSegmentUiModel(
                                    id = slot.id,
                                    timeText =
                                        "${slot.start.formatAsTimeOfDay(FESTIVAL_TIME_ZONE)} – " +
                                            slot.end.formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                                    state = slotLiveStateAt(now = now, start = slot.start, end = slot.end),
                                    barStart =
                                        if (hasAxis) {
                                            ((slot.start - axisStart) / axisSpan).toFloat().coerceIn(0f, 1f)
                                        } else {
                                            0f
                                        },
                                    barEnd =
                                        if (hasAxis) {
                                            ((slot.end - axisStart) / axisSpan).toFloat().coerceIn(0f, 1f)
                                        } else {
                                            1f
                                        },
                                )
                            }

                        val state = segments.loudestState()

                        SlotRowUiModel(
                            // The day is in it because the same Happening runs on all three of them,
                            // and a list key that repeats across days is a list that reuses a row's
                            // scroll state for a different day's copy of it. Under *Tous* the two
                            // copies are also on screen at the same time.
                            id = "${day.id}/$happeningId",
                            happeningId = happeningId,
                            name = first.name,
                            categoryId = first.categoryId,
                            categoryName = first.categoryName,
                            priceText =
                                first.price?.let { price ->
                                    // `tiers` is empty exactly when `free` is true, and the content
                                    // validator holds that — so a missing cheapest tier means free too.
                                    val cheapest = price.tiers.minByOrNull { it.amount.amount }
                                    when {
                                        price.free || cheapest == null -> {
                                            UiText.Resource(Res.string.price_free)
                                        }

                                        // "dès CHF 15" rather than the adult price: the Silent Party
                                        // is CHF 25 for adults and CHF 15 under 16, and a row that
                                        // shows only the higher one prices a family out of something
                                        // they can afford.
                                        price.tiers.size > 1 -> {
                                            UiText.Resource(
                                                Res.string.price_from,
                                                listOf(formatMoney(cheapest.amount.amount, cheapest.amount.currency)),
                                            )
                                        }

                                        else -> {
                                            UiText.Raw(formatMoney(cheapest.amount.amount, cheapest.amount.currency))
                                        }
                                    }
                                },
                            stateLabel = state.stateLabel(),
                            state = state,
                            slots = segments,
                        )
                    }

            // A day the filter emptied is absent, not an empty header — see DaySectionUiModel.
            if (rows.isEmpty()) {
                return@mapNotNull null
            }

            DaySectionUiModel(
                id = day.id,
                header =
                    if (hasAxis) {
                        DaySectionHeaderUiModel(
                            name = day.name,
                            scale =
                                SlotScaleUiModel(
                                    startText = axisStart.formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                                    middleText = (axisStart + axisSpan / 2).formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                                    endText = axisEnd.formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                                ),
                        )
                    } else {
                        null
                    },
                rows = rows,
            )
        }

    return ProgrammeUiModel(
        isLoading = false,
        scopes = scopes,
        categories = categories,
        // Built once, above, and then read from whichever place it belongs in: the chrome when the
        // list is one day, the sticky headers when it is several. Two computations of the same span
        // is two places for them to disagree about where a bar starts.
        scale = if (writesHeaders) null else sections.singleOrNull()?.header?.scale,
        sections = if (writesHeaders) sections else sections.map { it.copy(header = null) },
        catalogue = emptyList(),
        emptyMessage = if (sections.isEmpty()) UiText.Resource(Res.string.programme_empty_filter) else null,
    )
}

/**
 * Three, because that is the length of every French weekday abbreviation and the row has five
 * chips to fit — four once the edition adds a day.
 *
 * A truncation rather than an authored short name: the content publishes one name per day and a
 * second one would be a field to keep in sync for a gain of three characters. It does assume the
 * name is a weekday, which every edition since 2015 has published.
 */
private const val DAY_ABBREVIATION_LENGTH = 3
