package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveVolunteeringOfferUseCase
import io.nicolaszurbuchen.yadlo.infra.ui.mailtoUrl
import kotlinx.coroutines.launch

interface VolunteeringStore : Store<VolunteeringIntent, VolunteeringState, VolunteeringLabel>

class VolunteeringStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeVolunteeringOffer: ObserveVolunteeringOfferUseCase,
) {
    fun create(): VolunteeringStore =
        object :
            VolunteeringStore,
            Store<VolunteeringIntent, VolunteeringState, VolunteeringLabel> by storeFactory.create(
                name = "VolunteeringStore",
                initialState = VolunteeringState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<VolunteeringAction>() {
        override fun invoke() {
            dispatch(VolunteeringAction.ObserveOffer)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<VolunteeringIntent, VolunteeringAction, VolunteeringState, VolunteeringMessage, VolunteeringLabel>() {
        override fun executeAction(action: VolunteeringAction) {
            when (action) {
                VolunteeringAction.ObserveOffer -> observeOffer()
            }
        }

        override fun executeIntent(intent: VolunteeringIntent) {
            // Both leave the app, and that is the whole design: the association's own recruitment
            // site keeps receiving its applications instead of a form here collecting them.
            when (intent) {
                is VolunteeringIntent.SignupClicked -> publish(VolunteeringLabel.OpenUrl(intent.url))
                is VolunteeringIntent.EmailClicked -> publish(VolunteeringLabel.OpenUrl(mailtoUrl(intent.address)))
            }
        }

        private fun observeOffer() {
            scope.launch {
                observeVolunteeringOffer().collect { offer ->
                    dispatch(VolunteeringMessage.OfferUpdated(offer))
                }
            }
        }
    }

    // internal (not private) so VolunteeringReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<VolunteeringState, VolunteeringMessage> {
        override fun VolunteeringState.reduce(msg: VolunteeringMessage): VolunteeringState =
            when (msg) {
                is VolunteeringMessage.OfferUpdated -> {
                    copy(offer = msg.offer)
                }
            }
    }
}
