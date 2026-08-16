package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveOpeningDaysUseCase
import kotlinx.coroutines.launch

interface HoursStore : Store<HoursIntent, HoursState, HoursLabel>

class HoursStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeOpeningDays: ObserveOpeningDaysUseCase,
) {
    fun create(): HoursStore =
        object :
            HoursStore,
            Store<HoursIntent, HoursState, HoursLabel> by storeFactory.create(
                name = "HoursStore",
                initialState = HoursState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<HoursAction>() {
        override fun invoke() {
            dispatch(HoursAction.ObserveDays)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<HoursIntent, HoursAction, HoursState, HoursMessage, HoursLabel>() {
        override fun executeAction(action: HoursAction) {
            when (action) {
                HoursAction.ObserveDays -> observeDays()
            }
        }

        /**
         * Observed rather than read once: these hours are derived from the programme, so a
         * correction pushed during the festival moves them without anyone editing a time.
         */
        private fun observeDays() {
            scope.launch {
                observeOpeningDays().collect { days ->
                    dispatch(HoursMessage.DaysUpdated(days))
                }
            }
        }
    }

    // internal (not private) so HoursReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<HoursState, HoursMessage> {
        override fun HoursState.reduce(msg: HoursMessage): HoursState =
            when (msg) {
                is HoursMessage.DaysUpdated -> {
                    copy(days = msg.days)
                }
            }
    }
}
