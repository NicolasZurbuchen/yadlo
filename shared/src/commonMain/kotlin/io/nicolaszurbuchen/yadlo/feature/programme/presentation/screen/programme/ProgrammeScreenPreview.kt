package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.app.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotScaleUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotSegmentUiModel
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.price_free
import yadlo.shared.generated.resources.price_from
import yadlo.shared.generated.resources.programme_empty_filter
import yadlo.shared.generated.resources.programme_empty_unpublished
import yadlo.shared.generated.resources.programme_scope_all
import yadlo.shared.generated.resources.programme_scope_catalogue
import yadlo.shared.generated.resources.slot_state_ending
import yadlo.shared.generated.resources.slot_state_over
import yadlo.shared.generated.resources.slot_state_running
import yadlo.shared.generated.resources.slot_state_starts_in_minutes
import kotlin.time.Duration.Companion.minutes

/**
 * The Saturday at 15:45, which is the moment the prototype was argued from: five things running,
 * two ending, Dubside fifteen minutes out and the morning already dimmed. Then the same weekend
 * under *Tous*, where two days carry their own header and their own axis; then the Catalogue, the
 * view the tab opens on in May; then the two empties.
 *
 * Written out rather than mapped from a ProgrammeState, because a preview may not import the domain
 * layer and that is where ProgrammeContent lives.
 */
private class ProgrammeScreenStateProvider : PreviewParameterProvider<ProgrammeUiModel> {
    override val values =
        sequenceOf(
            // Before the first bundle reaches the screen.
            ProgrammeUiModel(
                isLoading = true,
                scopes = emptyList(),
                categories = emptyList(),
                scale = null,
                sections = emptyList(),
                catalogue = emptyList(),
                emptyMessage = null,
            ),
            // One day: no header, and the axis is written once in the chrome.
            ProgrammeUiModel(
                isLoading = false,
                scopes = scopes(selected = ProgrammeScopeState.Day("2026:sat")),
                categories = categories(),
                scale = SlotScaleUiModel(startText = "10:00", middleText = "18:00", endText = "03:00"),
                sections =
                    listOf(
                        DaySectionUiModel(id = "2026:sat", header = null, rows = saturdayAtQuarterToFour()),
                    ),
                catalogue = emptyList(),
                emptyMessage = null,
            ),
            // *Tous*: nothing in the chrome, and each day's own reading pinned to its own header.
            // Friday runs 16:00–02:00 and Saturday 12:00–03:00, which is why one scale could not have
            // been right about both.
            ProgrammeUiModel(
                isLoading = false,
                scopes = scopes(selected = ProgrammeScopeState.AllDays),
                categories = categories(),
                scale = null,
                sections =
                    listOf(
                        DaySectionUiModel(
                            id = "2026:fri",
                            header =
                                DaySectionHeaderUiModel(
                                    name = "Vendredi",
                                    scale = SlotScaleUiModel(startText = "16:00", middleText = "21:00", endText = "02:00"),
                                ),
                            rows = fridayAlreadyOver(),
                        ),
                        DaySectionUiModel(
                            id = "2026:sat",
                            header =
                                DaySectionHeaderUiModel(
                                    name = "Samedi",
                                    scale = SlotScaleUiModel(startText = "10:00", middleText = "18:00", endText = "03:00"),
                                ),
                            rows = saturdayAtQuarterToFour(),
                        ),
                    ),
                catalogue = emptyList(),
                emptyMessage = null,
            ),
            // The Catalogue: no axis and no headers, because nothing on it has an hour. An Artist
            // with its genres, an Activity with none, and the longest description in the edition
            // against the shortest — which is the pair the stagger exists for.
            ProgrammeUiModel(
                isLoading = false,
                scopes = scopes(selected = ProgrammeScopeState.Catalogue),
                categories = categories(),
                scale = null,
                sections = emptyList(),
                catalogue = theCatalogue(),
                emptyMessage = null,
            ),
            // A filter that matched nothing — the chips stay, since the way out is to change them.
            ProgrammeUiModel(
                isLoading = false,
                scopes = scopes(selected = ProgrammeScopeState.Day("2026:sat")),
                categories = categories(selectedId = "silent"),
                scale = null,
                sections = emptyList(),
                catalogue = emptyList(),
                emptyMessage = UiText.Resource(Res.string.programme_empty_filter),
            ),
            // Spring: dates published, nothing under them. No selector row at all, because none of
            // its five chips can help and offering them reads as a screen that failed to load.
            ProgrammeUiModel(
                isLoading = false,
                scopes = emptyList(),
                categories = emptyList(),
                scale = null,
                sections = emptyList(),
                catalogue = emptyList(),
                emptyMessage = UiText.Resource(Res.string.programme_empty_unpublished),
            ),
        )

