package io.nicolaszurbuchen.yadlo.common.content.di

import io.nicolaszurbuchen.yadlo.cache.AppDatabase
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.local.ContentLocalDataSource
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.local.ContentLocalDataSourceImpl
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.ContentRemoteDataSource
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.ContentRemoteDataSourceImpl
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.api.ContentApi
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.api.ContentApiImpl
import io.nicolaszurbuchen.yadlo.common.content.data.repository.ContentRepositoryImpl
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.usecase.DerivePhaseUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val contentModule =
    module {
        single { get<AppDatabase>().cachedDocumentQueries }

        singleOf(::ContentApiImpl) bind ContentApi::class
        singleOf(::ContentRemoteDataSourceImpl) bind ContentRemoteDataSource::class
        singleOf(::ContentLocalDataSourceImpl) bind ContentLocalDataSource::class

        singleOf(::ContentRepositoryImpl) bind ContentRepository::class

        factoryOf(::DerivePhaseUseCase)
    }
