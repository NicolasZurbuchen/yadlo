package io.nicolaszurbuchen.yadlo.app.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.foundation.Image as ComposeImage

/**
 * The brand moment, and the only screen whose every image is bundled in the app rather than fetched.
 *
 * A splash draws before any fetch has completed — that is its whole job — so reading the backer
 * logos from the content bundle would mean either waiting on the network or drawing incomplete on a
 * first launch, which is the one launch with no cache to fall back on.
 *
 * Everything sits on the photograph under the scrim, which is the same treatment a fiche gives its
 * hero image and exists for the same reason: it turns an image nobody has vetted into a predictable
 * ground.
 */
@Composable
fun SplashScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // rememberUpdatedState so the effect keeps its Unit key and still calls the current lambda: the
    // delay must not restart because a recomposition handed us a new one.
    val currentOnFinish by rememberUpdatedState(onFinish)

    LaunchedEffect(Unit) {
        delay(MINIMUM_DISPLAY_MILLIS)
        currentOnFinish()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        ComposeImage(
            painter = painterResource(Res.drawable.img_bg_splash),
            contentDescription = null,
            // Crop rather than Fit: the photograph is the ground, and letterboxing it would leave
            // bars that no colour in the theme is the right answer for.
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        // The same scrim the fiche uses over its hero photo, and for the same reason: it turns an
        // image into a predictable ground so the white wordmark is legible whatever the photo does.
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
                    .align(BiasAlignment(horizontalBias = CENTRED, verticalBias = WORDMARK_BIAS))
                    .padding(horizontal = MaterialTheme.spacing.xxl)
                    .width(WORDMARK_WIDTH),
        )

        SupportedBy(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .safeDrawingPadding()
                    .padding(MaterialTheme.spacing.lg),
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
            // The label above names both backers, so repeating each one as a content description
            // would have a screen reader say "avec le soutien de, Morges Région, Morges Région".
            ComposeImage(
                painter = painterResource(Res.drawable.img_logo_morges),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(BACKER_LOGO_HEIGHT).clearAndSetSemantics { },
            )

            ComposeImage(
                painter = painterResource(Res.drawable.img_logo_preverenges),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(BACKER_LOGO_HEIGHT).clearAndSetSemantics { },
            )
        }
    }
}

/**
 * Long enough that the wordmark registers as a brand moment rather than a flash of blue, short
 * enough that it never feels like the app is stuck. This is a *minimum*: once content loading exists
 * the splash stays until the bundle is ready and this becomes the floor rather than the duration.
 */
private const val MINIMUM_DISPLAY_MILLIS = 1_200L

/** Sized to the wordmark rather than the screen, so it does not balloon on a tablet. */
private val WORDMARK_WIDTH = 260.dp

/**
 * BiasAlignment runs -1 at the top through 0 at the centre to +1 at the bottom, so -0.5 lifts the
 * wordmark by a quarter of the screen height. Expressed as a bias rather than a fixed offset so it
 * lifts by the same proportion on every screen size instead of drifting on a tablet.
 */
private const val WORDMARK_BIAS = -0.5f
private const val CENTRED = 0f

/**
 * Height rather than width, because the two logos have very different aspect ratios — Morges is
 * nearly square, Préverenges is wide — and matching their widths would make one tower over the other.
 */
private val BACKER_LOGO_HEIGHT = 64.dp
