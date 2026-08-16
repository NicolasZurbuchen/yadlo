package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveTransportUseCase
import kotlinx.coroutines.launch

interface AccessStore : Store<AccessIntent, AccessState, AccessLabel>

class AccessStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeTransport: ObserveTransportUseCase,
) {
    fun create(): AccessStore =
        object :
            AccessStore,
            Store<AccessIntent, AccessState, AccessLabel> by storeFactory.create(
                name = "AccessStore",
                initialState = AccessState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<AccessAction>() {
        override fun invoke() {
            dispatch(AccessAction.ObserveTransport)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<AccessIntent, AccessAction, AccessState, AccessMessage, AccessLabel>() {
        override fun executeAction(action: AccessAction) {
            when (action) {
                AccessAction.ObserveTransport -> observeTransportBlock()
            }
        }

        override fun executeIntent(intent: AccessIntent) {
            when (intent) {
                is AccessIntent.LinkClicked -> publish(AccessLabel.OpenUrl(intent.url))
            }
        }

        private fun observeTransportBlock() {
            scope.launch {
                observeTransport().collect { transport ->
                    dispatch(AccessMessage.TransportUpdated(transport))
                }
            }
        }
    }

    // internal (not private) so AccessReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<AccessState, AccessMessage> {
        override fun AccessState.reduce(msg: AccessMessage): AccessState =
            when (msg) {
                is AccessMessage.TransportUpdated -> {
                    copy(transport = msg.transport, hasLoaded = true)
                }
            }
    }
}
