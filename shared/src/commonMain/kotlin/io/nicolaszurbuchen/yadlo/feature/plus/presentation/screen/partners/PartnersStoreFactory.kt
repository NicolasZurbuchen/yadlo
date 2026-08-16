package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObservePartnerTiersUseCase
import kotlinx.coroutines.launch

interface PartnersStore : Store<PartnersIntent, PartnersState, PartnersLabel>

class PartnersStoreFactory(
    private val storeFactory: StoreFactory,
    private val observePartnerTiers: ObservePartnerTiersUseCase,
) {
    fun create(): PartnersStore =
        object :
            PartnersStore,
            Store<PartnersIntent, PartnersState, PartnersLabel> by storeFactory.create(
                name = "PartnersStore",
                initialState = PartnersState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<PartnersAction>() {
        override fun invoke() {
            dispatch(PartnersAction.ObserveTiers)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<PartnersIntent, PartnersAction, PartnersState, PartnersMessage, PartnersLabel>() {
        override fun executeAction(action: PartnersAction) {
            when (action) {
                PartnersAction.ObserveTiers -> observeTiers()
            }
        }

        override fun executeIntent(intent: PartnersIntent) {
            when (intent) {
                is PartnersIntent.PartnerClicked -> {
                    // Five of the thirty-nine have no site — two genuinely have none, one is an
                    // activity of the festival rather than a company, and one's address 404s. So a
                    // silent tap is the common case, and silence reads as a broken app.
                    intent.url
                        ?.let { publish(PartnersLabel.OpenUrl(it)) }
                        ?: dispatch(PartnersMessage.NoWebsiteTapped)
                }
            }
        }

        private fun observeTiers() {
            scope.launch {
                observePartnerTiers().collect { tiers ->
                    dispatch(PartnersMessage.TiersUpdated(tiers))
                }
            }
        }
    }

    // internal (not private) so PartnersReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<PartnersState, PartnersMessage> {
        override fun PartnersState.reduce(msg: PartnersMessage): PartnersState =
            when (msg) {
                is PartnersMessage.TiersUpdated -> {
                    copy(tiers = msg.tiers)
                }

                PartnersMessage.NoWebsiteTapped -> {
                    copy(noWebsiteTaps = noWebsiteTaps + 1)
                }
            }
    }
}
