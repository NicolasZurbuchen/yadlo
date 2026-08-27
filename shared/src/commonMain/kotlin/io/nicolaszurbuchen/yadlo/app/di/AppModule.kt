package io.nicolaszurbuchen.yadlo.app.di

import io.nicolaszurbuchen.yadlo.app.navigation.appNavigationModule
import io.nicolaszurbuchen.yadlo.app.notification.reminderSchedulerModule
import io.nicolaszurbuchen.yadlo.core.content.di.contentModule
import io.nicolaszurbuchen.yadlo.core.plan.di.planModule
import io.nicolaszurbuchen.yadlo.core.reminder.di.reminderModule
import io.nicolaszurbuchen.yadlo.feature.happening.di.happeningModule
import io.nicolaszurbuchen.yadlo.feature.home.di.homeModule
import io.nicolaszurbuchen.yadlo.feature.monyadlo.di.monYadloModule
import io.nicolaszurbuchen.yadlo.feature.plus.di.plusModule
import io.nicolaszurbuchen.yadlo.feature.programme.di.programmeModule
import io.nicolaszurbuchen.yadlo.feature.search.di.searchModule
import io.nicolaszurbuchen.yadlo.infra.database.databaseModule
import io.nicolaszurbuchen.yadlo.infra.mvi.storeModule
import io.nicolaszurbuchen.yadlo.infra.navigation.infraNavigationModule
import io.nicolaszurbuchen.yadlo.infra.network.networkModule
import io.nicolaszurbuchen.yadlo.infra.notification.notificationModule
import io.nicolaszurbuchen.yadlo.infra.time.timeModule

val appModule =
    listOf(
        appNavigationModule,
        contentModule,
        databaseModule,
        happeningModule,
        homeModule,
        infraNavigationModule,
        monYadloModule,
        networkModule,
        notificationModule,
        planModule,
        plusModule,
        programmeModule,
        reminderModule,
        reminderSchedulerModule,
        searchModule,
        storeModule,
        timeModule,
    )
