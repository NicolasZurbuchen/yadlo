package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * `dans 15 min` · `en cours` · `se termine · 12 min` · `terminé`.
 *
 * Filled while something is happening or about to stop, outlined while it is only coming, and plain
 * once it is done — three treatments over two colour roles, so that the four states stay apart for
 * a reader who cannot separate green from amber. The words are the primary carrier either way.
 */
@Composable
fun SlotStatePill(
    label: UiText,
    state: SlotLiveStateUiModel,
    modifier: Modifier = Modifier,
) {
    val fill =
        when (state) {
            is SlotLiveStateUiModel.Running -> MaterialTheme.appColors.live
            is SlotLiveStateUiModel.Ending -> MaterialTheme.appColors.warning
            else -> Color.Transparent
        }

    val ink =
        when (state) {
            is SlotLiveStateUiModel.Running -> MaterialTheme.appColors.onLive
            is SlotLiveStateUiModel.Ending -> MaterialTheme.appColors.onWarning
            is SlotLiveStateUiModel.StartingSoon -> MaterialTheme.appColors.warning
            else -> MaterialTheme.appColors.textTertiary
        }

    val border = if (state is SlotLiveStateUiModel.StartingSoon) ink else Color.Transparent

    Text(
        text = label.asString(),
        style = MaterialTheme.typography.labelMedium,
        color = ink,
        modifier =
            modifier
                .clip(MaterialTheme.shapes.extraSmall)
                .background(fill)
                .border(BORDER_WIDTH, border, MaterialTheme.shapes.extraSmall)
                .padding(horizontal = MaterialTheme.spacing.sm, vertical = PILL_VERTICAL_PADDING),
    )
}

private val BORDER_WIDTH = 1.dp

/** Below the smallest spacing step: a pill that takes `sm` on both axes reads as a button. */
private val PILL_VERTICAL_PADDING = 2.dp
