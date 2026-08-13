package io.nicolaszurbuchen.yadlo.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.ktor.client.HttpClient
import io.nicolaszurbuchen.yadlo.app.content.ContentUnavailableScreen
import io.nicolaszurbuchen.yadlo.app.design.theme.YadloTheme
import io.nicolaszurbuchen.yadlo.app.navigation.MainScaffold
import io.nicolaszurbuchen.yadlo.app.splash.SplashScreen
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.common.error.toUiModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun App() {
    val httpClient = koinInject<HttpClient>()
    val contentRepository = koinInject<ContentRepository>()
    remember(Unit) {
        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .components { add(KtorNetworkFetcherFactory(httpClient = { httpClient })) }
                .build()
        }
    }

    val status by contentRepository.observeStatus().collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { contentRepository.refresh() }

    // A gate rather than a destination: nothing navigates to the splash and nothing returns to it,
    // so it sits above MainScaffold's four tab stacks instead of inside them. Putting it on a back
    // stack would mean a root NavDisplay above the tab shell, and AppNavigator.attach and the root
    // BackHandler both assume the top level is a tab. Revisit if a second pre-app screen appears.
    var minimumSplashElapsed by rememberSaveable { mutableStateOf(false) }

    YadloTheme {
        val currentStatus = status

        when {
            // The splash is a floor and a cover at once: it holds for its own minimum, then keeps
            // holding while the first fetch is still out. That is what keeps a spinner off a blank
            // screen, and it is why the minimum was written as a floor rather than a duration.
            !minimumSplashElapsed || currentStatus is ContentStatus.Loading -> {
                SplashScreen(onFinish = { minimumSplashElapsed = true })
            }

            currentStatus is ContentStatus.Unavailable -> {
                ContentUnavailableScreen(
                    error = currentStatus.error.toUiModel(),
                    onRetry = { scope.launch { contentRepository.refresh() } },
                )
            }

            else -> {
                MainScaffold()
            }
        }
    }
}
