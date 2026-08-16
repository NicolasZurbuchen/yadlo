package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance.EmergencyNumberUiModel

/**
 * One emergency number, and the largest thing on its screen after the title.
 *
 * These four are the only content in this app that is true whatever happens and needs nobody's
 * confirmation, which is why they get the display face at heading size rather than a row like any
 * other. Someone reading this is not browsing.
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
                .padding(vertical = MaterialTheme.spacing.sm),
    ) {
        Text(
            text = number.number,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.appColors.textPrimary,
        )

        Text(
            text = number.label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.appColors.textSecondary,
            modifier = Modifier.weight(1f),
        )
    }
}
