package io.nicolaszurbuchen.yadlo.feature.plus.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * One stated fact — a card that is accepted, a bus line that stops nearby, a toilet that is not
 * adapted.
 *
 * **Facts must not look tappable**, the rule the fiche's own fact row was built on: a leading mark
 * on the page ground, never the card-with-chevron style that is reserved for navigation.
 *
 * **The mark is not coloured, and that is deliberate.** The prototype drew ✓ in green and ✕ in
 * magenta, and neither survives this palette: the magenta is `musique`, and a red-pink beside a
 * kind dot is the collision DECISIONS.md § Open already flags for the accent. So polarity is
 * carried by the glyph and by the section it sits under — *Accepté partout* against *Non accepté* —
 * which is also what story 74 asks for, since colour is then never the only thing saying it.
 */
@Composable
fun PlusFactRow(
    mark: String,
    fact: String,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = mark,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.appColors.textTertiary,
        )

        Text(
            text = fact,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.appColors.textSecondary,
        )
    }
}
