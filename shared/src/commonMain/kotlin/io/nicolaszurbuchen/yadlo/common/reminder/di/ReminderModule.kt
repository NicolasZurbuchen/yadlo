package io.nicolaszurbuchen.yadlo.common.reminder.di

import io.nicolaszurbuchen.yadlo.cache.AppDatabase
import io.nicolaszurbuchen.yadlo.common.reminder.data.datasource.local.ReminderSettingsLocalDataSource
import io.nicolaszurbuchen.yadlo.common.reminder.data.datasource.local.ReminderSettingsLocalDataSourceImpl
import io.nicolaszurbuchen.yadlo.common.reminder.data.repository.ReminderSettingsRepositoryImpl
import io.nicolaszurbuchen.yadlo.common.reminder.domain.repository.ReminderSettingsRepository
import io.nicolaszurbuchen.yadlo.common.reminder.domain.usecase.ObserveRemindersEnabledUseCase
import io.nicolaszurbuchen.yadlo.common.reminder.domain.usecase.PlanRemindersUseCase
import io.nicolaszurbuchen.yadlo.common.reminder.domain.usecase.SetRemindersEnabledUseCase
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
