package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import io.nicolaszurbuchen.yadlo.app.design.component.YadloSectionHeader
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
 *
 * **The header is padded, the content slot is not.** The block used to pad everything at once,
 * which is fine for a paragraph and wrong for anything tappable: the annonces' ripple and the rules
 * between them stopped 16dp short of the border, so a row that reads as full width was not one. A
 * slot that holds rows sets [contentPadding] to zero and pads inside each row instead; a slot that
 * holds a paragraph leaves the default alone.
 */
@Composable
fun SectionBlock(
    title: UiText,
    modifier: Modifier = Modifier,
    actionLabel: UiText? = null,
    onActionClick: (() -> Unit)? = null,
    contentPadding: PaddingValues =
        PaddingValues(
            start = MaterialTheme.spacing.md,
            end = MaterialTheme.spacing.md,
            bottom = MaterialTheme.spacing.md,
        ),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .border(BORDER_WIDTH, MaterialTheme.appColors.borderSubtle, MaterialTheme.shapes.medium),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.spacing.md,
                        end = MaterialTheme.spacing.md,
                        top = MaterialTheme.spacing.md,
                    ),
        ) {
            YadloSectionHeader(title = title.asString())

            if (actionLabel != null && onActionClick != null) {
                Text(
                    text = actionLabel.asString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.appColors.primary,
                    modifier =
                        Modifier
                            .clip(MaterialTheme.shapes.extraSmall)
                            .clickable(onClick = onActionClick)
                            .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs),
                )
            }
        }

        // No arrangement of its own: a slot holding rows wants its rules flush against them, and a
        // slot holding one thing has nothing to space. The gap under the header is the outer one.
        Column(modifier = Modifier.fillMaxWidth().padding(contentPadding)) {
            content()
        }
    }
}

private val BORDER_WIDTH = 1.dp
