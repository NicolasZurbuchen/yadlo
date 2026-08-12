package io.nicolaszurbuchen.yadlo.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.ktor.client.HttpClient
import io.nicolaszurbuchen.yadlo.app.design.theme.YadloTheme
import io.nicolaszurbuchen.yadlo.app.navigation.MainScaffold
import io.nicolaszurbuchen.yadlo.app.splash.SplashScreen
import org.koin.compose.koinInject

@Composable
fun App() {
    val httpClient = koinInject<HttpClient>()
    remember(Unit) {
        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .components { add(KtorNetworkFetcherFactory(httpClient = { httpClient })) }
                .build()
        }
    }

    // rememberSaveable rather than remember: a configuration change must not replay the splash. The
    // gate is a plain flag today and becomes "the content bundle is ready" once loading exists —
    // which is why SplashScreen owns a *minimum* duration rather than the whole timing decision.
    var splashShown by rememberSaveable { mutableStateOf(false) }

    YadloTheme {
        if (splashShown) {
            MainScaffold()
        } else {
            SplashScreen(onFinish = { splashShown = true })
        }
    }
}
