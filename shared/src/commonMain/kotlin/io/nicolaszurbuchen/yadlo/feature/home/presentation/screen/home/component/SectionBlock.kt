package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * One bordered block: a header rule with the section label, an optional action on the right, and
 * whatever the section holds inside it.
 *
 * Shared by the annonces and the chiffres so they read as siblings rather than as two people's
 * ideas of a section — the prototype draws both the same way.
 */
@Composable
fun SectionBlock(
    title: UiText,
    modifier: Modifier = Modifier,
    actionLabel: UiText? = null,
    onActionClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .border(BORDER_WIDTH, MaterialTheme.appColors.borderSubtle, MaterialTheme.shapes.medium)
                .padding(MaterialTheme.spacing.md),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = title.asString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.appColors.textPrimary,
            )

            if (actionLabel != null && onActionClick != null) {
                Text(
                    text = actionLabel.asString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.appColors.primary,
                    modifier =
                        Modifier
                            .clip(MaterialTheme.shapes.extraSmall)
                            .clickable(onClick = onActionClick)
                            .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs),
                )
            }
        }

        content()
    }
}

private val BORDER_WIDTH = 1.dp
