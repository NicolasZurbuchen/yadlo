package io.nicolaszurbuchen.yadlo.feature.home.di

import io.nicolaszurbuchen.yadlo.feature.home.domain.usecase.ObserveHomeContentUseCase
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.HomeStoreFactory
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.HomeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule =
    module {
        factoryOf(::ObserveHomeContentUseCase)

        factoryOf(::HomeStoreFactory)
        viewModelOf(::HomeViewModel)
    }
