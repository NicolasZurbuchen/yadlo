package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveContactRouterUseCase
import kotlinx.coroutines.launch

private const val MAIL_SCHEME = "mailto:"

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
            // Both leave the app, and that is the whole design: no backend, no stored message, and
            // the association's own recruitment site keeps receiving its applications.
            when (intent) {
                is ContactIntent.EmailClicked -> publish(ContactLabel.OpenUrl("$MAIL_SCHEME${intent.address}"))
                is ContactIntent.SignupClicked -> publish(ContactLabel.OpenUrl(intent.url))
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
                    copy(router = msg.router, hasLoaded = true)
                }
            }
    }
}
