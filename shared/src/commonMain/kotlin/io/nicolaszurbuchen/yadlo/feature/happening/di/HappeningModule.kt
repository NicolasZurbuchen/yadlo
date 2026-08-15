package io.nicolaszurbuchen.yadlo.feature.happening.di

import io.nicolaszurbuchen.yadlo.feature.happening.domain.usecase.ObserveHappeningDetailUseCase
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningStoreFactory
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val happeningModule =
    module {
        factoryOf(::ObserveHappeningDetailUseCase)

        // Parameterised rather than declared with viewModelOf: which Happening the fiche is about
        // arrives from the NavKey, so the id is a construction parameter rather than a dependency.
        viewModel { (happeningId: String) ->
            HappeningViewModel(HappeningStoreFactory(get(), get(), get(), get(), happeningId))
        }
    }
