package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.common.content.domain.model.StandKind
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveStandDirectoryUseCase
import kotlinx.coroutines.launch

interface StandsStore : Store<StandsIntent, StandsState, StandsLabel>

/**
 * [kind] arrives from the NavKey through the ViewModel, the same construction-parameter route the
 * fiche uses for its Happening id — a Route may only take lambdas, a Modifier or a ViewModel. It
 * arrives as the presentation mirror because navigation may not name a domain type.
 */
class StandsStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeStandDirectory: ObserveStandDirectoryUseCase,
    private val kind: StandsKindUiModel,
) {
    fun create(): StandsStore =
        object :
            StandsStore,
            Store<StandsIntent, StandsState, StandsLabel> by storeFactory.create(
                name = "StandsStore",
                initialState = StandsState(kind = kind.toStandKind()),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<StandsAction>() {
        override fun invoke() {
            dispatch(StandsAction.ObserveDirectory)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<StandsIntent, StandsAction, StandsState, StandsMessage, StandsLabel>() {
        override fun executeAction(action: StandsAction) {
            when (action) {
                StandsAction.ObserveDirectory -> observeDirectory()
            }
        }

        override fun executeIntent(intent: StandsIntent) {
            when (intent) {
                is StandsIntent.MarkToggled -> {
                    dispatch(StandsMessage.MarkToggled(intent.mark))
                }

                is StandsIntent.StandClicked -> {
                    publish(StandsLabel.NavigateToHappening(intent.happeningId))
                }
            }
        }

        private fun observeDirectory() {
            scope.launch {
                observeStandDirectory(state().kind).collect { directory ->
                    dispatch(StandsMessage.DirectoryUpdated(directory))
                }
            }
        }
    }

    /** The one translation between what the back stack carries and what the content is keyed by. */
    private fun StandsKindUiModel.toStandKind(): StandKind =
        when (this) {
            StandsKindUiModel.FOOD -> StandKind.FOOD
            StandsKindUiModel.MAKERS -> StandKind.MAKERS
        }

    // internal (not private) so StandsReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<StandsState, StandsMessage> {
        override fun StandsState.reduce(msg: StandsMessage): StandsState =
            when (msg) {
                is StandsMessage.DirectoryUpdated -> {
                    // The selection survives a refresh landing underneath it, even when the mark it
                    // names has just stopped existing: the mapper offers no chip for it and the list
                    // says so, which is a truer answer than silently widening back to everything.
                    copy(directory = msg.directory)
                }

                is StandsMessage.MarkToggled -> {
                    // Null is *Tout*: it clears rather than toggling, which is what makes it the way
                    // back to the whole list from any combination.
                    copy(
                        selectedMarks =
                            when (msg.mark) {
                                null -> emptySet()
                                in selectedMarks -> selectedMarks - msg.mark
                                else -> selectedMarks + msg.mark
                            },
                    )
                }
            }
    }
}
