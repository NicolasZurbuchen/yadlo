package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Phase
import io.nicolaszurbuchen.yadlo.common.content.domain.usecase.DerivePhaseUseCase
import io.nicolaszurbuchen.yadlo.feature.home.domain.model.HomeContent
import io.nicolaszurbuchen.yadlo.feature.home.domain.usecase.ObserveHomeContentUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

interface HomeStore : Store<HomeIntent, HomeState, HomeLabel>

class HomeStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeHomeContent: ObserveHomeContentUseCase,
    private val derivePhase: DerivePhaseUseCase,
    private val clock: Clock,
) {
    fun create(): HomeStore =
        object :
            HomeStore,
            Store<HomeIntent, HomeState, HomeLabel> by storeFactory.create(
                name = "HomeStore",
                initialState = HomeState(now = clock.now(), phase = PhaseUiModel.OFF_SEASON),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<HomeAction>() {
        override fun invoke() {
            dispatch(HomeAction.ObserveContent)
            dispatch(HomeAction.StartTicking)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<HomeIntent, HomeAction, HomeState, HomeMessage, HomeLabel>() {
        override fun executeAction(action: HomeAction) {
            when (action) {
                HomeAction.ObserveContent -> observeContent()
                HomeAction.StartTicking -> startTicking()
            }
        }

        override fun executeIntent(intent: HomeIntent) {
            when (intent) {
                HomeIntent.HeroClicked -> {
                    // The hero only renders in these two phases, and its destination is the whole
                    // difference between them: ANNOUNCED says the programme exists, APPROACHING
                    // says build your Plan while you still realistically will.
                    if (state().phase == PhaseUiModel.APPROACHING) {
                        publish(HomeLabel.NavigateToMonYadlo)
                    } else {
                        publish(HomeLabel.NavigateToProgramme)
                    }
                }

                is HomeIntent.AnnouncementClicked -> {
                    publish(HomeLabel.OpenUrl(intent.url))
                }
            }
        }

        private fun observeContent() {
            scope.launch {
                observeHomeContent().collect { content ->
                    dispatch(HomeMessage.ContentUpdated(content = content, phase = phaseOf(content)))
                }
            }
        }

        /**
         * A Phase is not a launch-time reading: the app can be open across midnight on the opening
         * day, and the countdown is measured against this same instant so the two never disagree.
         */
        private fun startTicking() {
            scope.launch {
                while (true) {
                    delay(TICK_INTERVAL)
                    dispatch(HomeMessage.Ticked(now = clock.now(), phase = phaseOf(state().content)))
                }
            }
        }

        private fun phaseOf(content: HomeContent?): PhaseUiModel =
            derivePhase(
                days = content?.days.orEmpty(),
                hasPublishedProgramme = content?.hasPublishedProgramme == true,
            ).toUiModel()
    }

    // internal (not private) so HomeReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<HomeState, HomeMessage> {
        override fun HomeState.reduce(msg: HomeMessage): HomeState =
            when (msg) {
                is HomeMessage.ContentUpdated -> copy(content = msg.content, phase = msg.phase)
                is HomeMessage.Ticked -> copy(now = msg.now, phase = msg.phase)
            }
    }

    private companion object {
        /** One second because the countdown shows seconds; every other block is indifferent to it. */
        val TICK_INTERVAL = 1.seconds
    }
}

private fun Phase.toUiModel(): PhaseUiModel =
    when (this) {
        Phase.OFF_SEASON -> PhaseUiModel.OFF_SEASON
        Phase.ANNOUNCED -> PhaseUiModel.ANNOUNCED
        Phase.APPROACHING -> PhaseUiModel.APPROACHING
        Phase.LIVE -> PhaseUiModel.LIVE
        Phase.ENDED -> PhaseUiModel.ENDED
    }
