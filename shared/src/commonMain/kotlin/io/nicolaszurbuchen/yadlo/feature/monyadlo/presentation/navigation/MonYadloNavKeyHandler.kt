package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.MonYadloRoute
import io.nicolaszurbuchen.yadlo.infra.navigation.NavKeyHandler

class MonYadloNavKeyHandler : NavKeyHandler {
    override fun EntryProviderScope<NavKey>.registerEntries() {
        entry<MonYadloDestination> { MonYadloRoute() }
    }
}
