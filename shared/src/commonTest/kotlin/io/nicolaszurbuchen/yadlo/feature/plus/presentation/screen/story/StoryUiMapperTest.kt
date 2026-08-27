package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story

import io.nicolaszurbuchen.yadlo.core.content.domain.model.Figure
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StoryPage
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.home_figures_caveat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StoryUiMapperTest {
    @Test
    fun toUiModel_beforeTheBundleLands_isLoading() {
        assertTrue(StoryState().toUiModel().isLoading)
    }

    @Test
    fun toUiModel_thePageArrives_stopsLoading() {
        // The pair that matters now that one nullable field carries the whole state: the same field
        // that means "waiting" has to mean "done" the moment it is filled.
        assertFalse(loaded().isLoading)
    }

    @Test
    fun toUiModel_carriesTheOriginAndItsPassage() {
        val model = loaded()

        assertEquals("Yadlo est né en 2015.", model.body)
        assertEquals("Une journée à Yadlo", model.passageTitle)
        assertEquals("Tôt le matin.", model.passageBody)
    }

    @Test
    fun toUiModel_figures_areCarriedAsPublishedStrings() {
        // "3200" is a string because some figures are ranges or carry a qualifier, and it is only
        // ever printed beside its label.
        assertEquals(listOf("6000", "160"), loaded().figures.map { it.value })
    }

    @Test
    fun toUiModel_pastEditionFigures_carryTheSameCaveatAccueilPrints() {
        val model = loaded(confirmed = false)

        // The same string in both places, because it is the same claim being made twice.
        assertEquals(UiText.Resource(Res.string.home_figures_caveat), model.figuresCaveat)
    }

    @Test
    fun toUiModel_confirmedFigures_carryNoCaveat() {
        assertNull(loaded().figuresCaveat)
    }

    @Test
    fun toUiModel_noFigures_carriesNoCaveatEither() {
        val page =
            StoryPage(
                foundedYear = 2015,
                body = "…",
                passageTitle = null,
                passageBody = null,
                figures = emptyList(),
                figuresAreConfirmed = true,
            )

        // A caveat with nothing under it would be an apology for an absent block.
        assertNull(StoryState(page = page).toUiModel().figuresCaveat)
    }

    private fun loaded(confirmed: Boolean = true) =
        StoryState(
            page =
                StoryPage(
                    foundedYear = 2015,
                    body = "Yadlo est né en 2015.",
                    passageTitle = "Une journée à Yadlo",
                    passageBody = "Tôt le matin.",
                    figures =
                        listOf(
                            figure("visiteurs", "6000", confirmed),
                            figure("benevoles", "160", confirmed),
                        ),
                    figuresAreConfirmed = confirmed,
                ),
        ).toUiModel()

    private fun figure(
        id: String,
        value: String,
        confirmed: Boolean,
    ) = Figure(
        id = id,
        value = value,
        label = id,
        provenance = if (confirmed) Provenance.CONFIRMED else Provenance.ARCHIVED,
    )
}
