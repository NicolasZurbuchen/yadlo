package io.nicolaszurbuchen.yadlo.app.di

import io.nicolaszurbuchen.yadlo.infra.platform.BuildFlags
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * [isDebugBuild] has no default. It gates the time-travel panel, and a default would be a default
 * for whether a debug tool ships — which is exactly the decision that should have to be written
 * down at each platform's entry point.
 */
fun initKoin(
    isDebugBuild: Boolean,
    additionalModules: List<Module> = emptyList(),
    appDeclaration: KoinAppDeclaration = {},
) = startKoin {
    appDeclaration()
    modules(appModule + buildFlagsModule(isDebugBuild) + additionalModules)
}

private fun buildFlagsModule(isDebugBuild: Boolean): Module =
    module {
        single { BuildFlags(isDebug = isDebugBuild) }
    }
