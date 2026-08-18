package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloSectionHeader
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * A titled block on the fiche — *Quand*, *Tarifs*, *Au menu*, *Bon à savoir*, *Liens*.
 *
 * A header over content on the page background, never a card: cards separate, and these sections are
 * one continuous reading of one thing. The screen decides which of them exist by which lists are
 * non-empty, so a section is never drawn with nothing under it.
 */
@Composable
fun HappeningSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        YadloSectionHeader(title = title)

        content()
    }
}
