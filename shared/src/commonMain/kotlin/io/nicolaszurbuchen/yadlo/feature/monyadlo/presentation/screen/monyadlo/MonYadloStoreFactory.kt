package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.usecase.ObserveMonYadloContentUseCase
import io.nicolaszurbuchen.yadlo.infra.time.AppClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

interface MonYadloStore : Store<MonYadloIntent, MonYadloState, MonYadloLabel>

class MonYadloStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeMonYadloContent: ObserveMonYadloContentUseCase,
    private val clock: AppClock,
) {
    fun create(): MonYadloStore =
        object :
            MonYadloStore,
            Store<MonYadloIntent, MonYadloState, MonYadloLabel> by storeFactory.create(
                name = "MonYadloStore",
                initialState = MonYadloState(now = clock.now()),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<MonYadloAction>() {
        override fun invoke() {
            dispatch(MonYadloAction.ObserveContent)
            dispatch(MonYadloAction.StartTicking)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<MonYadloIntent, MonYadloAction, MonYadloState, MonYadloMessage, MonYadloLabel>() {
        override fun executeAction(action: MonYadloAction) {
            when (action) {
                MonYadloAction.ObserveContent -> observeContent()
                MonYadloAction.StartTicking -> startTicking()
            }
        }

        /**
         * One collector for both repositories, because the UseCase already joins them: a heart
         * tapped on a fiche removes the row from here without this screen being told about it.
         */
        private fun observeContent() {
            scope.launch {
                observeMonYadloContent().collect { content ->
                    dispatch(MonYadloMessage.ContentUpdated(content))
                }
            }
        }

        /** The Programme's minute, for the Programme's pills. See ProgrammeStoreFactory. */
        private fun startTicking() {
            scope.launch {
                while (true) {
                    delay(TICK_INTERVAL)
                    dispatch(MonYadloMessage.Ticked(clock.now()))
                }
            }

            scope.launch {
                clock.jumps.collect { dispatch(MonYadloMessage.Ticked(clock.now())) }
            }
        }
    }

    // internal (not private) so MonYadloReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<MonYadloState, MonYadloMessage> {
        override fun MonYadloState.reduce(msg: MonYadloMessage): MonYadloState =
            when (msg) {
                is MonYadloMessage.ContentUpdated -> {
                    copy(content = msg.content)
                }

                is MonYadloMessage.Ticked -> {
                    copy(now = msg.now)
                }
            }
    }

    private companion object {
        val TICK_INTERVAL = 1.minutes
    }
}
