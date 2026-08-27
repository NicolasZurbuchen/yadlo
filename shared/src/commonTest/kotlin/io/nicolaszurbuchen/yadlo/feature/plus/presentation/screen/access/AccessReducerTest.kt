package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access

import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Transport
import io.nicolaszurbuchen.yadlo.core.content.domain.model.TransportMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccessReducerTest {
    private val reducer = AccessStoreFactory.ReducerImpl

    @Test
    fun transportUpdated_beforeAnyEmission_hasNotLoaded() {
        assertFalse(AccessState().hasLoaded)
        assertNull(AccessState().transport)
    }

    @Test
    fun transportUpdated_firstEmission_holdsTheModes() {
        val result = with(reducer) { AccessState().reduce(AccessMessage.TransportUpdated(transport())) }

        assertTrue(result.hasLoaded)
        assertEquals(listOf("bus"), result.transport?.modes?.map { it.id })
    }

    @Test
    fun transportUpdated_aNullSection_isLoadedRatherThanStillWaiting() {
        val result = with(reducer) { AccessState().reduce(AccessMessage.TransportUpdated(null)) }

        assertTrue(result.hasLoaded)
        assertNull(result.transport)
    }

    private fun transport() =
        Transport(
            modes =
                listOf(
                    TransportMode(
                        id = "bus",
                        name = "En bus",
                        body = null,
                        facts = emptyList(),
                        links = emptyList(),
                        departures = emptyList(),
                    ),
                ),
            provenance = Provenance.CONFIRMED,
        )
}
