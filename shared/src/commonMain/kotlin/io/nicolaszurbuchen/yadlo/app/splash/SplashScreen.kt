package io.nicolaszurbuchen.yadlo.app.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.img_bg_splash
import yadlo.shared.generated.resources.img_logo_morges
import yadlo.shared.generated.resources.img_logo_preverenges
import yadlo.shared.generated.resources.img_logo_yadlo
import yadlo.shared.generated.resources.splash_logo_description
import yadlo.shared.generated.resources.splash_supported_by
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.foundation.Image as ComposeImage

/**
 * Every image here is bundled rather than fetched: the splash draws before any fetch completes, and
 * a first launch has no cache to fall back on.
 */
@Composable
fun SplashScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // rememberUpdatedState keeps the effect on its Unit key, so a recomposition cannot restart the
    // delay by handing it a new lambda.
    val currentOnFinish by rememberUpdatedState(onFinish)

    LaunchedEffect(Unit) {
        delay(MINIMUM_DISPLAY_MILLIS.milliseconds)
        currentOnFinish()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        ComposeImage(
            painter = painterResource(Res.drawable.img_bg_splash),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.appColors.scrim),
        )

        ComposeImage(
            painter = painterResource(Res.drawable.img_logo_yadlo),
            contentDescription = stringResource(Res.string.splash_logo_description),
            contentScale = ContentScale.Fit,
            modifier =
                Modifier
                    .align(BiasAlignment(horizontalBias = 0f, verticalBias = WORDMARK_BIAS))
                    .padding(horizontal = MaterialTheme.spacing.xxl)
                    .width(WORDMARK_WIDTH),
        )

        SupportedBy(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .safeDrawingPadding()
                    .padding(vertical = MaterialTheme.spacing.xxxl),
        )
    }
}

@Composable
private fun SupportedBy(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        modifier = modifier,
    ) {
        Text(
            text = stringResource(Res.string.splash_supported_by),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.appColors.onScrim,
            textAlign = TextAlign.Center,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        ) {
            // The label above already names both backers; describing each logo too would have a
            // screen reader say "avec le soutien de, Morges Région, Morges Région".
            ComposeImage(
                painter = painterResource(Res.drawable.img_logo_preverenges),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(BACKER_LOGO_HEIGHT).clearAndSetSemantics { },
            )

            ComposeImage(
                painter = painterResource(Res.drawable.img_logo_morges),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(BACKER_LOGO_HEIGHT).clearAndSetSemantics { },
            )
        }
    }
}

/** A floor, not a duration: once content loading exists the splash stays until the bundle is ready. */
private const val MINIMUM_DISPLAY_MILLIS = 1_200L

private val WORDMARK_WIDTH = 260.dp

/** Bias runs -1 top to +1 bottom: negative lifts the wordmark above centre, as a share of height. */
private const val WORDMARK_BIAS = -0.55f

/** Matched on height because the two logos have very different aspect ratios. */
private val BACKER_LOGO_HEIGHT = 64.dp
