package io.nicolaszurbuchen.yadlo.core.content.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.SocialLinkUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.painterResource

/**
 * Where else a thing can be found, as a row of marks. Accueil ends with it, Plus ends with it, and
 * a fiche's *Liens* section is it.
 *
 * The marks are bundled rather than fetched — they are chrome, not content, and a row of empty
 * squares is a worse first launch than no row at all. They are monochrome vectors, so they take the
 * row's own colour instead of ten brand palettes fighting the page.
 *
 * Icons only. The names still travel on the model and become each button's `contentDescription`,
 * so a screen reader announces "Instagram" rather than "button" — dropping the label from the
 * screen is a visual decision, not a reason to make the row unusable without sight. A platform with
 * no bundled mark falls back to its name, so the content can add one before the app ships its icon.
 *
 * **[start] is what a fiche needs and a footer does not.** Centred, the row reads as the end of a
 * page, which is what it is under Accueil and under Plus. Inside a fiche it is a section with a
 * title over it, and everything else on that screen — the description, the dates, the prices — sits
 * on one left edge; a centred row of icons in the middle of that column reads as a footer that has
 * landed halfway up the page.
 *
 * The left-aligned row is nudged back by [EDGE_CORRECTION] so that the first mark, rather than the
 * invisible touch target around it, lands on that edge. An [IconButton] is 48dp around a 24dp icon,
 * and without the correction the row starts twelve points in from every line above it — which is
 * exactly the misalignment left-aligning it was meant to fix.
 *
 * Deliberately not a feed. A real Instagram feed needs a Business account and a Meta app authorised
 * by the association, which is out of reach while the app is unofficial, so these are links out.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SocialLinksRow(
    items: List<SocialLinkUiModel>,
    onSocialClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    start: Boolean = false,
) {
    FlowRow(
        horizontalArrangement = if (start) Arrangement.Start else Arrangement.Center,
        modifier =
            modifier
                .fillMaxWidth()
                .then(if (start) Modifier.offset(x = -EDGE_CORRECTION) else Modifier),
    ) {
        items.forEach { item ->
            val name = item.name.asString()

            if (item.icon != null) {
                // IconButton rather than a clickable Icon: it brings the 48dp touch target that a
                // bare 24dp mark would not, and four in a row is exactly the case that needs one.
                IconButton(onClick = { onSocialClick(item.url) }) {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = name,
                        tint = MaterialTheme.appColors.textSecondary,
                        modifier = Modifier.size(SOCIAL_ICON_SIZE),
                    )
                }
            } else {
                TextButton(onClick = { onSocialClick(item.url) }) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }
            }
        }
    }
}

private val SOCIAL_ICON_SIZE = 24.dp

/** Half of what an [IconButton] puts around a [SOCIAL_ICON_SIZE] mark: (48 - 24) / 2. */
private val EDGE_CORRECTION = 12.dp
