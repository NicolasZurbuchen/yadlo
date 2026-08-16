package io.nicolaszurbuchen.yadlo.app.di

import io.nicolaszurbuchen.yadlo.infra.platform.BuildFlags
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Neither [isDebugBuild] nor [appVersion] has a default. The first gates the time-travel panel, and
 * a default would be a default for whether a debug tool ships; the second is printed on *À propos*
 * for somebody to quote back in an email, and a default there would be a version number that is
 * quietly wrong on one of the two platforms. Both are decisions that should have to be written down
 * at each entry point.
 */
fun initKoin(
    isDebugBuild: Boolean,
    appVersion: String,
    additionalModules: List<Module> = emptyList(),
    appDeclaration: KoinAppDeclaration = {},
) = startKoin {
    appDeclaration()
    modules(appModule + buildFlagsModule(isDebugBuild, appVersion) + additionalModules)
}

private fun buildFlagsModule(
    isDebugBuild: Boolean,
    appVersion: String,
): Module =
    module {
        single { BuildFlags(isDebug = isDebugBuild, version = appVersion) }
    }
