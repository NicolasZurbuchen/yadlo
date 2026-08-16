package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveContactRouterUseCase
import io.nicolaszurbuchen.yadlo.infra.ui.mailtoUrl
import kotlinx.coroutines.launch

interface ContactStore : Store<ContactIntent, ContactState, ContactLabel>

class ContactStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeContactRouter: ObserveContactRouterUseCase,
) {
    fun create(): ContactStore =
        object :
            ContactStore,
            Store<ContactIntent, ContactState, ContactLabel> by storeFactory.create(
                name = "ContactStore",
                initialState = ContactState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<ContactAction>() {
        override fun invoke() {
            dispatch(ContactAction.ObserveRouter)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<ContactIntent, ContactAction, ContactState, ContactMessage, ContactLabel>() {
        override fun executeAction(action: ContactAction) {
            when (action) {
                ContactAction.ObserveRouter -> observeRouter()
            }
        }

        override fun executeIntent(intent: ContactIntent) {
            // It leaves the app, and that is the whole design: no backend, no stored message, and
            // the association's own inboxes keep receiving their own mail.
            when (intent) {
                is ContactIntent.EmailClicked -> publish(ContactLabel.OpenUrl(mailtoUrl(intent.address)))
            }
        }

        private fun observeRouter() {
            scope.launch {
                observeContactRouter().collect { router ->
                    dispatch(ContactMessage.RouterUpdated(router))
                }
            }
        }
    }

    // internal (not private) so ContactReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<ContactState, ContactMessage> {
        override fun ContactState.reduce(msg: ContactMessage): ContactState =
            when (msg) {
                is ContactMessage.RouterUpdated -> {
                    copy(router = msg.router)
                }
            }
    }
}
