package io.nicolaszurbuchen.yadlo.infra.di

import io.nicolaszurbuchen.yadlo.infra.database.DatabaseDriverFactory
import io.nicolaszurbuchen.yadlo.infra.platform.Notifier
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * The iOS twin of the Android module of the same name: the bindings whose constructors differ by
 * platform. Both factories take nothing here, where Android's take a `Context`, which is the entire
 * reason neither can be declared in common code.
 */
val platformModule =
    module {
        singleOf(::DatabaseDriverFactory)
        singleOf(::Notifier)
    }
