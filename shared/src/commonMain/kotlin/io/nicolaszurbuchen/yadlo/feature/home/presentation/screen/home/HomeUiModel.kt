package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.SmartDisplay
import androidx.compose.ui.graphics.vector.ImageVector
import io.nicolaszurbuchen.yadlo.feature.home.presentation.uimodel.AnnouncementUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * Accueil is a block stack whose contents change with the Phase, so the screen renders a list it is
 * handed rather than deciding for itself which blocks exist. That puts the phase-to-blocks decision
 * in the UiMapper, where a test can assert it.
 */
data class HomeUiModel(
    val isLoading: Boolean,
    val blocks: List<HomeBlockUiModel>,
)

sealed interface HomeBlockUiModel {
    /**
     * A day count, not a clock. The prototype is unambiguous — one large `J-19` — and it is the
     * right unit: nobody plans around the seconds until a festival nineteen days away.
     */
    data class Countdown(
        val daysText: UiText,
        val subtitle: String,
    ) : HomeBlockUiModel

    data class Hero(
        val kicker: UiText,
        val title: UiText,
        val body: UiText,
    ) : HomeBlockUiModel

    data class ThankYou(
        val title: UiText,
        val body: UiText,
    ) : HomeBlockUiModel

    data class Figures(
        val title: UiText,
        val items: List<FigureUiModel>,
        /** Null when every figure is confirmed; the caveat Provenance owes the reader otherwise. */
        val caveat: UiText?,
    ) : HomeBlockUiModel

    /**
     * [hasMore] is false when the block is already showing every annonce there is — the action that
     * opens the full list is hidden then, because a button leading to what you are already reading
     * is the same problem as a button that does nothing.
     */
    data class Announcements(
        val title: UiText,
        val items: List<AnnouncementUiModel>,
        val hasMore: Boolean,
    ) : HomeBlockUiModel

    data class Social(
        val items: List<SocialUiModel>,
    ) : HomeBlockUiModel
}

data class FigureUiModel(
    val id: String,
    val value: String,
    val label: String,
)

data class SocialUiModel(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val url: String,
)

/**
 * Keyed on the content's own id, so a network the app has never heard of renders with the generic
 * mark rather than failing to appear.
 *
 * **Three of these are stand-ins.** Material ships a real Facebook mark and nothing for the others,
 * so the rest are the nearest honest metaphor. Drop the brand vectors into
 * `composeResources/drawable` and each becomes a one-line swap.
 *
 * It lives beside [SocialUiModel] rather than in the UiMapper because a UiMapper file may hold
 * nothing but the single State-to-UiModel function.
 */
fun socialIconFor(id: String): ImageVector =
    when (id) {
        "instagram" -> Icons.Outlined.PhotoCamera
        "facebook" -> Icons.Filled.Facebook
        "youtube" -> Icons.Outlined.SmartDisplay
        "tiktok" -> Icons.Outlined.MusicNote
        else -> Icons.Outlined.Public
    }
