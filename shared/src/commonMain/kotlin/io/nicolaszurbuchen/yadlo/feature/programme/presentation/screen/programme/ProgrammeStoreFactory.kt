package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeContent
import io.nicolaszurbuchen.yadlo.feature.programme.domain.usecase.ObserveProgrammeContentUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

interface ProgrammeStore : Store<ProgrammeIntent, ProgrammeState, ProgrammeLabel>

class ProgrammeStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeProgrammeContent: ObserveProgrammeContentUseCase,
    private val clock: Clock,
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
                is ProgrammeIntent.DaySelected -> {
                    dispatch(ProgrammeMessage.DaySelected(intent.dayId))
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
                        ProgrammeMessage.ContentUpdated(content = content, defaultDayId = defaultDayFor(content)),
                    )
                }
            }
        }

        /**
         * The pills are written in minutes, so a minute is the resolution that matters: a finer tick
         * would redraw the same string sixty times to catch a boundary it catches anyway. The cost
         * is that "se termine · 3 min" can be up to a minute stale, which is inside the accuracy
         * anyone reads it with while walking.
         */
        private fun startTicking() {
            scope.launch {
                while (true) {
                    delay(TICK_INTERVAL)
                    dispatch(ProgrammeMessage.Ticked(clock.now()))
                }
            }
        }

        /**
         * The day the visitor is standing in — the first one that has not ended.
         *
         * Against the FestivalDay window rather than the calendar date, which is the same rule that
         * keeps a 01:30 set on Friday: at 01:00 on the Saturday morning the day still on is Friday,
         * and opening on Saturday would hide the set playing thirty metres away. Before the festival
         * that lands on day one, after it on the last day.
         */
        private fun defaultDayFor(content: ProgrammeContent): String? {
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
                        // A day the visitor picked survives a refresh; one that no longer exists in
                        // the content does not, and neither does an unmade choice.
                        selectedDayId =
                            selectedDayId?.takeIf { id -> msg.content.days.any { it.id == id } }
                                ?: msg.defaultDayId,
                    )
                }

                is ProgrammeMessage.Ticked -> {
                    copy(now = msg.now)
                }

                is ProgrammeMessage.DaySelected -> {
                    copy(selectedDayId = msg.dayId)
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
