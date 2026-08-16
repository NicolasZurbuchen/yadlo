package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Contact
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.ContactRouter
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.contact_empty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ContactUiMapperTest {
    @Test
    fun toUiModel_beforeTheBundleLands_isLoading() {
        assertTrue(ContactState().toUiModel().isLoading)
    }

    @Test
    fun toUiModel_loadedWithNoContact_saysSo() {
        assertEquals(
            UiText.Resource(Res.string.contact_empty),
            ContactState(router = null, hasLoaded = true).toUiModel().emptyMessage,
        )
    }

    @Test
    fun toUiModel_carriesEveryAddressWithItsOwnLabel() {
        val model = loaded()

        assertEquals(listOf("Informations générales", "Programmation musicale"), model.emails.map { it.label })
        assertEquals("musique@yadlo.ch", model.emails.last().address)
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

        assertNull(ContactState(router = router, hasLoaded = true).toUiModel().address)
    }

    private fun loaded() =
        ContactState(
            hasLoaded = true,
            router =
                ContactRouter(
                    emails =
                        listOf(
                            Contact.Email(id = "hello", address = "hello@yadlo.ch", label = "Informations générales"),
                            Contact.Email(
                                id = "musique",
                                address = "musique@yadlo.ch",
                                label = "Programmation musicale",
                            ),
                        ),
                    addressLines = listOf("Avenue de la Plage 1", "1028 Préverenges"),
                ),
        ).toUiModel()
}
