package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.HomeBlockUiModel

/**
 * The four networks, quietly, at the foot of the screen.
 *
 * Icons only. The names still travel on the model and become each button's `contentDescription`,
 * so a screen reader announces "Instagram" rather than "button" — dropping the label from the
 * screen is a visual decision, not a reason to make the row unusable without sight.
 *
 * Deliberately not a feed. A real Instagram feed needs a Business account and a Meta app authorised
 * by the association, which is out of reach while the app is unofficial, so these are links out.
 *
 * A [FlowRow] rather than a Row so the buttons wrap instead of being clipped when the display scale
 * is turned up — this is the last thing on the screen and it has the room.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SocialBlock(
    block: HomeBlockUiModel.Social,
    onSocialClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        block.items.forEach { item ->
            // IconButton rather than a clickable Icon: it brings the 48dp touch target that a bare
            // 24dp mark would not, and four of them in a row is exactly the case that needs one.
            IconButton(onClick = { onSocialClick(item.url) }) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.name,
                    tint = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier.size(SOCIAL_ICON_SIZE),
                )
            }
        }
    }
}

private val SOCIAL_ICON_SIZE = 24.dp
