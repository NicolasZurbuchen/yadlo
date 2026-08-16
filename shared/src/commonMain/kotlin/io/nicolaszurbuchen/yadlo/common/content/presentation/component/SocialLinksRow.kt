package io.nicolaszurbuchen.yadlo.common.content.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
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
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SocialLinkUiModel
import org.jetbrains.compose.resources.painterResource

/**
 * The four networks, quietly, at the foot of a screen. Accueil ends with it, and so does Plus.
 *
 * The association's own marks, bundled rather than fetched — they are chrome, not content, and a
 * row of empty squares is a worse first launch than no row at all. They are monochrome single-path
 * vectors, so they take the row's own colour instead of four brand palettes fighting the page.
 *
 * Icons only. The names still travel on the model and become each button's `contentDescription`,
 * so a screen reader announces "Instagram" rather than "button" — dropping the label from the
 * screen is a visual decision, not a reason to make the row unusable without sight. A network with
 * no bundled mark falls back to its name, so the content can add one before the app ships its icon.
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
) {
    FlowRow(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        items.forEach { item ->
            if (item.icon != null) {
                // IconButton rather than a clickable Icon: it brings the 48dp touch target that a
                // bare 24dp mark would not, and four in a row is exactly the case that needs one.
                IconButton(onClick = { onSocialClick(item.url) }) {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.name,
                        tint = MaterialTheme.appColors.textSecondary,
                        modifier = Modifier.size(SOCIAL_ICON_SIZE),
                    )
                }
            } else {
                TextButton(onClick = { onSocialClick(item.url) }) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }
            }
        }
    }
}

private val SOCIAL_ICON_SIZE = 24.dp
