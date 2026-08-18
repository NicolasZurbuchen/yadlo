package io.nicolaszurbuchen.yadlo.app.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloFactMarkUiModel
import org.jetbrains.compose.resources.stringResource

/**
 * One stated fact — a card that is accepted, a bus line that stops nearby, a meal that comes with
 * the shift.
 *
 * In the design system rather than in `feature/plus/` because it is not a Plus row: the fiche states
 * facts about a Stand, Accueil states them about the edition, and Confidentialité states them about
 * the app. Three features drawing the same shape is the definition given in CLAUDE.md for something
 * that belongs in `app/`.
 *
 * **Facts must not look tappable**, the rule the fiche's own fact row was built on: a leading mark
 * on the page ground, never the card-with-chevron style that is reserved for navigation.
 *
 * The mark is tinted by what it means — see [YadloFactMarkUiModel] — and never *only* by what it means.
 * Every coloured mark also carries a glyph and a content description, so the polarity survives a
 * greyscale screenshot, a colour-blind reader and a screen reader alike.
 */
@Composable
fun YadloFactRow(
    mark: YadloFactMarkUiModel,
    fact: String,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        Icon(
            imageVector = mark.icon,
            contentDescription = mark.contentDescription?.let { stringResource(it) },
            tint = mark.tint,
            // Nudged down onto the first line's optical centre. An icon top-aligned with a text run
            // sits visibly high, because the glyph fills its box where the letters do not fill
            // theirs — and this row is read as a sentence with a mark in front of it.
            modifier = Modifier.padding(top = MARK_BASELINE_NUDGE).size(MARK_SIZE),
        )

        Text(
            text = fact,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.appColors.textSecondary,
        )
    }
}

// Matched to the sentence's own line height rather than to Material's 24dp default: at 24 the mark
// outweighs the fact it qualifies.
private val MARK_SIZE = 20.dp
private val MARK_BASELINE_NUDGE = 2.dp
