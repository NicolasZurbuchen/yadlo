package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandChipUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * The dietary filter — *Tout*, then every mark that any stand or any dish actually carries.
 *
 * The set is derived from the listing rather than declared, so a chip is never offered that matches
 * nothing and a mark the content adds appears without an app release.
 *
 * Horizontally scrolling rather than wrapped, so the row keeps its height whatever the content
 * publishes. There are five chips today and the same layout survives fifteen.
 */
@Composable
fun StandMarkChips(
    chips: List<StandChipUiModel>,
    onMarkClick: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier =
            modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = MaterialTheme.spacing.md),
    ) {
        chips.forEach { chip ->
            FilterChip(
                selected = chip.isSelected,
                onClick = { onMarkClick(chip.mark) },
                label = {
                    Text(
                        text = chip.label.asString(),
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                colors =
                    FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.appColors.primary,
                        selectedLabelColor = MaterialTheme.appColors.onPrimary,
                        labelColor = MaterialTheme.appColors.textSecondary,
                    ),
            )
        }
    }
}
