package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusPageId
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObservePlusPageUseCase
import kotlinx.coroutines.launch

interface PageStore : Store<PageIntent, PageState, PageLabel>

/**
 * [kind] arrives from the NavKey through the ViewModel, the same construction-parameter route the
 * fiche uses for its Happening id — a Route may only take lambdas, a Modifier or a ViewModel.
 *
 * It arrives as the presentation mirror because navigation may not name a domain type, and this is
 * the one place with business knowing both sides of that pair.
 */
class PageStoreFactory(
    private val storeFactory: StoreFactory,
    private val observePlusPage: ObservePlusPageUseCase,
    private val kind: PageKind,
) {
    fun create(): PageStore =
        object :
            PageStore,
            Store<PageIntent, PageState, PageLabel> by storeFactory.create(
                name = "PageStore",
                initialState = PageState(kind = kind),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<PageAction>() {
        override fun invoke() {
            dispatch(PageAction.ObservePage)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<PageIntent, PageAction, PageState, PageMessage, PageLabel>() {
        override fun executeAction(action: PageAction) {
            when (action) {
                PageAction.ObservePage -> observePage()
            }
        }

        override fun executeIntent(intent: PageIntent) {
            when (intent) {
                is PageIntent.LinkClicked -> publish(PageLabel.OpenUrl(intent.url))
            }
        }

        private fun observePage() {
            scope.launch {
                observePlusPage(kind.toPageId()).collect { page ->
                    dispatch(PageMessage.PageUpdated(page))
                }
            }
        }
    }

    /** The one translation between what the back stack carries and what the content is keyed by. */
    private fun PageKind.toPageId(): PlusPageId =
        when (this) {
            PageKind.RESPONSIBLE -> PlusPageId.RESPONSIBLE
            PageKind.SOCIAL -> PlusPageId.SOCIAL
        }

    // internal (not private) so PageReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<PageState, PageMessage> {
        override fun PageState.reduce(msg: PageMessage): PageState =
            when (msg) {
                is PageMessage.PageUpdated -> {
                    copy(page = msg.page)
                }
            }
    }
}
