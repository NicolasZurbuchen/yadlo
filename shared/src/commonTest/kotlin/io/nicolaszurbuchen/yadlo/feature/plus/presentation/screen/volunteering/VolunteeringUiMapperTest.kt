package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.VolunteeringOffer
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.volunteering_empty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VolunteeringUiMapperTest {
    @Test
    fun toUiModel_beforeTheBundleLands_isLoading() {
        val model = VolunteeringState().toUiModel()

        assertTrue(model.isLoading)
        assertNull(model.emptyMessage)
    }

    @Test
    fun toUiModel_recruitingClosed_saysSoRatherThanShowingAnEmptyPage() {
        val model = VolunteeringState(offer = null, hasLoaded = true).toUiModel()

        assertEquals(UiText.Resource(Res.string.volunteering_empty), model.emptyMessage)
        assertTrue(!model.isLoading)
        assertTrue(model.perks.isEmpty())
    }

    @Test
    fun toUiModel_carriesTheProgrammesOwnName() {
        // The heading is the content's word, not the app's: the row that opened this said "Devenir
        // bénévole" so anyone would recognise it, and the page says "Hot'Staff" because that is
        // what the association calls it.
        assertEquals("Hot'Staff", loaded().name)
    }

    @Test
    fun toUiModel_carriesTheAskTheOfferAndBothWaysToAct() {
        val model = loaded()

        assertEquals("Six heures minimum.", model.body)
        assertEquals(listOf("Tote bag", "Repas végane"), model.perks)
        assertEquals("https://ehro.app/o/yadlo/", model.signupUrl)
        assertEquals("staff@yadlo.ch", model.email)
        assertNull(model.emptyMessage)
    }

    @Test
    fun toUiModel_noSignupUrl_stillLeavesTheAddress() {
        // Applications sometimes close before the campaign text comes down. The mail tile is then
        // the only way left to ask, which is exactly when it must not disappear too.
        val model = loaded(signupUrl = null)

        assertNull(model.signupUrl)
        assertEquals("staff@yadlo.ch", model.email)
    }

    private fun loaded(signupUrl: String? = "https://ehro.app/o/yadlo/") =
        VolunteeringState(
            hasLoaded = true,
            offer =
                VolunteeringOffer(
                    name = "Hot'Staff",
                    body = "Six heures minimum.",
                    perks = listOf("Tote bag", "Repas végane"),
                    signupUrl = signupUrl,
                    email = "staff@yadlo.ch",
                ),
        ).toUiModel()
}