    private fun scopes(selected: ProgrammeScopeState) =
        listOf(
            ScopeChipUiModel(
                id = ProgrammeScopeState.Catalogue.id,
                label = UiText.Resource(Res.string.programme_scope_catalogue),
                isSelected = selected is ProgrammeScopeState.Catalogue,
            ),
            ScopeChipUiModel(
                id = ProgrammeScopeState.AllDays.id,
                label = UiText.Resource(Res.string.programme_scope_all),
                isSelected = selected is ProgrammeScopeState.AllDays,
            ),
            day(id = "2026:fri", name = "Ven", selected = selected),
            day(id = "2026:sat", name = "Sam", selected = selected),
            day(id = "2026:sun", name = "Dim", selected = selected),
        )

    private fun day(
        id: String,
        name: String,
        selected: ProgrammeScopeState,
    ) = ScopeChipUiModel(
        id = id,
        label = UiText.Raw(name),
        isSelected = selected is ProgrammeScopeState.Day && selected.id == id,
    )

    private fun categories(selectedId: String? = null) =
        listOf(
            CategoryChipUiModel(id = "musique", name = "Musique", isSelected = selectedId == "musique"),
            CategoryChipUiModel(id = "silent", name = "Silent Party", isSelected = selectedId == "silent"),
            CategoryChipUiModel(id = "eau", name = "Sur l'eau", isSelected = selectedId == "eau"),
            CategoryChipUiModel(id = "terre", name = "Sur terre", isSelected = selectedId == "terre"),
            CategoryChipUiModel(id = "enfants", name = "Enfants", isSelected = selectedId == "enfants"),
        )

    private fun theCatalogue() =
        listOf(
            CatalogueCardUiModel(
                id = "dubside",
                name = "Dubside",
                categoryId = "musique",
                categoryName = "Musique",
                description =
                    "Duo lausannois formé en 2019, connu pour ses sets aux frontières de la techno " +
                        "mélodique et de la house profonde.",
                imageUrl = null,
                genres = listOf("Techno-house"),
            ),
            CatalogueCardUiModel(
                id = "gauthier-quenis",
                name = "Gauthier Quenis",
                categoryId = "musique",
                categoryName = "Musique",
                // The shortest description in the edition, beside the longest below it.
                description = "Le retour des tubes qui ont bercé une génération.",
                imageUrl = null,
                genres = listOf("Années 2000"),
            ),
            CatalogueCardUiModel(
                id = "silent-party",
                name = "Silent Party",
                categoryId = "silent",
                categoryName = "Silent Party",
                description =
                    "Trois ambiances, un seul dancefloor et des centaines de casques illuminés. " +
                        "Choisissez votre canal et changez-en quand l'envie vous prend.",
                imageUrl = null,
                genres = listOf("All style"),
            ),
            // No genres at all, which is most of the Activities: the band and its rule are absent
            // rather than empty.
            CatalogueCardUiModel(
                id = "sup-yoga",
                name = "SUP Yoga",
                categoryId = "eau",
                categoryName = "Sur l'eau",
                description = "Une séance de yoga sur paddle, au large de la plage.",
                imageUrl = null,
                genres = emptyList(),
            ),
            CatalogueCardUiModel(
                id = "mur-de-grimpe",
                name = "Le mur de grimpe",
                categoryId = "enfants",
                categoryName = "Enfants",
                description =
                    "Envie de prendre de la hauteur ? Un mur de grimpe encadré par Totem Escalade, " +
                        "pour découvrir l'escalade en toute sécurité.",
                imageUrl = null,
                genres = emptyList(),
            ),
        )

