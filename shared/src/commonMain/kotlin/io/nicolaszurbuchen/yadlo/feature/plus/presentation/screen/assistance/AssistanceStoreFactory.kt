package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveAssistanceGuideUseCase
import io.nicolaszurbuchen.yadlo.infra.ui.mailtoUrl
import kotlinx.coroutines.launch

/**
 * `tel:` rather than a dialogue. The platform's own dialer opens with the number already in it and
 * nothing is placed without a second tap — which is the behaviour someone reaching for 144 with one
 * hand needs, and the only one that is safe to give a row a reader might brush past.
 */
private const val TEL_SCHEME = "tel:"

/** Spaces and non-breaking spaces are how a number is read, never how it is dialled. */
private val NON_DIALLABLE = Regex("[^+0-9]")

interface AssistanceStore : Store<AssistanceIntent, AssistanceState, AssistanceLabel>

class AssistanceStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeAssistanceGuide: ObserveAssistanceGuideUseCase,
) {
    fun create(): AssistanceStore =
        object :
            AssistanceStore,
            Store<AssistanceIntent, AssistanceState, AssistanceLabel> by storeFactory.create(
                name = "AssistanceStore",
                initialState = AssistanceState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<AssistanceAction>() {
        override fun invoke() {
            dispatch(AssistanceAction.ObserveGuide)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<AssistanceIntent, AssistanceAction, AssistanceState, AssistanceMessage, AssistanceLabel>() {
        override fun executeAction(action: AssistanceAction) {
            when (action) {
                AssistanceAction.ObserveGuide -> observeGuide()
            }
        }

        override fun executeIntent(intent: AssistanceIntent) {
            when (intent) {
                is AssistanceIntent.NumberClicked -> {
                    publish(AssistanceLabel.OpenUrl("$TEL_SCHEME${intent.number.replace(NON_DIALLABLE, "")}"))
                }

                is AssistanceIntent.LostPropertyClicked -> {
                    publish(AssistanceLabel.OpenUrl(mailtoUrl(intent.email)))
                }
            }
        }

        private fun observeGuide() {
            scope.launch {
                observeAssistanceGuide().collect { guide ->
                    dispatch(AssistanceMessage.GuideUpdated(guide))
                }
            }
        }
    }

    // internal (not private) so AssistanceReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<AssistanceState, AssistanceMessage> {
        override fun AssistanceState.reduce(msg: AssistanceMessage): AssistanceState =
            when (msg) {
                is AssistanceMessage.GuideUpdated -> {
                    copy(guide = msg.guide, hasLoaded = true)
                }
            }
    }
}
