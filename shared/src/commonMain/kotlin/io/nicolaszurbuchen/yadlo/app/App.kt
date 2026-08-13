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

    // A gate rather than a destination: nothing navigates to the splash and nothing returns to it,
    // so it sits above MainScaffold's four tab stacks instead of inside them. Putting it on a back
    // stack would mean a root NavDisplay above the tab shell, and AppNavigator.attach and the root
    // BackHandler both assume the top level is a tab. Revisit if a second pre-app screen appears.
    var splashShown by rememberSaveable { mutableStateOf(false) }

    YadloTheme {
        if (splashShown) {
            MainScaffold()
        } else {
            SplashScreen(onFinish = { splashShown = true })
        }
    }
}