    /**
     * Friday, read from the same Saturday afternoon as everything else — so every row on it is Over
     * and the whole section is dimmed, which is the state *Tous* spends most of the weekend in.
     *
     * **Its own rows rather than a slice of Saturday's.** A Slot id is Edition- *and* day-qualified
     * precisely so one cannot stand in for another, and *Tous* flattens every section into one
     * LazyColumn keyed by row id: two sections sharing an id is a duplicate key, which throws before
     * anything is drawn.
     */
    private fun fridayAlreadyOver() =
        listOf(
            row(
                id = "2026:mini-escape-game-fri",
                name = "Mini Escape Game",
                categoryId = "terre",
                categoryName = "Sur terre",
                priceText = UiText.Resource(Res.string.price_free),
                stateLabel = UiText.Resource(Res.string.slot_state_over),
                state = SlotLiveStateUiModel.Over,
                // Fractions of Friday's own ten-hour window, which is what the section header carries.
                slots = listOf(segment("16:00 – 19:00", SlotLiveStateUiModel.Over, 0f, 0.3f)),
            ),
            row(
                id = "2026:carlos-willengton-fri",
                name = "Carlos Willengton",
                categoryId = "musique",
                categoryName = "Musique",
                priceText = null,
                stateLabel = UiText.Resource(Res.string.slot_state_over),
                state = SlotLiveStateUiModel.Over,
                slots = listOf(segment("23:30 – 01:30", SlotLiveStateUiModel.Over, 0.75f, 0.95f)),
            ),
        )

