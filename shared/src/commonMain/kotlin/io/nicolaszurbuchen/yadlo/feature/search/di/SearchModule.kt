package io.nicolaszurbuchen.yadlo.feature.search.di

import io.nicolaszurbuchen.yadlo.feature.search.domain.usecase.MatchSearchQueryUseCase
import io.nicolaszurbuchen.yadlo.feature.search.domain.usecase.ObserveSearchIndexUseCase
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.SearchStoreFactory
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.SearchViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val searchModule =
    module {
        factoryOf(::ObserveSearchIndexUseCase)
        factoryOf(::MatchSearchQueryUseCase)

        factoryOf(::SearchStoreFactory)
        viewModelOf(::SearchViewModel)
    }
