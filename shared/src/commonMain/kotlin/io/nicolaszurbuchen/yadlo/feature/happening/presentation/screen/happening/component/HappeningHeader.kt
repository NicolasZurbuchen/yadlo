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
 * **Over a photograph both lines are [io.nicolaszurbuchen.yadlo.app.design.theme.AppColors.onScrim],
 * including the Category label.** The scrim's alpha was derived as the lowest at which white clears
 * 4.5:1 over a *white* photograph, which is the worst case an image can present; a Category fill has
 * no such guarantee, because the fills were measured against the app's own grounds and never against
 * a photograph nobody has seen. Colour is not lost as a carrier — the Category is written out in
 * words, and the toolbar above takes its fill as the header scrolls away.
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
                        .fillMaxSize()
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
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.lg),
        ) {
            Text(
                text = categoryLabel,
                style = MaterialTheme.typography.displaySmall,
                color = if (imageUrl != null) MaterialTheme.appColors.onScrim else category.fill,
            )

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = if (imageUrl != null) MaterialTheme.appColors.onScrim else MaterialTheme.appColors.textPrimary,
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

/** The clear band, as fractions of the header's height. Wide enough to be the photograph's subject. */
private const val SCRIM_CLEAR_FROM = 0.3f
private const val SCRIM_CLEAR_TO = 0.5f

private const val BLOB_CENTER_X = 0.92f
private const val BLOB_CENTER_Y = 1.05f

/** Wider than the header itself, so what is on screen is the inner part of the falloff. */
private const val BLOB_RADIUS = 0.9f

/** Enough to colour the corner, low enough to keep the title legible if the blob reaches it. */
private const val BLOB_ALPHA = 0.55f
