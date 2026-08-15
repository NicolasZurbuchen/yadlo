package io.nicolaszurbuchen.yadlo.feature.plus.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * A tappable destination inside a Plus page — the TWINT site, a timetable PDF, an address to write
 * to.
 *
 * **The trailing mark says where the tap goes**, which is the whole reason facts and links look
 * different here: `↗` leaves for the browser, `✉` opens the mail app, `›` stays inside. On a beach
 * with one bar of signal that difference is what tells someone whether tapping is about to cost
 * them a page load.
 *
 * [sublabel] is where the label alone would be a guess — *PDF · MBC* under a line number, the
 * address under *Écrivez-nous*.
 */
@Composable
fun PlusLinkTile(
    label: String,
    mark: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sublabel: String? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.appColors.surface)
                .clickable { onClick() }
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.appColors.textPrimary,
            )

            sublabel?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
        }

        Text(
            text = mark,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.appColors.textTertiary,
        )
    }
}
