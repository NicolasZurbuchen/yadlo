package io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusRoute
import io.nicolaszurbuchen.yadlo.infra.navigation.NavKeyHandler

class PlusNavKeyHandler : NavKeyHandler {
    override fun EntryProviderScope<NavKey>.registerEntries() {
        entry<PlusDestination> { PlusRoute() }
    }
}
