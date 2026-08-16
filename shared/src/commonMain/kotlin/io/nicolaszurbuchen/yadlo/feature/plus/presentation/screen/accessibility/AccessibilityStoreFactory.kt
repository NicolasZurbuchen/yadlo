package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.accessibility

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveAccessibilityGuideUseCase
import io.nicolaszurbuchen.yadlo.infra.ui.mailtoUrl
import kotlinx.coroutines.launch

/**
 * `mailto:` with no subject and no body. Prefilling either would need percent-encoding a French
 * accent for every mail client on two platforms, to save a tap on a screen that has already told
 * the reader what to write about.
 */

interface AccessibilityStore : Store<AccessibilityIntent, AccessibilityState, AccessibilityLabel>

class AccessibilityStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeAccessibilityGuide: ObserveAccessibilityGuideUseCase,
) {
    fun create(): AccessibilityStore =
        object :
            AccessibilityStore,
            Store<AccessibilityIntent, AccessibilityState, AccessibilityLabel> by storeFactory.create(
                name = "AccessibilityStore",
                initialState = AccessibilityState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<AccessibilityAction>() {
        override fun invoke() {
            dispatch(AccessibilityAction.ObserveGuide)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<
            AccessibilityIntent,
            AccessibilityAction,
            AccessibilityState,
            AccessibilityMessage,
            AccessibilityLabel,
        >() {
        override fun executeAction(action: AccessibilityAction) {
            when (action) {
                AccessibilityAction.ObserveGuide -> observeGuide()
            }
        }

        override fun executeIntent(intent: AccessibilityIntent) {
            when (intent) {
                is AccessibilityIntent.ContactClicked -> {
                    publish(AccessibilityLabel.OpenUrl(mailtoUrl(intent.email)))
                }
            }
        }

        private fun observeGuide() {
            scope.launch {
                observeAccessibilityGuide().collect { guide ->
                    dispatch(AccessibilityMessage.GuideUpdated(guide))
                }
            }
        }
    }

    // internal (not private) so AccessibilityReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<AccessibilityState, AccessibilityMessage> {
        override fun AccessibilityState.reduce(msg: AccessibilityMessage): AccessibilityState =
            when (msg) {
                is AccessibilityMessage.GuideUpdated -> {
                    copy(guide = msg.guide, hasLoaded = true)
                }
            }
    }
}
