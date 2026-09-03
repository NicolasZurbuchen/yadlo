package io.nicolaszurbuchen.yadlo.core.content.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import io.nicolaszurbuchen.yadlo.infra.navigation.LocalSharedTransitionScope
import org.jetbrains.compose.resources.painterResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.img_placeholder

/**
 * A Happening's photograph, wherever one is drawn — a card, a grid, the head of a fiche.
 *
 * **The bundled site photograph stands in for a null url and a failed load alike**, and the caller
 * does not get to choose (DECISIONS.md § The fiche has one ground). A caller that could choose is a
 * caller that eventually differs from the one beside it, which is how the same picture ended up
 * described three times in three files.
 *
 * **It sets no content description.** Every site draws the name directly beside the picture, so one
 * here would say it twice.
 */
@Composable
fun ContentImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    sharedKey: String? = null,
) {
    val placeholder = painterResource(Res.drawable.img_placeholder)

    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        fallback = placeholder,
        error = placeholder,
        modifier = modifier.sharedPicture(sharedKey),
    )
}

/**
 * Joins this picture to the one carrying the same [key] on the screen being opened or left.
 *
 * **Both scopes or neither.** The navigation scope throws rather than returning null when it is
 * read outside a `NavDisplay`, and every preview in the app draws these cards outside one — so
 * the transition scope, which *is* nullable, is what the branch is taken on. They are provided
 * together and only together, one wrapping the other in `NavGraph`.
 */
@Composable
private fun Modifier.sharedPicture(key: String?): Modifier {
    val transition = LocalSharedTransitionScope.current

    if (key == null || transition == null) return this

    return with(transition) {
        sharedElement(
            sharedContentState = rememberSharedContentState(key = key),
            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
        )
    }
}

/**
 * Three by two, wherever a content photograph is framed rather than filled.
 *
 * Every photograph in the bank is four by three, so any wider frame is a centre crop that throws
 * away the top and bottom of the picture — at 16:9 that is a quarter of the height, which on the one
 * portrait among the eight takes the top of the subject's head off. 3:2 keeps all but a tenth and
 * still reads as a banner rather than as a photograph shown whole.
 */
const val CONTENT_IMAGE_RATIO = 3f / 2f
