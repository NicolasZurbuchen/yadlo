package io.nicolaszurbuchen.yadlo.feature.programme.di

import io.nicolaszurbuchen.yadlo.feature.programme.domain.usecase.ObserveProgrammeContentUseCase
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.ProgrammeStoreFactory
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.ProgrammeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val programmeModule =
    module {
        factoryOf(::ObserveProgrammeContentUseCase)

        factoryOf(::ProgrammeStoreFactory)
        viewModelOf(::ProgrammeViewModel)
    }
