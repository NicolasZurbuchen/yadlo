package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Payment
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PaymentReducerTest {
    private val reducer = PaymentStoreFactory.ReducerImpl

    @Test
    fun paymentUpdated_beforeAnyEmission_carriesNoBlock() {
        assertNull(PaymentState().payment)
    }

    @Test
    fun paymentUpdated_firstEmission_holdsTheMethods() {
        val result = with(reducer) { PaymentState().reduce(PaymentMessage.PaymentUpdated(payment())) }

        assertEquals(listOf("especes"), result.payment?.methods?.map { it.id })
    }

    private fun payment() =
        Payment(
            headline = null,
            summary = null,
            methods = listOf(Payment.Method(id = "especes", name = "Espèces", accepted = false)),
            notes = emptyList(),
            provenance = Provenance.CONFIRMED,
        )
}
