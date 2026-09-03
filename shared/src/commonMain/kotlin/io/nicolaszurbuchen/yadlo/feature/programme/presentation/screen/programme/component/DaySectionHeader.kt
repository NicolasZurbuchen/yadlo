package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.core.content.presentation.component.SlotScaleRow
import io.nicolaszurbuchen.yadlo.design.theme.WAVE_DEPTH
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.design.theme.waveEdgeBackground
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.DaySectionHeaderUiModel

/**
 * *Samedi*, and the span the bars under it are drawn against — pinned while that day is on screen.
 *
 * Only under *Tous*. One day needs no header, because the chip that chose it is directly above and
 * a header would say the same word twice.
 *
 * **It wears the chrome's blue, and it is the reason the axis can travel at all.** A span is a fact
 * about one day — Friday runs 16:00–02:00 and Sunday 12:00–22:00 — so a single reading in the
 * toolbar could only be right about one of the three. Sticking the header keeps the reading on
 * screen belonging to the rows on screen. The blue is what makes that read as the chrome briefly
 * growing a line rather than as a card wedged into the list, and it is also what keeps
 * [SlotScaleRow] legible: its three readings are drawn in the ink that blue carries, because no dim
 * role clears 4.5:1 on it.
 *
 * **The ground is opaque, and that is load-bearing rather than decorative.** A sticky header is
 * drawn over the rows scrolling beneath it; a transparent one would let them through and make both
 * unreadable for the length of the scroll.
 *
 * The scale is inset by the same numbers the rows are built from — the row's own left edge, and the
 * right edge of the bar where the chevron column starts — because [SlotScaleRow] carries no inset
 * of its own and a reading offset from the axis it labels answers the question wrongly rather than
 * failing to answer it.
 */
@Composable
fun DaySectionHeader(
    header: DaySectionHeaderUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        modifier =
            modifier
                .fillMaxWidth()
                // Blue, and the last of it before the rows — so the wave is here rather than on
                // the chip block above, which defers to this one whenever a day header exists.
                .waveEdgeBackground(MaterialTheme.appColors.primarySubtle)
                .padding(top = MaterialTheme.spacing.sm, bottom = MaterialTheme.spacing.xs + WAVE_DEPTH),
    ) {
        Text(
            text = header.name.uppercase(),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.appColors.onPrimarySubtle,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
        )

        // The same numbers [ProgrammeHeader] pads its own copy with, off the same constant the row
        // reserves its chevron column from.
        SlotScaleRow(
            scale = header.scale,
            modifier =
                Modifier.padding(
                    start = MaterialTheme.spacing.md,
                    end = MaterialTheme.spacing.sm + CHEVRON_SIZE + MaterialTheme.spacing.sm,
                ),
        )
    }
}
