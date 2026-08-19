package io.nicolaszurbuchen.yadlo.app.design.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors

/**
 * The label over a block — *Bon à savoir*, *Accepté partout*, *Rentrer de nuit*, *Sur place*.
 *
 * **Blue rather than the dimmest grey in the palette.** Every screen in the app drew this the same
 * way and it was the wrong way: `textTertiary` is a step *paler* than body text, so a 14pt heading
 * sat quieter on the page than the 15pt paragraph it was introducing. On *Accès*, where four short
 * sections alternate with four short paragraphs, that left a page with no visible structure at all.
 * The bandeau blue is what the prototypes use, and it separates the two by hue rather than by
 * asking a reader to notice one point of size.
 *
 * Uppercase, letterspaced and set in the condensed face — a label rather than a sentence, which is
 * the other half of not being mistaken for the prose under it.
 */
@Composable
fun YadloSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.appColors.primary,
        modifier = modifier,
    )
}
