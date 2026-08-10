package io.nicolaszurbuchen.yadlo

import android.app.Application
import io.nicolaszurbuchen.yadlo.app.di.initKoin
import io.nicolaszurbuchen.yadlo.infra.di.platformModule
import org.koin.android.ext.koin.androidContext

class YadloApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            additionalModules = listOf(platformModule),
            appDeclaration = {
                androidContext(this@YadloApplication)
            },
        )
    }
}
