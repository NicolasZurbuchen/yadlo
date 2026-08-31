package io.nicolaszurbuchen.yadlo.infra.di

import coil3.PlatformContext
import io.nicolaszurbuchen.yadlo.infra.database.DatabaseDriverFactory
import io.nicolaszurbuchen.yadlo.infra.image.CoilImageCache
import io.nicolaszurbuchen.yadlo.infra.image.ImageCache
import io.nicolaszurbuchen.yadlo.infra.notification.Notifier
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * The iOS twin of the Android module of the same name: the bindings whose constructors differ by
 * platform. The two factories take nothing here, where Android's take a `Context`, which is the
 * entire reason neither can be declared in common code.
 *
 * The image cache is the same class on both sides and differs only in what a `PlatformContext` is.
 * On iOS it is an object with nothing in it, so it is named directly rather than resolved — there
 * is no graph entry for it and inventing one would be a binding whose only purpose is to hand back
 * a constant.
 */
val platformModule =
    module {
        singleOf(::DatabaseDriverFactory)
        singleOf(::Notifier)
        single<ImageCache> { CoilImageCache(PlatformContext.INSTANCE) }
    }
