package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Payment
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PaymentReducerTest {
    private val reducer = PaymentStoreFactory.ReducerImpl

    @Test
    fun paymentUpdated_beforeAnyEmission_hasNotLoaded() {
        assertFalse(PaymentState().hasLoaded)
        assertNull(PaymentState().payment)
    }

    @Test
    fun paymentUpdated_firstEmission_holdsTheMethods() {
        val result = with(reducer) { PaymentState().reduce(PaymentMessage.PaymentUpdated(payment())) }

        assertTrue(result.hasLoaded)
        assertEquals(listOf("especes"), result.payment?.methods?.map { it.id })
    }

    @Test
    fun paymentUpdated_aNullSection_isLoadedRatherThanStillWaiting() {
        val result = with(reducer) { PaymentState().reduce(PaymentMessage.PaymentUpdated(null)) }

        // The difference between a spinner and a sentence. Both look like "no payment" in the
        // state and only one of them is honest.
        assertTrue(result.hasLoaded)
        assertNull(result.payment)
    }

    private fun payment() =
        Payment(
            methods = listOf(Payment.Method(id = "especes", name = "Espèces", accepted = false)),
            notes = emptyList(),
            links = emptyList(),
            provenance = Provenance.CONFIRMED,
        )
}
