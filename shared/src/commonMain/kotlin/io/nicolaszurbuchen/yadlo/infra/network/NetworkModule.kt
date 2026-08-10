package io.nicolaszurbuchen.yadlo.infra.network

import org.koin.dsl.module

val networkModule =
    module {
        single { createHttpClient() }
    }
