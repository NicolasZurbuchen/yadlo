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
import io.nicolaszurbuchen.yadlo.infra.time.AppClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

interface HomeStore : Store<HomeIntent, HomeState, HomeLabel>

class HomeStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeHomeContent: ObserveHomeContentUseCase,
    private val derivePhase: DerivePhaseUseCase,
    private val clock: AppClock,
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
                    // Both heroes land on Programme. Mon Yadlo was the obvious target for the
                    // APPROACHING one until you follow it through: a Plan is built by saving rows
                    // in Programme, so sending someone to their empty Plan is sending them one tab
                    // short of the thing the hero is asking them to do.
                    publish(HomeLabel.NavigateToProgramme)
                }

                is HomeIntent.AnnouncementClicked -> {
                    publish(HomeLabel.OpenUrl(intent.url))
                }

                is HomeIntent.SocialClicked -> {
                    publish(HomeLabel.OpenUrl(intent.url))
                }

                HomeIntent.AllAnnouncementsClicked -> {
                    publish(HomeLabel.NavigateToAnnouncements)
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

            // A debug time-travel jump is not time passing, so it does not wait out the interval —
            // and the interval here is a minute, which would make the panel useless for checking
            // the phase stack. Nothing emits on this in release.
            scope.launch {
                clock.jumps.collect {
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
        /**
         * One minute. Everything on this screen that moves is measured in days or in phases, and
         * both turn on a calendar boundary — a per-second tick would redraw the same string sixty
         * times to catch a midnight it will catch anyway. Programme's live pills will want finer,
         * and that is Programme's store to decide.
         */
        val TICK_INTERVAL = 1.minutes
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
