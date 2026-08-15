package io.nicolaszurbuchen.yadlo.app.navigation

import io.nicolaszurbuchen.yadlo.app.navigation.impl.HappeningNavigatorImpl
import io.nicolaszurbuchen.yadlo.app.navigation.impl.HomeNavigatorImpl
import io.nicolaszurbuchen.yadlo.app.navigation.impl.MonYadloNavigatorImpl
import io.nicolaszurbuchen.yadlo.app.navigation.impl.PokemonExplorerNavigatorImpl
import io.nicolaszurbuchen.yadlo.app.navigation.impl.ProgrammeNavigatorImpl
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation.HappeningNavKeyHandler
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation.HappeningNavigator
import io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation.HomeNavKeyHandler
import io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation.HomeNavigator
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.navigation.MonYadloNavKeyHandler
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.navigation.MonYadloNavigator
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PlusNavKeyHandler
import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.presentation.navigation.PokemonExplorerNavKeyHandler
import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.presentation.navigation.PokemonExplorerNavigator
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.navigation.ProgrammeNavKeyHandler
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.navigation.ProgrammeNavigator
import io.nicolaszurbuchen.yadlo.infra.navigation.NavKeyHandler
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val appNavigationModule =
    module {
        single { TabNavigator() }

        singleOf(::HomeNavigatorImpl) bind HomeNavigator::class
        singleOf(::ProgrammeNavigatorImpl) bind ProgrammeNavigator::class
        singleOf(::MonYadloNavigatorImpl) bind MonYadloNavigator::class
        singleOf(::HappeningNavigatorImpl) bind HappeningNavigator::class
        singleOf(::PokemonExplorerNavigatorImpl) bind PokemonExplorerNavigator::class

        // Every handler is registered against the same NavKeyHandler type and resolved with
        // getAll, so a feature becomes reachable by adding a line here and nowhere else.
        singleOf(::HomeNavKeyHandler) { named("home") } bind NavKeyHandler::class
        singleOf(::ProgrammeNavKeyHandler) { named("programme") } bind NavKeyHandler::class
        singleOf(::MonYadloNavKeyHandler) { named("monYadlo") } bind NavKeyHandler::class
        singleOf(::PlusNavKeyHandler) { named("plus") } bind NavKeyHandler::class
        singleOf(::HappeningNavKeyHandler) { named("happening") } bind NavKeyHandler::class

        singleOf(::PokemonExplorerNavKeyHandler) { named("pokemonExplorer") } bind NavKeyHandler::class
    }