    /** Bar fractions are measured against the Saturday axis in the scale above: 10:00 to 03:00. */
    private fun saturdayAtQuarterToFour() =
        listOf(
            row(
                id = "2026:acro-yoga-sat",
                name = "Acro-yoga",
                categoryId = "terre",
                categoryName = "Sur terre",
                priceText = UiText.Resource(Res.string.price_free),
                stateLabel = UiText.Resource(Res.string.slot_state_over),
                state = SlotLiveStateUiModel.Over,
                slots = listOf(segment("10:00 – 11:00", SlotLiveStateUiModel.Over, 0f, 0.059f)),
            ),
            row(
                id = "2026:thalasso-sat",
                name = "Thalassothérapie",
                categoryId = "musique",
                categoryName = "Musique",
                priceText = null,
                stateLabel = UiText.Resource(Res.string.slot_state_ending, listOf("15")),
                state = SlotLiveStateUiModel.Ending(endsIn = 15.minutes, progress = 0.875f),
                slots =
                    listOf(
                        segment(
                            "14:00 – 16:00",
                            SlotLiveStateUiModel.Ending(endsIn = 15.minutes, progress = 0.875f),
                            0.235f,
                            0.353f,
                        ),
                    ),
            ),
            row(
                id = "2026:gladiasup-sat",
                name = "GladiaSUP",
                categoryId = "eau",
                categoryName = "Sur l'eau",
                priceText = UiText.Raw("CHF 5"),
                stateLabel = UiText.Resource(Res.string.slot_state_running),
                state = SlotLiveStateUiModel.Running(progress = 0.53f),
                slots =
                    listOf(
                        segment("12:00 – 19:00", SlotLiveStateUiModel.Running(progress = 0.53f), 0.118f, 0.529f),
                    ),
            ),
            // The case the merge exists for: three separate hours of one activity, the first over
            // while the row is not, on one track — DECISIONS.md § A row is a Happening on a day.
            row(
                id = "2026:sat/sup-yoga",
                name = "SUP Yoga",
                categoryId = "eau",
                categoryName = "Sur l'eau",
                priceText = UiText.Raw("CHF 20"),
                stateLabel = UiText.Resource(Res.string.slot_state_starts_in_minutes, listOf("45")),
                state = SlotLiveStateUiModel.StartingSoon(startsIn = 45.minutes),
                slots =
                    listOf(
                        segment("14:00 – 15:00", SlotLiveStateUiModel.Over, 0.235f, 0.294f),
                        segment("16:00 – 17:00", SlotLiveStateUiModel.StartingSoon(45.minutes), 0.353f, 0.412f),
                        segment("18:00 – 19:00", SlotLiveStateUiModel.Upcoming, 0.471f, 0.529f),
                    ),
            ),
            row(
                id = "2026:dubside-sat",
                name = "Dubside",
                categoryId = "musique",
                categoryName = "Musique",
                priceText = null,
                stateLabel = UiText.Resource(Res.string.slot_state_starts_in_minutes, listOf("15")),
                state = SlotLiveStateUiModel.StartingSoon(startsIn = 15.minutes),
                slots =
                    listOf(
                        segment("16:00 – 18:00", SlotLiveStateUiModel.StartingSoon(15.minutes), 0.353f, 0.471f),
                    ),
            ),
            row(
                id = "2026:silent-party-sat",
                name = "Silent Party",
                categoryId = "silent",
                categoryName = "Silent Party",
                // Two tiers, so the row shows the one that lets a family in rather than the adult
                // price — CHF 25 adulte, CHF 15 moins de 16 ans.
                priceText = UiText.Resource(Res.string.price_from, listOf("CHF 15")),
                stateLabel = null,
                state = SlotLiveStateUiModel.Upcoming,
                slots = listOf(segment("20:00 – 02:00", SlotLiveStateUiModel.Upcoming, 0.588f, 0.941f)),
            ),
            row(
                id = "2026:coin-enfant-sat",
                name = "Coin enfant — maquillage, bricolage et mur de grimpe",
                categoryId = "enfants",
                categoryName = "Enfants",
                priceText = UiText.Resource(Res.string.price_free),
                stateLabel = UiText.Resource(Res.string.slot_state_running),
                state = SlotLiveStateUiModel.Running(progress = 0.53f),
                slots =
                    listOf(
                        segment("12:00 – 19:00", SlotLiveStateUiModel.Running(progress = 0.53f), 0.118f, 0.529f),
                    ),
            ),
        )

    private fun row(
        id: String,
        name: String,
        categoryId: String,
        categoryName: String,
        priceText: UiText?,
        stateLabel: UiText?,
        state: SlotLiveStateUiModel,
        slots: List<SlotSegmentUiModel>,
    ) = SlotRowUiModel(
        id = id,
        happeningId = id.substringAfterLast('/').substringAfter(':').substringBeforeLast('-'),
        name = name,
        categoryId = categoryId,
        categoryName = categoryName,
        priceText = priceText,
        stateLabel = stateLabel,
        state = state,
        slots = slots,
    )

    private fun segment(
        timeText: String,
        state: SlotLiveStateUiModel,
        barStart: Float,
        barEnd: Float,
    ) = SlotSegmentUiModel(
        id = timeText,
        timeText = timeText,
        state = state,
        barStart = barStart,
        barEnd = barEnd,
    )
}

@PreviewThemes
@Composable
private fun ProgrammeScreenPreview(
    @PreviewParameter(ProgrammeScreenStateProvider::class) state: ProgrammeUiModel,
) {
    YadloPreview {
        ProgrammeScreen(
            state = state,
            onScopeClick = {},
            onCategoryClick = {},
            onAllCategoriesClick = {},
            onSlotClick = {},
        )
    }
}
