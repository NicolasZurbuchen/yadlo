package io.nicolaszurbuchen.yadlo.infra.di

import io.nicolaszurbuchen.yadlo.infra.database.DatabaseDriverFactory
import io.nicolaszurbuchen.yadlo.infra.platform.Notifier
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * The bindings whose constructors differ by platform — both of these take the `Context` that
 * `androidContext()` provides, which is the entire reason neither can be declared in common code.
 */
val platformModule =
    module {
        singleOf(::DatabaseDriverFactory)
        singleOf(::Notifier)
    }
