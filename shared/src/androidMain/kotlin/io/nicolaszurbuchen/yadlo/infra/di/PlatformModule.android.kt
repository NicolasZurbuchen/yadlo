package io.nicolaszurbuchen.yadlo.infra.di

import io.nicolaszurbuchen.yadlo.infra.database.DatabaseDriverFactory
import io.nicolaszurbuchen.yadlo.infra.image.CoilImageCache
import io.nicolaszurbuchen.yadlo.infra.image.ImageCache
import io.nicolaszurbuchen.yadlo.infra.notification.Notifier
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * The bindings whose constructors differ by platform — every one of these takes the `Context` that
 * `androidContext()` provides, which is the entire reason none can be declared in common code.
 *
 * The image cache is the third, and the only one whose parameter is not spelled `Context`: Coil's
 * `PlatformContext` *is* `Context` on Android, so `get()` resolves the same binding the other two
 * use. On iOS it is a singleton with no graph entry at all, which is why the twin of this file
 * writes it out by hand.
 */
val platformModule =
    module {
        singleOf(::DatabaseDriverFactory)
        singleOf(::Notifier)
        single<ImageCache> { CoilImageCache(get()) }
    }
