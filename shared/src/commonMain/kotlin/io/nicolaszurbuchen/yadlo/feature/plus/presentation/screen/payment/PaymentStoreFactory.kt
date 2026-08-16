package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObservePaymentUseCase
import kotlinx.coroutines.launch

interface PaymentStore : Store<PaymentIntent, PaymentState, PaymentLabel>

class PaymentStoreFactory(
    private val storeFactory: StoreFactory,
    private val observePayment: ObservePaymentUseCase,
) {
    fun create(): PaymentStore =
        object :
            PaymentStore,
            Store<PaymentIntent, PaymentState, PaymentLabel> by storeFactory.create(
                name = "PaymentStore",
                initialState = PaymentState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<PaymentAction>() {
        override fun invoke() {
            dispatch(PaymentAction.ObservePayment)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<PaymentIntent, PaymentAction, PaymentState, PaymentMessage, PaymentLabel>() {
        override fun executeAction(action: PaymentAction) {
            when (action) {
                PaymentAction.ObservePayment -> observePaymentBlock()
            }
        }

        override fun executeIntent(intent: PaymentIntent) {
            when (intent) {
                // The TWINT page is the platform's business rather than the navigator's, the same
                // reasoning as an artist's own site on the fiche.
                is PaymentIntent.LinkClicked -> publish(PaymentLabel.OpenUrl(intent.url))
            }
        }

        private fun observePaymentBlock() {
            scope.launch {
                observePayment().collect { payment ->
                    dispatch(PaymentMessage.PaymentUpdated(payment))
                }
            }
        }
    }

    // internal (not private) so PaymentReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<PaymentState, PaymentMessage> {
        override fun PaymentState.reduce(msg: PaymentMessage): PaymentState =
            when (msg) {
                is PaymentMessage.PaymentUpdated -> {
                    copy(payment = msg.payment)
                }
            }
    }
}
