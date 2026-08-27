package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import io.nicolaszurbuchen.yadlo.core.content.domain.model.Contact
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.ContactRouter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ContactUiMapperTest {
    @Test
    fun toUiModel_beforeTheBundleLands_isLoading() {
        assertTrue(ContactState().toUiModel().isLoading)
    }

    @Test
    fun toUiModel_theDirectoryArrives_stopsLoading() {
        assertFalse(loaded().isLoading)
    }

    @Test
    fun toUiModel_carriesEveryAddressWithItsOwnLabel() {
        val model = loaded()

        assertEquals(listOf("Informations générales", "Programmation musicale"), model.emails.map { it.label })
        assertEquals("musique@yadlo.ch", model.emails.last().address)
    }

    @Test
    fun toUiModel_whoIsBehindAnAddress_travelsBesideIt() {
        // Kept as its own field rather than folded into the label: the screen decides how to write
        // the two together, and the general address has no name to write at all.
        assertEquals("Jeremy B.", loaded().emails.last().responsible)
        assertNull(loaded().emails.first().responsible)
    }

    @Test
    fun toUiModel_theAddress_readsAsOneBlockRatherThanThreeRows() {
        // It is a postal address and is read as one. Splitting it into rows would make it look like
        // three separate facts.
        assertEquals("Avenue de la Plage 1\n1028 Préverenges", loaded().address)
    }

    @Test
    fun toUiModel_noPostalAddressPublished_writesNothingRatherThanAnEmptyBlock() {
        val router = ContactRouter(emails = emptyList(), addressLines = emptyList())

        assertNull(ContactState(router = router).toUiModel().address)
    }

    private fun loaded() =
        ContactState(
            router =
                ContactRouter(
                    emails =
                        listOf(
                            Contact.Email(
                                id = "hello",
                                address = "hello@yadlo.ch",
                                label = "Informations générales",
                                responsible = null,
                            ),
                            Contact.Email(
                                id = "musique",
                                address = "musique@yadlo.ch",
                                label = "Programmation musicale",
                                responsible = "Jeremy B.",
                            ),
                        ),
                    addressLines = listOf("Avenue de la Plage 1", "1028 Préverenges"),
                ),
        ).toUiModel()
}
