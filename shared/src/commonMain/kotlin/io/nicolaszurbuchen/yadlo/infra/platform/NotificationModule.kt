package io.nicolaszurbuchen.yadlo.infra.platform

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * The platform-independent half of notifications. [Notifier] itself is not here: it needs a
 * `Context` on Android and nothing on iOS, so it is bound in each platform's own module beside the
 * database driver.
 */
val notificationModule =
    module {
        singleOf(::NotificationTargetRelay)
    }
