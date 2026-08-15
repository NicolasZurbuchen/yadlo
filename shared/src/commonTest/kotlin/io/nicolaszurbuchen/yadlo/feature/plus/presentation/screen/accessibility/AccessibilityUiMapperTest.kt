package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.accessibility

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Accessibility
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.AccessibilityGuide
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.accessibility_empty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccessibilityUiMapperTest {
    @Test
    fun toUiModel_beforeTheBundleLands_isLoading() {
        assertTrue(AccessibilityState().toUiModel().isLoading)
    }

    @Test
    fun toUiModel_noSectionAtAll_isAContentBugAndSaysSo() {
        val model = AccessibilityState(guide = null, hasLoaded = true).toUiModel()

        assertEquals(UiText.Resource(Res.string.accessibility_empty), model.emptyMessage)
        assertFalse(model.nothingPublished)
    }

    @Test
    fun toUiModel_aPublishedSectionWithNothingInIt_isTheStateTheScreenWasBuiltFor() {
        val model = model(available = emptyList(), unavailable = emptyList())

        // Distinct from a missing section: this one says "we have not answered this yet", and the
        // screen turns into the address it hands over.
        assertTrue(model.nothingPublished)
        assertNull(model.emptyMessage)
        assertEquals("hello@yadlo.ch", model.contactEmail)
    }

    @Test
    fun toUiModel_somethingPublished_isNoLongerTheEmptyState() {
        val model = model(available = listOf(item("parking")), unavailable = emptyList())

        assertFalse(model.nothingPublished)
    }

    @Test
    fun toUiModel_onlyNegativeFactsPublished_isStillNotTheEmptyState() {
        val model = model(available = emptyList(), unavailable = listOf(item("toilettes")))

        // A list of what is *not* available is a real answer and one of the most useful ones. It
        // must not read as "nothing published".
        assertFalse(model.nothingPublished)
        assertEquals(listOf("toilettes"), model.unavailable.map { it.id })
    }

    @Test
    fun toUiModel_bothLists_keepTheirNotes() {
        val model =
            model(
                available = listOf(item("parking", note = "Deux, à l'entrée")),
                unavailable = listOf(item("toilettes", note = "Le site est une plage")),
            )

        assertEquals("Deux, à l'entrée", model.available.single().note)
        assertEquals("Le site est une plage", model.unavailable.single().note)
    }

    @Test
    fun toUiModel_noContactResolved_leavesTheListsStanding() {
        val model =
            AccessibilityState(
                hasLoaded = true,
                guide =
                    AccessibilityGuide(
                        available = listOf(item("parking")),
                        unavailable = emptyList(),
                        contactEmail = null,
                    ),
            ).toUiModel()

        assertNull(model.contactEmail)
        assertEquals(1, model.available.size)
    }

    private fun model(
        available: List<Accessibility.Item>,
        unavailable: List<Accessibility.Item>,
    ) = AccessibilityState(
        hasLoaded = true,
        guide =
            AccessibilityGuide(
                available = available,
                unavailable = unavailable,
                contactEmail = "hello@yadlo.ch",
            ),
    ).toUiModel()

    private fun item(
        id: String,
        note: String? = null,
    ) = Accessibility.Item(id = id, name = id, available = true, note = note)
}
