package io.nicolaszurbuchen.yadlo.feature.plus.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * The answer, before the page that supports it.
 *
 * **For the screens whose whole content is one sentence.** *Paiement* is the case that earned it:
 * the fact is three words, everything under it is a consequence of those three words, and a page
 * that opened with a section header called *Accepté partout* made the reader assemble the answer
 * out of a list. Read it, and you can put the phone away.
 *
 * The tinted ground is [io.nicolaszurbuchen.yadlo.app.design.theme.AppColors.primarySubtle] — the
 * bandeau blue, which is the app's quietest way of saying "this is the festival speaking". It always
 * carries dark ink; white on that blue is 2.4:1 and unusable, which AppColorTest holds rather than
 * leaving as prose.
 */
@Composable
fun PlusHeroStatement(
    headline: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.appColors.primarySubtle)
                .padding(MaterialTheme.spacing.md),
    ) {
        Text(
            text = headline,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.appColors.onPrimarySubtle,
        )

        summary?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.appColors.onPrimarySubtle,
            )
        }
    }
}
