package io.nicolaszurbuchen.yadlo.common.plan.di

import io.nicolaszurbuchen.yadlo.cache.AppDatabase
import io.nicolaszurbuchen.yadlo.common.plan.data.datasource.local.PlanLocalDataSource
import io.nicolaszurbuchen.yadlo.common.plan.data.datasource.local.PlanLocalDataSourceImpl
import io.nicolaszurbuchen.yadlo.common.plan.data.repository.PlanRepositoryImpl
import io.nicolaszurbuchen.yadlo.common.plan.domain.repository.PlanRepository
import io.nicolaszurbuchen.yadlo.common.plan.domain.usecase.ToggleSavedUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val planModule =
    module {
        single { get<AppDatabase>().savedEntryQueries }

        singleOf(::PlanLocalDataSourceImpl) bind PlanLocalDataSource::class

        singleOf(::PlanRepositoryImpl) bind PlanRepository::class

        factoryOf(::ToggleSavedUseCase)
    }
