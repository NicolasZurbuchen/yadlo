package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.common.plan.domain.usecase.ToggleSavedUseCase
import io.nicolaszurbuchen.yadlo.feature.happening.domain.model.HappeningKind
import io.nicolaszurbuchen.yadlo.feature.happening.domain.usecase.ObserveHappeningDetailUseCase
import io.nicolaszurbuchen.yadlo.infra.time.AppClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

interface HappeningStore : Store<HappeningIntent, HappeningState, HappeningLabel>

class HappeningStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeHappeningDetail: ObserveHappeningDetailUseCase,
    private val toggleSaved: ToggleSavedUseCase,
    private val clock: AppClock,
    private val happeningId: String,
) {
    fun create(): HappeningStore =
        object :
            HappeningStore,
            Store<HappeningIntent, HappeningState, HappeningLabel> by storeFactory.create(
                name = "HappeningStore",
                initialState = HappeningState(now = clock.now()),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<HappeningAction>() {
        override fun invoke() {
            dispatch(HappeningAction.ObserveDetail)
            dispatch(HappeningAction.StartTicking)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<HappeningIntent, HappeningAction, HappeningState, HappeningMessage, HappeningLabel>() {
        override fun executeAction(action: HappeningAction) {
            when (action) {
                HappeningAction.ObserveDetail -> observeDetail()
                HappeningAction.StartTicking -> startTicking()
            }
        }

        /**
         * Neither heart dispatches a Message. The detail flow is a join over the content and the
         * Plan, so a write to the Plan comes back through the same collector the fiche is already
         * reading — the filled heart is the repository answering, never the screen assuming it was
         * obeyed.
         */
        override fun executeIntent(intent: HappeningIntent) {
            when (intent) {
                is HappeningIntent.LinkClicked -> {
                    publish(HappeningLabel.OpenUrl(intent.url))
                }

                is HappeningIntent.SlotHeartClicked -> {
                    scope.launch { toggleSaved(id = intent.slotId, kind = SavedKind.SLOT) }
                }

                HappeningIntent.WishlistHeartClicked -> {
                    scope.launch { toggleSaved(id = happeningId, kind = SavedKind.STAND) }
                }
            }
        }

        /**
         * Observed rather than read once, unlike the template's detail screen: the content refreshes
         * under whatever is on screen, and a fiche is exactly where someone sits long enough for a
         * refresh to land — reading a set's time while the association corrects it.
         */
        private fun observeDetail() {
            scope.launch {
                observeHappeningDetail(happeningId).collect { detail ->
                    dispatch(HappeningMessage.DetailUpdated(detail = detail, kind = detail?.kind?.toUiModel()))
                }
            }
        }

        /** The same minute as the Programme, for the same pills. See ProgrammeStoreFactory. */
        private fun startTicking() {
            scope.launch {
                while (true) {
                    delay(TICK_INTERVAL)
                    dispatch(HappeningMessage.Ticked(clock.now()))
                }
            }

            scope.launch {
                clock.jumps.collect { dispatch(HappeningMessage.Ticked(clock.now())) }
            }
        }
    }

    // internal (not private) so HappeningReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<HappeningState, HappeningMessage> {
        override fun HappeningState.reduce(msg: HappeningMessage): HappeningState =
            when (msg) {
                is HappeningMessage.DetailUpdated -> {
                    copy(detail = msg.detail, kind = msg.kind, isLoaded = true)
                }

                is HappeningMessage.Ticked -> {
                    copy(now = msg.now)
                }
            }
    }

    private companion object {
        val TICK_INTERVAL = 1.minutes
    }
}

private fun HappeningKind.toUiModel(): HappeningKindUiModel =
    when (this) {
        HappeningKind.ARTIST -> HappeningKindUiModel.ARTIST
        HappeningKind.ACTIVITY -> HappeningKindUiModel.ACTIVITY
        HappeningKind.STAND -> HappeningKindUiModel.STAND
    }
