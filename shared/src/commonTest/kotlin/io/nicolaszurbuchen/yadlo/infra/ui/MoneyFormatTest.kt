package io.nicolaszurbuchen.yadlo.infra.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyFormatTest {
    @Test
    fun formatMoney_wholeAmount_dropsTheDecimals() {
        assertEquals("CHF 25", formatMoney(amount = 25.0, currency = "CHF"))
    }

    @Test
    fun formatMoney_halfFranc_writesBothRappen() {
        // 4.5 in the content means CHF 4.50, and "CHF 4.5" is not a price anyone has ever written.
        assertEquals("CHF 4.50", formatMoney(amount = 4.5, currency = "CHF"))
    }

    @Test
    fun formatMoney_amountThatCannotBeHeldExactly_stillRoundsToItsAuthoredRappen() {
        // 8.1 is not representable as a Double, so splitting it on its fractional part directly
        // yields 09 rappen. Going through a rounded whole number of rappen is what avoids that.
        assertEquals("CHF 8.10", formatMoney(amount = 8.1, currency = "CHF"))
    }

    @Test
    fun formatMoney_free_readsAsZeroRatherThanEmpty() {
        // The caller decides whether "gratuit" is the better word; the formatter never invents it.
        assertEquals("CHF 0", formatMoney(amount = 0.0, currency = "CHF"))
    }

    @Test
    fun formatMoney_currencyIsTakenAsAuthored_notAssumedToBeFrancs() {
        assertEquals("EUR 12", formatMoney(amount = 12.0, currency = "EUR"))
    }
}
