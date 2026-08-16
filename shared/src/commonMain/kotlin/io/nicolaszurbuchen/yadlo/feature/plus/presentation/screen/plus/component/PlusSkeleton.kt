package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * What the tab looks like while the bundle is still landing.
 *
 * **The shape of the answer, not a spinner in the middle of it.** A centred `CircularProgressIndicator`
 * throws away everything already known about this screen — that it is cards of rows, that the first
 * card is the long one, that a header sits above each — and replaces it with a symbol that could be
 * any screen in any app. Drawing the frame instead means the real rows arrive *into* a layout the
 * eye has already settled on, rather than replacing one.
 *
 * The counts are deliberately the published shape of the tab rather than a round number, so the
 * skeleton does not visibly resize the moment the content lands.
 *
 * It fades rather than sweeping a gradient across the row. A travelling highlight needs a brush
 * animated per-frame across every placeholder, which on the slowest device this app supports is
 * work spent on the one screen that is by definition waiting for something else. One alpha, shared
 * by every block, animates a single value.
 */
@Composable
fun PlusSkeleton(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "plusSkeleton")
    val alpha by transition.animateFloat(
        initialValue = MIN_ALPHA,
        targetValue = MAX_ALPHA,
        animationSpec = infiniteRepeatable(animation = tween(PULSE_MILLIS), repeatMode = RepeatMode.Reverse),
        label = "plusSkeletonAlpha",
    )
    val blockColor = MaterialTheme.appColors.textTertiary.copy(alpha = alpha)

    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        modifier = modifier.fillMaxWidth(),
    ) {
        GROUP_ROW_COUNTS.forEach { rowCount ->
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                // The group header, inset from the card exactly as PlusCard insets the real one.
                Block(
                    color = blockColor,
                    width = HEADER_WIDTH,
                    height = HEADER_HEIGHT,
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.sm),
                )

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.appColors.surface),
                ) {
                    repeat(rowCount) { index ->
                        if (index > 0) {
                            HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
                        }

                        SkeletonRow(color = blockColor)
                    }
                }
            }
        }
    }
}

/** The real row's geometry with its words taken out — same height, same icon slot, same insets. */
@Composable
private fun SkeletonRow(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = ROW_MIN_HEIGHT)
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
    ) {
        Spacer(
            modifier =
                Modifier
                    .size(LEADING_ICON_SIZE)
                    .clip(RoundedCornerShape(BLOCK_CORNER))
                    .background(color),
        )

        Block(color = color, width = LABEL_WIDTH, height = LABEL_HEIGHT)
    }
}

@Composable
private fun Block(
    color: Color,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier =
            modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(BLOCK_CORNER))
                .background(color),
    )
}

// Sur place is the long card and the eye lands on it first, so the placeholder has to be long too;
// the three below it are the published sizes of Le festival, S'impliquer and L'application.
private val GROUP_ROW_COUNTS = listOf(8, 3, 3, 3)

// Never fully transparent and never fully opaque: at 0 the blocks disappear and the layout reads as
// empty rather than as loading, and at 1 a tertiary-coloured bar is mistakable for real text.
private const val MIN_ALPHA = 0.10f
private const val MAX_ALPHA = 0.28f

// Slower than the 700ms the fiche's list uses. That one covers a network read; this one covers a
// cache read that is usually over in a frame or two, and a fast pulse on a placeholder nobody sees
// for long only reads as flicker.
private const val PULSE_MILLIS = 1000

private val BLOCK_CORNER = 4.dp
private val HEADER_WIDTH = 96.dp
private val HEADER_HEIGHT = 12.dp
private val LABEL_WIDTH = 160.dp
private val LABEL_HEIGHT = 16.dp

// Matched to PlusRow, which is the whole point of drawing this instead of a spinner.
private val LEADING_ICON_SIZE = 20.dp
private val ROW_MIN_HEIGHT = 64.dp
