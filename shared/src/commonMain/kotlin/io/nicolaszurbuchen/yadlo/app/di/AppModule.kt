package io.nicolaszurbuchen.yadlo.app.di

import io.nicolaszurbuchen.yadlo.app.navigation.appNavigationModule
import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.di.pokemonExplorerModule
import io.nicolaszurbuchen.yadlo.infra.database.databaseModule
import io.nicolaszurbuchen.yadlo.infra.mvi.storeModule
import io.nicolaszurbuchen.yadlo.infra.navigation.infraNavigationModule
import io.nicolaszurbuchen.yadlo.infra.network.networkModule

val appModule =
    listOf(
        appNavigationModule,
        databaseModule,
        infraNavigationModule,
        networkModule,
        storeModule,
        pokemonExplorerModule,
    )
