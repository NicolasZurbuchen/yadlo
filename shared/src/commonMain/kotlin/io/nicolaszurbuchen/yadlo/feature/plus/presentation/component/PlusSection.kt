package io.nicolaszurbuchen.yadlo.feature.plus.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * A titled block on a Plus detail page — *Accepté partout*, *Rentrer de nuit*, *Urgence vitale*.
 *
 * A header over content on the page ground, never a card, for the reason the fiche gives: cards
 * separate, and a practical-information page is one continuous reading of one subject. The screen
 * decides which sections exist by which lists are non-empty, so a header is never drawn with
 * nothing under it.
 *
 * A null [title] draws the block without its header. That is not a styling option — it is what a
 * block whose content is published without a heading looks like, and an empty rule of uppercase
 * nothing above a paragraph would be worse than no rule at all.
 */
@Composable
fun PlusSection(
    title: String?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        title?.let {
            Text(
                text = it.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.appColors.textTertiary,
            )
        }

        content()
    }
}
