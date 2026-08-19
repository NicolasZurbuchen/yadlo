package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance.EmergencyNumberUiModel

/**
 * One emergency number, and the largest thing on its screen after the title.
 *
 * These are the only content in this app that is true whatever happens and needs nobody's
 * confirmation, which is why they get the display face at heading size rather than a row like any
 * other. Someone reading this is not browsing.
 *
 * **In the accent's ink, not the page's.** They were the same colour as every other title on the
 * screen, so the one thing worth finding in a hurry looked like a heading. `accent` itself is a
 * fill role and measures 2.3:1 as text on the page ground, so this uses the ink step of the same
 * rose ramp — see
 * [io.nicolaszurbuchen.yadlo.app.design.theme.AppColors.accentInk].
 *
 * **Tabular figures in a column of their own**, which is what makes every label start at the same
 * x. Barlow's proportional numerals give 112 and 144 different widths, so four labels set beside
 * them landed at four different places and read as a ragged edge rather than a list.
 *
 * The whole row is the target, not the number alone: it is read under stress, sometimes one-handed.
 */
@Composable
fun EmergencyNumberRow(
    number: EmergencyNumberUiModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick(number.number) }
                .padding(vertical = MaterialTheme.spacing.md),
    ) {
        Text(
            text = number.number,
            style = MaterialTheme.typography.headlineSmall.copy(fontFeatureSettings = TABULAR_FIGURES),
            color = MaterialTheme.appColors.accentInk,
            modifier = Modifier.widthIn(min = NUMBER_COLUMN),
        )

        Text(
            text = number.label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.appColors.textSecondary,
            modifier = Modifier.weight(1f),
        )
    }
}

// Three tabular digits of the display face at heading size, which is every emergency number Europe
// publishes. A minimum rather than a fixed width, so a longer one is never clipped — it pushes its
// own label instead, which is visibly odd and therefore fixable, unlike a truncated number.
private val NUMBER_COLUMN = 46.dp

// Same device Type.kt uses for the Programme's times, and for the same reason: without it the
// numerals are proportional and the column they sit in is not a column.
private const val TABULAR_FIGURES = "tnum"
