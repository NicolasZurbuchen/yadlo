package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment

import io.nicolaszurbuchen.yadlo.core.content.domain.model.InfoLink
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Payment
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PaymentUiMapperTest {
    @Test
    fun toUiModel_beforeTheBundleLands_isLoading() {
        assertTrue(PaymentState().toUiModel().isLoading)
    }

    @Test
    fun toUiModel_theBlockArrives_stopsLoading() {
        assertFalse(loaded().isLoading)
    }

    @Test
    fun toUiModel_theAnswerComesFirstAndIsPublishedRatherThanAssembled() {
        val model = loaded()

        // Three words, from the content. Deriving them from the method list would mean generating
        // French out of a list of names, which is how a screen ends up saying something nobody wrote.
        assertEquals("Carte et TWINT uniquement", model.headline)
        assertEquals("Aucun stand n'accepte les espèces.", model.summary)
    }

    @Test
    fun toUiModel_theRefusal_staysInTheListAndSinksToTheBottomOfIt() {
        // One column with one ✕ at the end of it: being sure about the refusal used to mean reading
        // two sections and working out which was which.
        assertEquals(
            listOf("carte" to true, "twint" to true, "especes" to false),
            loaded().methods.map { it.id to it.accepted },
        )
    }

    @Test
    fun toUiModel_methodNames_areTheContentsOwnPhrasing() {
        // Shortening "Visa, Mastercard et Maestro — sans contact" would be deciding which cards
        // matter and dropping the one word that answers "can I pay with my watch".
        assertEquals("Visa, Mastercard et Maestro — sans contact", loaded().methods.first().name)
    }

    @Test
    fun toUiModel_aNote_keepsItsHeadingItsBodyAndItsOwnLinks() {
        val note = loaded().notes.single()

        assertEquals("Vous n'avez pas TWINT ?", note.title)
        assertEquals("L'application dépend de votre banque.", note.body)
        assertEquals("twint.ch", note.links.single().label)
        assertEquals("Site officiel", note.links.single().sublabel)
        assertEquals("https://www.twint.ch/", note.links.single().url)
    }

    @Test
    fun toUiModel_aNoteWithNoHeading_keepsItsParagraphRatherThanBeingDropped() {
        // What content older than the build that reads it looks like. The paragraph is still the
        // answer to somebody's question; only the heading over it is missing.
        val state =
            PaymentState(
                payment =
                    Payment(
                        headline = null,
                        summary = null,
                        methods = emptyList(),
                        notes =
                            listOf(
                                Payment.Note(
                                    id = "sans-especes",
                                    title = null,
                                    body = "Aucun stand n'accepte les espèces.",
                                    links = emptyList(),
                                ),
                            ),
                        provenance = Provenance.CONFIRMED,
                    ),
            )

        val note = state.toUiModel().notes.single()

        assertNull(note.title)
        assertEquals("Aucun stand n'accepte les espèces.", note.body)
    }

    private fun loaded() =
        PaymentState(
            payment =
                Payment(
                    headline = "Carte et TWINT uniquement",
                    summary = "Aucun stand n'accepte les espèces.",
                    // Deliberately published out of order, so the sort is exercised rather than
                    // trusted to the file happening to be written the right way round.
                    methods =
                        listOf(
                            Payment.Method(
                                id = "carte",
                                name = "Visa, Mastercard et Maestro — sans contact",
                                accepted = true,
                            ),
                            Payment.Method(id = "especes", name = "Espèces", accepted = false),
                            Payment.Method(id = "twint", name = "TWINT", accepted = true),
                        ),
                    notes =
                        listOf(
                            Payment.Note(
                                id = "pas-de-twint",
                                title = "Vous n'avez pas TWINT ?",
                                body = "L'application dépend de votre banque.",
                                links =
                                    listOf(
                                        InfoLink(
                                            id = "twint",
                                            label = "twint.ch",
                                            sublabel = "Site officiel",
                                            url = "https://www.twint.ch/",
                                        ),
                                    ),
                            ),
                        ),
                    provenance = Provenance.CONFIRMED,
                ),
        ).toUiModel()
}
