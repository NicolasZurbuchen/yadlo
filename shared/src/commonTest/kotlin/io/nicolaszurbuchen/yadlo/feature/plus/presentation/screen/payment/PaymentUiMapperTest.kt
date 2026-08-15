package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment

import io.nicolaszurbuchen.yadlo.common.content.domain.model.InfoLink
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Payment
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.payment_empty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PaymentUiMapperTest {
    @Test
    fun toUiModel_beforeTheBundleLands_isLoading() {
        val model = PaymentState().toUiModel()

        assertTrue(model.isLoading)
        assertNull(model.emptyMessage)
    }

    @Test
    fun toUiModel_loadedWithNoSection_saysSoRatherThanSpinningForever() {
        val model = PaymentState(payment = null, hasLoaded = true).toUiModel()

        // Reachable through a restored back stack over a publish that dropped the block. Rare, and
        // a page that spins is a worse way to say it than a sentence.
        assertTrue(!model.isLoading)
        assertEquals(UiText.Resource(Res.string.payment_empty), model.emptyMessage)
    }

    @Test
    fun toUiModel_splitsTheAcceptedFromTheRefused() {
        val model = loaded()

        assertEquals(listOf("Cartes Visa, Mastercard et Maestro", "TWINT"), model.accepted)
        assertEquals(listOf("Espèces"), model.refused)
    }

    @Test
    fun toUiModel_theRefusal_isItsOwnListAndNotTheLeftovers() {
        // A list of what works answers "have I got one of these". The refusal answers "do I need to
        // stop at a cash machine", which is the only question that has to be settled before
        // leaving home — so it cannot be the last row of the first list.
        assertEquals(1, loaded().refused.size)
    }

    @Test
    fun toUiModel_methodNames_areTheContentsOwnPhrasing() {
        // Shortening "Cartes Visa, Mastercard et Maestro" would be deciding which cards matter.
        assertEquals("Cartes Visa, Mastercard et Maestro", loaded().accepted.first())
    }

    @Test
    fun toUiModel_notes_carryTheirBodyAndNotTheirIds() {
        assertEquals(listOf("Aucun stand n'accepte les espèces."), loaded().notes)
    }

    @Test
    fun toUiModel_links_keepBothLinesForTheTile() {
        val link = loaded().links.single()

        assertEquals("twint.ch", link.label)
        assertEquals("Site officiel", link.sublabel)
        assertEquals("https://www.twint.ch/", link.url)
    }

    @Test
    fun toUiModel_aPublishedSection_saysNothingAboutBeingEmpty() {
        assertNull(loaded().emptyMessage)
    }

    private fun loaded() =
        PaymentState(
            hasLoaded = true,
            payment =
                Payment(
                    methods =
                        listOf(
                            Payment.Method(
                                id = "carte",
                                name = "Cartes Visa, Mastercard et Maestro",
                                accepted = true,
                            ),
                            Payment.Method(id = "twint", name = "TWINT", accepted = true),
                            Payment.Method(id = "especes", name = "Espèces", accepted = false),
                        ),
                    notes = listOf(Payment.Note(id = "sans-especes", body = "Aucun stand n'accepte les espèces.")),
                    links =
                        listOf(
                            InfoLink(
                                id = "twint",
                                label = "twint.ch",
                                sublabel = "Site officiel",
                                url = "https://www.twint.ch/",
                            ),
                        ),
                    provenance = Provenance.CONFIRMED,
                ),
        ).toUiModel()
}
