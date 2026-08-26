package io.nicolaszurbuchen.yadlo.feature.monyadlo.di

import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.usecase.ObserveMonYadloContentUseCase
import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.usecase.ObserveWishlistUseCase
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.MonYadloStoreFactory
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.MonYadloViewModel
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist.WishlistStoreFactory
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist.WishlistViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val monYadloModule =
    module {
        factoryOf(::ObserveMonYadloContentUseCase)
        factoryOf(::ObserveWishlistUseCase)

        factoryOf(::MonYadloStoreFactory)
        viewModelOf(::MonYadloViewModel)

        factoryOf(::WishlistStoreFactory)
        viewModelOf(::WishlistViewModel)
    }
