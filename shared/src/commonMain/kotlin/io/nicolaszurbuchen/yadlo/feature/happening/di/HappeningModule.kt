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

        factoryOf(::HappeningStoreFactory)

        // Parameterised rather than declared with viewModelOf: which Happening the fiche is about
        // arrives from the NavKey, so the id is passed at resolution rather than resolved. The
        // factory above is an ordinary binding, because the id reaches it through `create()` — it
        // used to be built by hand here, which put its dependencies beyond `AppModuleTest`.
        viewModel { (happeningId: String) ->
            HappeningViewModel(get(), happeningId)
        }
    }
