package io.nicolaszurbuchen.yadlo.infra.navigation

import org.koin.dsl.module

val infraNavigationModule =
    module {
        single { AppNavigator() }
    }
