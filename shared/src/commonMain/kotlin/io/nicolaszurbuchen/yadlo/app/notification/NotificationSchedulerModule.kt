package io.nicolaszurbuchen.yadlo.app.notification

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val reminderSchedulerModule =
    module {
        singleOf(::ReminderScheduler)
    }
