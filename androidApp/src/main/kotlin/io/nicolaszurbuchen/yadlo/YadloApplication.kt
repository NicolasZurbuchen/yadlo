package io.nicolaszurbuchen.yadlo

import android.app.Application
import io.nicolaszurbuchen.yadlo.app.di.initKoin
import io.nicolaszurbuchen.yadlo.infra.di.platformModule
import org.koin.android.ext.koin.androidContext

class YadloApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            isDebugBuild = BuildConfig.DEBUG,
            appVersion = BuildConfig.VERSION_NAME,
            additionalModules = listOf(platformModule),
            appDeclaration = {
                androidContext(this@YadloApplication)
            },
        )
    }
}
