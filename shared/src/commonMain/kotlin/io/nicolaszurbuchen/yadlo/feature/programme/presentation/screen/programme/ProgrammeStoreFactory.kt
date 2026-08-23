package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Phase
import io.nicolaszurbuchen.yadlo.common.content.domain.usecase.DerivePhaseUseCase
import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeContent
import io.nicolaszurbuchen.yadlo.feature.programme.domain.usecase.ObserveProgrammeContentUseCase
import io.nicolaszurbuchen.yadlo.infra.time.AppClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

interface ProgrammeStore : Store<ProgrammeIntent, ProgrammeState, ProgrammeLabel>

class ProgrammeStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeProgrammeContent: ObserveProgrammeContentUseCase,
    private val derivePhase: DerivePhaseUseCase,
    private val clock: AppClock,
) {
    fun create(): ProgrammeStore =
        object :
            ProgrammeStore,
            Store<ProgrammeIntent, ProgrammeState, ProgrammeLabel> by storeFactory.create(
                name = "ProgrammeStore",
                initialState = ProgrammeState(now = clock.now()),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<ProgrammeAction>() {
        override fun invoke() {
            dispatch(ProgrammeAction.ObserveContent)
            dispatch(ProgrammeAction.StartTicking)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<ProgrammeIntent, ProgrammeAction, ProgrammeState, ProgrammeMessage, ProgrammeLabel>() {
        override fun executeAction(action: ProgrammeAction) {
            when (action) {
                ProgrammeAction.ObserveContent -> observeContent()
                ProgrammeAction.StartTicking -> startTicking()
            }
        }

        override fun executeIntent(intent: ProgrammeIntent) {
            when (intent) {
                is ProgrammeIntent.ScopeSelected -> {
                    dispatch(ProgrammeMessage.ScopeSelected(intent.scope))
                }

                is ProgrammeIntent.CategoryToggled -> {
                    val current = state().selectedCategoryIds
                    val next =
                        if (intent.categoryId in current) {
                            current - intent.categoryId
                        } else {
                            current + intent.categoryId
                        }
                    dispatch(ProgrammeMessage.CategoriesChanged(next))
                }

                ProgrammeIntent.AllCategoriesSelected -> {
                    dispatch(ProgrammeMessage.CategoriesChanged(emptySet()))
                }

                is ProgrammeIntent.SlotClicked -> {
                    publish(ProgrammeLabel.NavigateToHappening(intent.happeningId))
                }
            }
        }

        private fun observeContent() {
            scope.launch {
                observeProgrammeContent().collect { content ->
                    dispatch(
                        ProgrammeMessage.ContentUpdated(content = content, defaultScope = defaultScopeFor(content)),
                    )
                }
            }
        }

        /**
         * The pills are written in minutes, so a minute is the resolution that matters: a finer tick
         * would redraw the same string sixty times to catch a boundary it catches anyway. The cost
         * is that "se termine · 3 min" can be up to a minute stale, which is inside the accuracy
         * anyone reads it with while walking.
         *
         * The second collector is what makes a minute-long interval compatible with the debug
         * time-travel panel: a jump is not time passing, so it does not wait for the tick. Nothing
         * emits on it in release.
         */
        private fun startTicking() {
            scope.launch {
                while (true) {
                    delay(TICK_INTERVAL)
                    dispatch(ProgrammeMessage.Ticked(clock.now()))
                }
            }

            scope.launch {
                clock.jumps.collect { dispatch(ProgrammeMessage.Ticked(clock.now())) }
            }
        }

        /**
         * What the tab opens on, and the second place the Phase decides what a visitor sees.
         *
         * Three answers, one per question the year is asking:
         *
         * - **ANNOUNCED → the Catalogue.** The week the programme drops, nobody has read the bill
         *   yet. The useful screen is the one that says what there is; hours on top of a list nobody
         *   can parse yet are noise.
         * - **LIVE → the day you are standing in.** "What is on now" is the only question on site,
         *   and it is about one day.
         * - **everything else → the whole weekend.** Off season, the week before and the weeks after
         *   are all read the same way — from a sofa, across all three days, deciding or
         *   remembering. APPROACHING is the one that matters, because it is the only time anyone
         *   realistically builds a Plan and they do not build it a day at a time.
         *
         * Only the opening scope. [ProgrammeState.selectedScope] takes this once and never again, so
         * a Phase that turns over while the app is open moves nothing.
         */
        private fun defaultScopeFor(content: ProgrammeContent): ProgrammeScopeUiModel {
            val phase =
                derivePhase(
                    days = content.days,
                    hasPublishedProgramme = content.hasPublishedProgramme,
                )

            return when (phase) {
                Phase.ANNOUNCED -> {
                    ProgrammeScopeUiModel.Catalogue
                }

                Phase.LIVE -> {
                    liveDayFor(content)?.let { ProgrammeScopeUiModel.Day(it) } ?: ProgrammeScopeUiModel.AllDays
                }

                else -> {
                    ProgrammeScopeUiModel.AllDays
                }
            }
        }

        /**
         * The day the visitor is standing in — the first one that has not ended.
         *
         * Against the FestivalDay window rather than the calendar date, which is the same rule that
         * keeps a 01:30 set on Friday: at 01:00 on the Saturday morning the day still on is Friday,
         * and opening on Saturday would hide the set playing thirty metres away. It also answers the
         * overnight gaps, where no day is current at all — 04:00 on the Saturday lands on the
         * Saturday, the next one to open.
         */
        private fun liveDayFor(content: ProgrammeContent): String? {
            val now = clock.now()

            return content.days.firstOrNull { now < it.end }?.id ?: content.days.lastOrNull()?.id
        }
    }

    // internal (not private) so ProgrammeReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<ProgrammeState, ProgrammeMessage> {
        override fun ProgrammeState.reduce(msg: ProgrammeMessage): ProgrammeState =
            when (msg) {
                is ProgrammeMessage.ContentUpdated -> {
                    copy(
                        content = msg.content,
                        // A scope the visitor chose survives a refresh; a day that no longer exists
                        // in the content does not, and neither does an unmade choice. See
                        // ProgrammeState for why every other case leaves it alone.
                        selectedScope =
                            selectedScope
                                ?.takeIf { scope ->
                                    scope !is ProgrammeScopeUiModel.Day ||
                                        msg.content.days.any { it.id == scope.id }
                                }
                                ?: msg.defaultScope,
                    )
                }

                is ProgrammeMessage.Ticked -> {
                    copy(now = msg.now)
                }

                is ProgrammeMessage.ScopeSelected -> {
                    copy(selectedScope = msg.scope)
                }

                is ProgrammeMessage.CategoriesChanged -> {
                    copy(selectedCategoryIds = msg.categoryIds)
                }
            }
    }

    private companion object {
        val TICK_INTERVAL = 1.minutes
    }
}
