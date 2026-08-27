package io.nicolaszurbuchen.yadlo.core.content.presentation.mapper

import io.nicolaszurbuchen.yadlo.core.content.domain.model.DietaryCoverage
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloDietaryMarkUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DietaryTagUiMapperTest {
    @Test
    fun toDietaryTags_labelsAMarkByHowMuchOfTheCarteItCovers() {
        val tags =
            mapOf(
                YadloDietaryMarkUiModel.VEGAN.id to DietaryCoverage.ALL,
                YadloDietaryMarkUiModel.GLUTEN_FREE.id to DietaryCoverage.SOME,
            ).toDietaryTags()

        assertEquals(
            mapOf(
                YadloDietaryMarkUiModel.VEGAN to YadloDietaryMarkUiModel.VEGAN.allLabel,
                YadloDietaryMarkUiModel.GLUTEN_FREE to YadloDietaryMarkUiModel.GLUTEN_FREE.someLabel,
            ),
            tags.associate { it.mark to it.label },
        )
    }

    @Test
    fun toDietaryTags_dropsVegetarianOnlyWhenVeganCoversExactlyAsMuch() {
        val bothAll =
            mapOf(
                YadloDietaryMarkUiModel.VEGAN.id to DietaryCoverage.ALL,
                YadloDietaryMarkUiModel.VEGETARIAN.id to DietaryCoverage.ALL,
            ).toDietaryTags()

        assertEquals(listOf(YadloDietaryMarkUiModel.VEGAN), bothAll.map { it.mark })
    }

    @Test
    fun toDietaryTags_keepsVegetarianWhenItCoversMoreThanVegan() {
        // The case the dish-level twin gets wrong on purpose: one vegan dish on an otherwise
        // meat-free carte is *options véganes* and *100 % végétarien*, and a reader choosing
        // dinner needs both answers.
        val mixed =
            mapOf(
                YadloDietaryMarkUiModel.VEGAN.id to DietaryCoverage.SOME,
                YadloDietaryMarkUiModel.VEGETARIAN.id to DietaryCoverage.ALL,
            ).toDietaryTags()

        assertEquals(
            listOf(YadloDietaryMarkUiModel.VEGAN, YadloDietaryMarkUiModel.VEGETARIAN),
            mixed.map { it.mark },
        )
    }

    @Test
    fun toDietaryTags_ordersByTheEnumRatherThanByTheContent() {
        // A Stand row and the fiche it opens must list the same two marks in the same order, so
        // the order comes from the enum and never from whatever order the content published.
        val declared =
            listOf(
                YadloDietaryMarkUiModel.GLUTEN_FREE,
                YadloDietaryMarkUiModel.DAIRY_FREE,
                YadloDietaryMarkUiModel.VEGAN,
            )

        val tags = declared.reversed().associate { it.id to DietaryCoverage.ALL }.toDietaryTags()

        assertEquals(
            YadloDietaryMarkUiModel.entries.filter { it in declared },
            tags.map { it.mark },
        )
    }

    @Test
    fun toDietaryTags_dropsAnIdThisBuildHasNoMarkFor() {
        // Content can publish a mark before the app ships a glyph for it — "casher" is not one of
        // the six this build knows. That is a missing tag,
        // never a crash and never an empty chip.
        val tags =
            mapOf(
                YadloDietaryMarkUiModel.VEGAN.id to DietaryCoverage.ALL,
                "casher" to DietaryCoverage.SOME,
            ).toDietaryTags()

        assertEquals(listOf(YadloDietaryMarkUiModel.VEGAN), tags.map { it.mark })
    }

    @Test
    fun toDietaryTags_isEmptyWhenNothingIsPublished() {
        // StandCard draws no dietary band at all in this case, rather than a rule with nothing
        // under it.
        assertTrue(emptyMap<String, DietaryCoverage>().toDietaryTags().isEmpty())
    }
}
