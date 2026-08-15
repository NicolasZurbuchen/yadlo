package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveStoryPageUseCase
import kotlinx.coroutines.launch

interface StoryStore : Store<StoryIntent, StoryState, StoryLabel>

class StoryStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeStoryPage: ObserveStoryPageUseCase,
) {
    fun create(): StoryStore =
        object :
            StoryStore,
            Store<StoryIntent, StoryState, StoryLabel> by storeFactory.create(
                name = "StoryStore",
                initialState = StoryState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<StoryAction>() {
        override fun invoke() {
            dispatch(StoryAction.ObserveStory)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<StoryIntent, StoryAction, StoryState, StoryMessage, StoryLabel>() {
        override fun executeAction(action: StoryAction) {
            when (action) {
                StoryAction.ObserveStory -> observeStory()
            }
        }

        private fun observeStory() {
            scope.launch {
                observeStoryPage().collect { page ->
                    dispatch(StoryMessage.StoryUpdated(page))
                }
            }
        }
    }

    // internal (not private) so StoryReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<StoryState, StoryMessage> {
        override fun StoryState.reduce(msg: StoryMessage): StoryState =
            when (msg) {
                is StoryMessage.StoryUpdated -> {
                    copy(page = msg.page, hasLoaded = true)
                }
            }
    }
}
