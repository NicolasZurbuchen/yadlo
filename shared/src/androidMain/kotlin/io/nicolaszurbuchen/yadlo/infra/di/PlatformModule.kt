package io.nicolaszurbuchen.yadlo.infra.di

import io.nicolaszurbuchen.yadlo.infra.database.DatabaseDriverFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val platformModule =
    module {
        singleOf(::DatabaseDriverFactory)
    }
