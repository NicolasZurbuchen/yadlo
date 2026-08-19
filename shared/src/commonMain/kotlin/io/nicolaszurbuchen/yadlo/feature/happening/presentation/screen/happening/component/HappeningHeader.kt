package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.categoryColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * The head of the fiche: the Category written out, then the name, over the photograph when there is
 * one.
 *
 * **Two grounds, one layout.** With a photograph it is a hero image bled to all three edges, running
 * under the status bar and the toolbar, with the words at the bottom of it. Without one it is the
 * Category colour as a radial blob anchored bottom-right — the treatment that shipped while the
 * content had no images at all, and still the right answer for the twenty-two Happenings that have
 * none. The words sit in the same place either way, so a fiche with a photo and a fiche without do
 * not read as two different screens.
 *
 * **A rule in the Category's colour closes the hero, and only the hero.** Sitting still at the top
 * of a fiche, a photograph says nothing about which Category it belongs to: the toolbar is
 * transparent, the label is written in the scrim's ink, and the colour does not arrive until
 * something is scrolled. The rule is the one place the Category is stated at rest. The blob variant
 * needs no such thing — it is already the colour — and capping its soft falloff with a hard edge
 * would fight the only idea it has.
 *
 * **[tint] closes the Category's own colour over the whole head**, photograph and blob alike, and
 * **it is the only thing painting that colour while it is translucent.** The toolbar over it stays
 * clear until this veil is already solid.
 *
 * That is not a preference, it is what alpha does. Two layers of one colour at `t` composite to
 * `1 - (1 - t)²`, not to `t` — at `t = 0.5` the overlap reads 0.75 — so a bar tinted alongside the
 * veil drew its own outline across the picture: the same hue, visibly heavier for exactly the height
 * of the toolbar. Nothing about the two ramps could fix that while both were between 0 and 1 at the
 * same time over the same pixels. One layer paints, then the other takes over once there is nothing
 * left to see through.
 *
 * The veil covers the blob variant too, which it did not have to while the toolbar tinted itself:
 * with the bar now clear until the end, the header is the only thing that can carry the colour on a
 * fiche that has no photograph.
 *
 * **The words are under the veil rather than on it.** Drawn on top they outlived the thing they were
 * captioning — the head went solid Category colour and the title stayed sitting on it, at the same
 * moment the toolbar was fading in a second copy of the same word, so the fiche showed its title
 * twice in two sizes. What closes over a photograph has to close over what is written on it.
 *
 * **Over a photograph both lines are [io.nicolaszurbuchen.yadlo.app.design.theme.AppColors.onScrim],
 * including the Category label.** The scrim's alpha was derived as the lowest at which white clears
 * 4.5:1 over a *white* photograph, which is the worst case an image can present; a Category fill has
 * no such guarantee, because the fills were measured against the app's own grounds and never against
 * a photograph nobody has seen. Colour is not lost as a carrier — the Category is written out in
 * words, it closes over the image on scroll, and the rule states it at rest.
 *
 * The Category colour is also what fills the frame until the image arrives, so a fiche opened with
 * no signal is the same colour as the bar it collapses into rather than a grey rectangle.
 */
@Composable
fun HappeningHeader(
    imageUrl: String?,
    categoryId: String,
    categoryLabel: String,
    title: String,
    tint: Float,
    modifier: Modifier = Modifier,
) {
    val category = MaterialTheme.categoryColors.forId(categoryId)
    val scrim = MaterialTheme.appColors.scrim

    Box(
        contentAlignment = Alignment.BottomStart,
        modifier =
            if (imageUrl != null) {
                modifier.fillMaxWidth().height(HERO_HEIGHT).background(category.fill)
            } else {
                modifier
                    .fillMaxWidth()
                    .heightIn(min = BLOB_HEADER_MIN_HEIGHT)
                    .drawBehind {
                        val center = Offset(x = size.width * BLOB_CENTER_X, y = size.height * BLOB_CENTER_Y)
                        val radius = size.maxDimension * BLOB_RADIUS

                        drawCircle(
                            brush =
                                Brush.radialGradient(
                                    colors = listOf(category.fill.copy(alpha = BLOB_ALPHA), Color.Transparent),
                                    center = center,
                                    radius = radius,
                                ),
                            radius = radius,
                            center = center,
                        )
                    }
            },
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                // The photograph carries no information the words below it do not, and announcing
                // "photo de DJ ALF" on a screen whose title is DJ ALF says it twice.
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            // Dark at both ends and clear through the middle: the bottom stop is what the title
            // stands on, the top one is what the back arrow does, and the gap between them is the
            // part of the photograph that is actually worth showing.
            Box(
                modifier =
                    Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                0f to scrim,
                                SCRIM_CLEAR_FROM to Color.Transparent,
                                SCRIM_CLEAR_TO to Color.Transparent,
                                1f to scrim,
                            ),
                        ),
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier =
                Modifier.padding(
                    start = MaterialTheme.spacing.md,
                    top = MaterialTheme.spacing.lg,
                    end = MaterialTheme.spacing.md,
                    // Tighter than the top, so the name sits down on the rule rather than floating
                    // in the middle of the photograph's lower third.
                    bottom = MaterialTheme.spacing.md,
                ),
        ) {
            Text(
                text = categoryLabel,
                style = MaterialTheme.typography.displaySmall,
                color = if (imageUrl != null) MaterialTheme.appColors.onScrim else category.fill,
            )

            Text(
                text = title,
                // The screen-title size rather than the toolbar's: at rest this *is* the screen's
                // title, and the 22sp the bar carries it at looked like a caption on a photograph.
                style = MaterialTheme.typography.headlineLarge,
                color = if (imageUrl != null) MaterialTheme.appColors.onScrim else MaterialTheme.appColors.textPrimary,
            )
        }

        // Last, so it closes over the words as well as over the picture. matchParentSize rather than
        // fillMaxSize: the blob variant is only as tall as its own title, and a child that fills
        // would be measured against a list item's unbounded height.
        Box(modifier = Modifier.matchParentSize().background(category.fill.copy(alpha = tint)))

        if (imageUrl != null) {
            // Above the veil rather than under it, so it stays the same colour at every point of
            // the collapse instead of being painted over by its own hue.
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(CATEGORY_RULE_HEIGHT)
                        .background(category.fill),
            )
        }
    }
}

/**
 * Roughly a third of a phone's height, which is what the prototype gives it: enough for a face to
 * survive a centre crop, little enough that the first paragraph is on screen without scrolling.
 */
private val HERO_HEIGHT = 280.dp

/**
 * Taller than the hero needs to be, because this one has no photograph to be interesting and its
 * words still have to clear a toolbar and a status bar that are drawn on top of it.
 */
private val BLOB_HEADER_MIN_HEIGHT = 200.dp

/**
 * Thicker than a divider and thinner than a band. A hairline of `eau` blue against a photograph of
 * the lake is not a statement of anything; three points reads as deliberate at arm's length in
 * sunlight, which is the only place it has to work.
 */
private val CATEGORY_RULE_HEIGHT = 3.dp

/** The clear band, as fractions of the header's height. Wide enough to be the photograph's subject. */
private const val SCRIM_CLEAR_FROM = 0.3f
private const val SCRIM_CLEAR_TO = 0.5f

private const val BLOB_CENTER_X = 0.92f
private const val BLOB_CENTER_Y = 1.05f

/** Wider than the header itself, so what is on screen is the inner part of the falloff. */
private const val BLOB_RADIUS = 0.9f

/** Enough to colour the corner, low enough to keep the title legible if the blob reaches it. */
private const val BLOB_ALPHA = 0.55f
