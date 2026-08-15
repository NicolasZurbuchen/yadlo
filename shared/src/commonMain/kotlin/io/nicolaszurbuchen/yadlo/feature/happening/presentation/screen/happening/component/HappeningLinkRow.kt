package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * A link that leaves the app — an artist's own site, a stand's Instagram, a booking page.
 *
 * Marked `↗` rather than `›`. The outward arrow is the app's word for "this leaves", the chevron for
 * "this navigates", and the difference is what tells someone on the beach with one bar of signal
 * whether tapping is about to cost them a page load.
 */
@Composable
fun HappeningLinkRow(
    label: UiText,
    url: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick(url) }
                .padding(vertical = MaterialTheme.spacing.sm),
    ) {
        Text(
            text = label.asString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.appColors.primary,
        )

        Text(
            text = EXTERNAL_MARK,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.appColors.primary,
        )
    }
}

private const val EXTERNAL_MARK = "↗"
