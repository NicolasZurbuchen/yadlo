package io.nicolaszurbuchen.yadlo.core.reminder.di

import io.nicolaszurbuchen.yadlo.cache.AppDatabase
import io.nicolaszurbuchen.yadlo.core.reminder.data.datasource.local.ReminderSettingsLocalDataSource
import io.nicolaszurbuchen.yadlo.core.reminder.data.datasource.local.ReminderSettingsLocalDataSourceImpl
import io.nicolaszurbuchen.yadlo.core.reminder.data.repository.ReminderSettingsRepositoryImpl
import io.nicolaszurbuchen.yadlo.core.reminder.domain.repository.ReminderSettingsRepository
import io.nicolaszurbuchen.yadlo.core.reminder.domain.usecase.ObserveRemindersEnabledUseCase
import io.nicolaszurbuchen.yadlo.core.reminder.domain.usecase.PlanRemindersUseCase
import io.nicolaszurbuchen.yadlo.core.reminder.domain.usecase.SetRemindersEnabledUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val reminderModule =
    module {
        single { get<AppDatabase>().reminderSettingQueries }

        singleOf(::ReminderSettingsLocalDataSourceImpl) bind ReminderSettingsLocalDataSource::class

        singleOf(::ReminderSettingsRepositoryImpl) bind ReminderSettingsRepository::class

        factoryOf(::PlanRemindersUseCase)
        factoryOf(::ObserveRemindersEnabledUseCase)
        factoryOf(::SetRemindersEnabledUseCase)
    }
