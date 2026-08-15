package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.categoryColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * The head of the fiche: the Category written out, then the name.
 *
 * **The photograph is deliberately absent.** DECISIONS.md describes this as a collapsing toolbar
 * over a hero image, and it will be — but not one Happening in the 2026 content carries an image and
 * `imageBaseUrl` is still null (content/GAPS.md § Images). Building the image layer now would mean
 * an untested remote path drawing nothing on every fiche in the app, so what ships is the half that
 * has something to draw: the Category colour as a radial blob anchored bottom-right, which was
 * always specified as sitting *over* the photo rather than being derived from it. The photo slides
 * in behind this when the association's press images are hosted.
 */
@Composable
fun HappeningHeader(
    categoryId: String,
    categoryLabel: String,
    title: String,
    modifier: Modifier = Modifier,
) {
    val category = MaterialTheme.categoryColors.forId(categoryId)

    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs, Alignment.Bottom),
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = HEADER_MIN_HEIGHT)
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
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.lg),
    ) {
        Text(
            text = categoryLabel,
            style = MaterialTheme.typography.displaySmall,
            color = category.fill,
        )

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.appColors.textPrimary,
        )
    }
}

/** Tall enough that the blob has room to fall off the bottom-right corner rather than sit in it. */
private val HEADER_MIN_HEIGHT = 160.dp

private const val BLOB_CENTER_X = 0.92f
private const val BLOB_CENTER_Y = 1.05f

/** Wider than the header itself, so what is on screen is the inner part of the falloff. */
private const val BLOB_RADIUS = 0.9f

/** Enough to colour the corner, low enough to keep the title legible if the blob reaches it. */
private const val BLOB_ALPHA = 0.55f
