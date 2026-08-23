package io.nicolaszurbuchen.yadlo.common.reminder.di

import io.nicolaszurbuchen.yadlo.common.reminder.domain.usecase.PlanRemindersUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val reminderModule =
    module {
        factoryOf(::PlanRemindersUseCase)
    }
