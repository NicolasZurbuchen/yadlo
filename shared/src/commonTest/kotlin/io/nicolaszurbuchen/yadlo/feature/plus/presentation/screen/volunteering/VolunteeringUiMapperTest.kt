package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Contact
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.VolunteeringOffer
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.uimodel.PlusEmailUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VolunteeringUiMapperTest {
    @Test
    fun toUiModel_shareText_carriesTheAssociationsOwnSignupAddressRatherThanTheApp() {
        // Somebody forwarding this is doing the association a favour, and what they send has to
        // work for a recipient who has never heard of this app — which is every recipient.
        assertEquals(
            "Hot'Staff\nSix heures minimum.\nhttps://ehro.app/o/yadlo/",
            loaded().shareText,
        )
    }

    @Test
    fun toUiModel_noSignupUrlPublished_hasNothingToShare() {
        // The action hides itself rather than offering to send a message with nowhere to go.
        assertNull(loaded(signupUrl = null).shareText)
    }

    @Test
    fun toUiModel_beforeTheBundleLands_isLoading() {
        assertTrue(VolunteeringState().toUiModel().isLoading)
    }

    @Test
    fun toUiModel_theOfferArrives_stopsLoading() {
        assertFalse(loaded().isLoading)
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
        assertEquals(PlusEmailUiModel(id = "staff", label = "Staff", responsible = "Maeva C.", address = "staff@yadlo.ch"), model.email)
    }

    @Test
    fun toUiModel_noSignupUrl_stillLeavesTheAddress() {
        // Applications sometimes close before the campaign text comes down. The mail tile is then
        // the only way left to ask, which is exactly when it must not disappear too.
        val model = loaded(signupUrl = null)

        assertNull(model.signupUrl)
        assertEquals("staff@yadlo.ch", model.email?.address)
    }

    private fun loaded(signupUrl: String? = "https://ehro.app/o/yadlo/") =
        VolunteeringState(
            offer =
                VolunteeringOffer(
                    name = "Hot'Staff",
                    body = "Six heures minimum.",
                    perks = listOf("Tote bag", "Repas végane"),
                    signupUrl = signupUrl,
                    email = staffEmail(),
                ),
        ).toUiModel()

    // The whole directory entry, not the address: the tile names the concern and whoever is behind
    // it, the same way *Nous écrire* does.
    private fun staffEmail() = Contact.Email(id = "staff", address = "staff@yadlo.ch", label = "Staff", responsible = "Maeva C.")
}
