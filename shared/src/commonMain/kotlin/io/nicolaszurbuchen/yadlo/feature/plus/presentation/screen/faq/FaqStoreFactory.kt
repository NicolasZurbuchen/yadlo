package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveFaqUseCase
import kotlinx.coroutines.launch

interface FaqStore : Store<FaqIntent, FaqState, FaqLabel>

class FaqStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeFaq: ObserveFaqUseCase,
) {
    fun create(): FaqStore =
        object :
            FaqStore,
            Store<FaqIntent, FaqState, FaqLabel> by storeFactory.create(
                name = "FaqStore",
                initialState = FaqState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<FaqAction>() {
        override fun invoke() {
            dispatch(FaqAction.ObserveFaq)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<FaqIntent, FaqAction, FaqState, FaqMessage, FaqLabel>() {
        override fun executeAction(action: FaqAction) {
            when (action) {
                FaqAction.ObserveFaq -> observeQuestions()
            }
        }

        /**
         * Observed rather than read once, and this is the screen where that matters most: an answer
         * the association corrects mid-festival is exactly what the FAQ is for.
         */
        private fun observeQuestions() {
            scope.launch {
                observeFaq().collect { entries ->
                    dispatch(FaqMessage.FaqUpdated(entries))
                }
            }
        }
    }

    // internal (not private) so FaqReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<FaqState, FaqMessage> {
        override fun FaqState.reduce(msg: FaqMessage): FaqState =
            when (msg) {
                is FaqMessage.FaqUpdated -> {
                    copy(entries = msg.entries)
                }
            }
    }
}
