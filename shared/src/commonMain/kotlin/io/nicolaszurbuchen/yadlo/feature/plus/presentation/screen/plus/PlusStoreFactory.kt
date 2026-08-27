package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObservePlusOverviewUseCase
import io.nicolaszurbuchen.yadlo.infra.format.mailtoUrl
import kotlinx.coroutines.launch

interface PlusStore : Store<PlusIntent, PlusState, PlusLabel>

class PlusStoreFactory(
    private val storeFactory: StoreFactory,
    private val observePlusOverview: ObservePlusOverviewUseCase,
) {
    fun create(): PlusStore =
        object :
            PlusStore,
            Store<PlusIntent, PlusState, PlusLabel> by storeFactory.create(
                name = "PlusStore",
                initialState = PlusState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<PlusAction>() {
        override fun invoke() {
            dispatch(PlusAction.ObserveOverview)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<PlusIntent, PlusAction, PlusState, PlusMessage, PlusLabel>() {
        override fun executeAction(action: PlusAction) {
            when (action) {
                PlusAction.ObserveOverview -> observeOverview()
            }
        }

        override fun executeIntent(intent: PlusIntent) {
            // Read off the state rather than carried in the intent: only these two rows exist
            // because the content supplied an address, so the same read decides both.
            when (intent) {
                PlusIntent.NewsletterClicked -> {
                    state().overview?.newsletterUrl?.let { publish(PlusLabel.OpenUrl(it)) }
                }

                PlusIntent.ReportClicked -> {
                    state().overview?.reportEmail?.let { publish(PlusLabel.OpenUrl(mailtoUrl(it))) }
                }

                is PlusIntent.SocialClicked -> {
                    publish(PlusLabel.OpenUrl(intent.url))
                }
            }
        }

        /**
         * Observed rather than read once: this is the app's root for everything that is not the
         * programme, so it is the screen most likely to be open when a refresh lands — and a
         * refresh is exactly what turns a section from absent into published.
         */
        private fun observeOverview() {
            scope.launch {
                observePlusOverview().collect { overview ->
                    dispatch(PlusMessage.OverviewUpdated(overview))
                }
            }
        }
    }

    // internal (not private) so PlusReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<PlusState, PlusMessage> {
        override fun PlusState.reduce(msg: PlusMessage): PlusState =
            when (msg) {
                is PlusMessage.OverviewUpdated -> {
                    copy(overview = msg.overview)
                }
            }
    }
}
