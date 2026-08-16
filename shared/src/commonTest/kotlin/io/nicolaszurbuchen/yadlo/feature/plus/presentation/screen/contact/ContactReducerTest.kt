package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.ContactRouter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ContactReducerTest {
    private val reducer = ContactStoreFactory.ReducerImpl

    @Test
    fun routerUpdated_beforeAnyEmission_carriesNoRouter() {
        assertNull(ContactState().router)
    }

    @Test
    fun routerUpdated_firstEmission_holdsTheDirectory() {
        val router =
            ContactRouter(
                emails = emptyList(),
                addressLines = listOf("Avenue de la Plage 1"),
            )

        val result = with(reducer) { ContactState().reduce(ContactMessage.RouterUpdated(router)) }

        assertEquals(listOf("Avenue de la Plage 1"), result.router?.addressLines)
    }
}
