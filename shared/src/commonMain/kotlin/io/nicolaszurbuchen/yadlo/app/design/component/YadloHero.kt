package io.nicolaszurbuchen.yadlo.app.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * The answer, before the page that supports it.
 *
 * **For the blocks whose whole content is one sentence.** *Paiement* is the case that earned it:
 * the fact is three words, everything under it is a consequence of those three words, and a page
 * that opened with a section header called *Accepté partout* made the reader assemble the answer out
 * of a list. Read it, and you can put the phone away. Accueil's thank-you on the Monday after the
 * festival is the same shape doing the same job.
 *
 * **One component rather than three.** They were written weeks apart and had drifted into blocks
 * that meant the same thing and looked different: Paiement set its headline in the 11pt letterspaced
 * category face, which is a label style, so the one sentence the screen exists to say was the
 * smallest text on it; Accueil's *La programmation est là* was the same shape again on a rose ground,
 * with a [kicker] above the title and a tap that opened the Programme. All three are this now, and
 * what separated them turned out to be two optional lines and a chevron.
 *
 * The tinted ground is [io.nicolaszurbuchen.yadlo.app.design.theme.AppColors.primarySubtle] — the
 * bandeau blue, which is the app's quietest way of saying "this is the festival speaking". It always
 * carries dark ink; white on that blue is 2.4:1 and unusable, which AppColorTest holds rather than
 * leaving as prose.
 *
 * [kicker] is set in the label face, which is what it is: *Nouveau*, *À faire maintenant* — a word
 * about the sentence under it rather than a sentence of its own.
 *
 * A non-null [onClick] makes the whole block the target and draws the chevron that says so. The card
 * rather than a button inside it, which is what the prototype shows and what the rest of the app
 * does — a block you can read is a block you can tap.
 */
@Composable
fun YadloHero(
    title: String,
    modifier: Modifier = Modifier,
    kicker: String? = null,
    body: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.appColors.primarySubtle)
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(MaterialTheme.spacing.lg),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier.weight(1f),
        ) {
            kicker?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.appColors.onPrimarySubtle,
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.appColors.onPrimarySubtle,
            )

            body?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.appColors.onPrimarySubtle,
                )
            }
        }

        if (onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.appColors.onPrimarySubtle,
            )
        }
    }
}
