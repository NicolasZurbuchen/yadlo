package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.responsible

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveResponsiblePageUseCase
import kotlinx.coroutines.launch

interface ResponsibleStore : Store<ResponsibleIntent, ResponsibleState, ResponsibleLabel>

class ResponsibleStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeResponsiblePage: ObserveResponsiblePageUseCase,
) {
    fun create(): ResponsibleStore =
        object :
            ResponsibleStore,
            Store<ResponsibleIntent, ResponsibleState, ResponsibleLabel> by storeFactory.create(
                name = "ResponsibleStore",
                initialState = ResponsibleState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<ResponsibleAction>() {
        override fun invoke() {
            dispatch(ResponsibleAction.ObservePage)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<ResponsibleIntent, ResponsibleAction, ResponsibleState, ResponsibleMessage, ResponsibleLabel>() {
        override fun executeAction(action: ResponsibleAction) {
            when (action) {
                ResponsibleAction.ObservePage -> observePage()
            }
        }

        override fun executeIntent(intent: ResponsibleIntent) {
            // A charter's own site is the platform's business rather than the navigator's, the same
            // reasoning as an artist's own site on the fiche.
            when (intent) {
                is ResponsibleIntent.LinkClicked -> publish(ResponsibleLabel.OpenUrl(intent.url))
            }
        }

        private fun observePage() {
            scope.launch {
                observeResponsiblePage().collect { page ->
                    dispatch(ResponsibleMessage.PageUpdated(page))
                }
            }
        }
    }

    // internal (not private) so ResponsibleReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<ResponsibleState, ResponsibleMessage> {
        override fun ResponsibleState.reduce(msg: ResponsibleMessage): ResponsibleState =
            when (msg) {
                is ResponsibleMessage.PageUpdated -> {
                    copy(page = msg.page)
                }
            }
    }
}
