package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.announcements

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.home.domain.usecase.ObserveHomeContentUseCase
import kotlinx.coroutines.launch

interface AnnouncementsStore : Store<AnnouncementsIntent, AnnouncementsState, AnnouncementsLabel>

class AnnouncementsStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeHomeContent: ObserveHomeContentUseCase,
) {
    fun create(): AnnouncementsStore =
        object :
            AnnouncementsStore,
            Store<AnnouncementsIntent, AnnouncementsState, AnnouncementsLabel> by storeFactory.create(
                name = "AnnouncementsStore",
                initialState = AnnouncementsState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<AnnouncementsAction>() {
        override fun invoke() {
            dispatch(AnnouncementsAction.ObserveContent)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<AnnouncementsIntent, AnnouncementsAction, AnnouncementsState, AnnouncementsMessage, AnnouncementsLabel>() {
        override fun executeAction(action: AnnouncementsAction) {
            when (action) {
                AnnouncementsAction.ObserveContent -> observeContent()
            }
        }

        override fun executeIntent(intent: AnnouncementsIntent) {
            when (intent) {
                is AnnouncementsIntent.AnnouncementClicked -> {
                    publish(AnnouncementsLabel.OpenUrl(intent.url))
                }
            }
        }

        /**
         * The same narrowing Accueil reads, so an annonce that belongs to a past edition is missing
         * from both rather than from one of them.
         */
        private fun observeContent() {
            scope.launch {
                observeHomeContent().collect { content ->
                    dispatch(AnnouncementsMessage.AnnouncementsUpdated(content.announcements))
                }
            }
        }
    }

    // internal (not private) so AnnouncementsReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<AnnouncementsState, AnnouncementsMessage> {
        override fun AnnouncementsState.reduce(msg: AnnouncementsMessage): AnnouncementsState =
            when (msg) {
                is AnnouncementsMessage.AnnouncementsUpdated -> {
                    copy(isLoading = false, announcements = msg.announcements)
                }
            }
    }
}
