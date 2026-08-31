package io.nicolaszurbuchen.yadlo.design.uimodel

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The dish-level half of the pairing. Its Stand-level twin lives in
 * `core/content/presentation/mapper/` because that one names `DietaryCoverage`, and the two
 * deliberately disagree about *Végétarien* — see `DietaryTagUiMapperTest`.
 */
class YadloDietaryTagUiModelTest {
    @Test
    fun toDietaryTags_labelsEachMarkWithItsDishWording() {
        // Not `allLabel` or `someLabel`: a dish is one thing and cannot be partly vegan, so it
        // reads *Végan* rather than *100 % végan*.
        val tags = listOf(YadloDietaryMarkUiModel.GLUTEN_FREE.id).toDietaryTags()

        assertEquals(
            listOf(YadloDietaryTagUiModel(YadloDietaryMarkUiModel.GLUTEN_FREE, YadloDietaryMarkUiModel.GLUTEN_FREE.label)),
            tags,
        )
    }

    @Test
    fun toDietaryTags_dropsVegetarianWheneverVeganIsThere() {
        // Unconditionally, unlike the Stand-level twin: every vegan dish is also vegetarian, and
        // spending two of a row's three tags saying nearly one thing is what this drops.
        val tags =
            listOf(
                YadloDietaryMarkUiModel.VEGAN.id,
                YadloDietaryMarkUiModel.VEGETARIAN.id,
            ).toDietaryTags()

        assertEquals(listOf(YadloDietaryMarkUiModel.VEGAN), tags.map { it.mark })
    }

    @Test
    fun toDietaryTags_keepsVegetarianOnItsOwn() {
        val tags = listOf(YadloDietaryMarkUiModel.VEGETARIAN.id).toDietaryTags()

        assertEquals(listOf(YadloDietaryMarkUiModel.VEGETARIAN), tags.map { it.mark })
    }

    @Test
    fun toDietaryTags_ordersByTheEnumRatherThanByTheContent() {
        // A dish row and the Stand card above it must list the same marks in the same order, so
        // the order comes from the enum and never from whatever order the content published.
        val declared =
            listOf(
                YadloDietaryMarkUiModel.SPICY,
                YadloDietaryMarkUiModel.GLUTEN_FREE,
                YadloDietaryMarkUiModel.VEGAN,
            )

        val tags = declared.reversed().map { it.id }.toDietaryTags()

        assertEquals(YadloDietaryMarkUiModel.entries.filter { it in declared }, tags.map { it.mark })
    }

    @Test
    fun toDietaryTags_dropsAnIdThisBuildHasNoMarkFor() {
        val tags = listOf(YadloDietaryMarkUiModel.VEGAN.id, "casher").toDietaryTags()

        assertEquals(listOf(YadloDietaryMarkUiModel.VEGAN), tags.map { it.mark })
    }

    @Test
    fun toDietaryTags_collapsesARepeatedId() {
        // The marks are resolved into a set before being ordered, so content that lists one twice
        // does not draw it twice.
        val tags = listOf(YadloDietaryMarkUiModel.VEGAN.id, YadloDietaryMarkUiModel.VEGAN.id).toDietaryTags()

        assertEquals(listOf(YadloDietaryMarkUiModel.VEGAN), tags.map { it.mark })
    }

    @Test
    fun toDietaryTags_isEmptyWhenNothingIsPublished() {
        assertTrue(emptyList<String>().toDietaryTags().isEmpty())
    }

    @Test
    fun forId_resolvesEveryMarkFromItsOwnId() {
        // The round trip the two `toDietaryTags` overloads both go through. An id that stopped
        // resolving would silently drop that mark from every dish and every Stand at once.
        assertEquals(
            YadloDietaryMarkUiModel.entries.toList(),
            YadloDietaryMarkUiModel.entries.mapNotNull { YadloDietaryMarkUiModel.forId(it.id) },
        )
    }
}
