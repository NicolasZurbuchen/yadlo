package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.ShimmerPulse
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.shimmerBlock
import io.nicolaszurbuchen.yadlo.app.design.theme.sizing
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.app.navigation.LocalTabChromeInsets

/**
 * The timetable while the first bundle is landing.
 *
 * *Reversed: this was a centred spinner.* This tab is the one where a spinner cost the most, because
 * its chrome is two rows of chips and an axis before a single Slot is drawn — a shape nothing else
 * in the app has, replaced by a rotating circle that could be any screen anywhere.
 *
 * **The chrome is drawn on its own blue ground, which is the half a spinner could not fake.** The
 * band arrives before the rows do and stays put once they are there, so drawing it here means the
 * only thing that changes when the content lands is inside the list.
 *
 * The rows are one Slot's geometry repeated: the Category square, the name, the time under it, and
 * the bar out to where the chevron column starts. Widths vary because Happening names do — every
 * row the same length reads as a loading graphic rather than as a timetable about to appear.
 */
@Composable
fun ProgrammeSkeleton(modifier: Modifier = Modifier) {
    val chrome = LocalTabChromeInsets.current

    ShimmerPulse {
        Column(modifier = modifier.fillMaxSize().padding(top = chrome.top)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.appColors.primarySubtle)
                        .padding(
                            horizontal = MaterialTheme.spacing.md,
                            vertical = MaterialTheme.spacing.sm,
                        ),
            ) {
                ChipRow(widths = SCOPE_CHIP_WIDTHS)

                ChipRow(widths = CATEGORY_CHIP_WIDTHS)

                // The axis, which is three readings spread across the width the bars will occupy.
                Spacer(modifier = Modifier.fillMaxWidth().height(SCALE_HEIGHT).shimmerBlock())
            }

            NAME_WIDTHS.forEachIndexed { index, nameWidth ->
                if (index > 0) {
                    HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
                }

                SlotRowSilhouette(nameWidth = nameWidth)
            }
        }
    }
}

@Composable
private fun ChipRow(widths: List<Int>) {
    Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
        widths.forEach { width ->
            Spacer(
                modifier =
                    Modifier
                        .width(width.dp)
                        .height(CHIP_HEIGHT)
                        .shimmerBlock(RoundedCornerShape(CHIP_HEIGHT / 2)),
            )
        }
    }
}

@Composable
private fun SlotRowSilhouette(nameWidth: Float) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.md, vertical = ROW_VERTICAL_PADDING),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.size(MaterialTheme.sizing.categoryMark).shimmerBlock())

            Spacer(modifier = Modifier.fillMaxWidth(nameWidth).height(NAME_HEIGHT).shimmerBlock())
        }

        Spacer(modifier = Modifier.width(TIME_WIDTH).height(TIME_HEIGHT).shimmerBlock())

        Spacer(modifier = Modifier.fillMaxWidth(BAR_WIDTH).height(BAR_HEIGHT).shimmerBlock())
    }
}

/** *Découvrir · Tous · Ven · Sam · Dim* — five, and the first two are the long ones. */
private val SCOPE_CHIP_WIDTHS = listOf(84, 56, 44, 46, 44)

/** *Tout* and the five Categories the 2026 content publishes, cut off by the row's own scroll. */
private val CATEGORY_CHIP_WIDTHS = listOf(52, 76, 88, 68, 64)

/** Six rows fills a phone, so the list does not visibly grow when the real ones land. */
private val NAME_WIDTHS = listOf(0.58f, 0.74f, 0.45f, 0.66f, 0.8f, 0.52f)

// SlotRow's own padding and mark, so nothing shifts vertically when a row replaces its silhouette.
private val ROW_VERTICAL_PADDING = 12.dp

private val CHIP_HEIGHT = 32.dp
private val SCALE_HEIGHT = 10.dp
private val NAME_HEIGHT = 16.dp
private val TIME_HEIGHT = 12.dp
private val TIME_WIDTH = 96.dp

/** The bar stops where the chevron column starts, which is what the real row does. */
private const val BAR_WIDTH = 0.88f
private val BAR_HEIGHT = 6.dp
