package io.nicolaszurbuchen.yadlo.app.di

import io.nicolaszurbuchen.yadlo.app.navigation.appNavigationModule
import io.nicolaszurbuchen.yadlo.common.content.di.contentModule
import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.di.pokemonExplorerModule
import io.nicolaszurbuchen.yadlo.infra.database.databaseModule
import io.nicolaszurbuchen.yadlo.infra.mvi.storeModule
import io.nicolaszurbuchen.yadlo.infra.navigation.infraNavigationModule
import io.nicolaszurbuchen.yadlo.infra.network.networkModule
import io.nicolaszurbuchen.yadlo.infra.time.timeModule

val appModule =
    listOf(
        appNavigationModule,
        contentModule,
        databaseModule,
        infraNavigationModule,
        networkModule,
        storeModule,
        timeModule,
        pokemonExplorerModule,
    )
