package io.nicolaszurbuchen.yadlo.design.uimodel

import androidx.compose.ui.graphics.vector.ImageVector
import io.nicolaszurbuchen.yadlo.infra.text.UiText

/**
 * One row of a grouped list — an icon, a name, sometimes a line of what is behind it, and the mark
 * that says where tapping goes.
 *
 * In the design system rather than in `feature/plus/` because two features draw it now: the Plus
 * tab's four cards, and the block on Accueil that promotes a handful of those same rows by Phase.
 * They have to be the same object — a row that looks subtly different on the two screens that open
 * *Paiement* is two rows as far as the reader is concerned — and the rule in CLAUDE.md puts a
 * component more than one feature draws in `app/`.
 *
 * It carries no identity and no callback. What a row *is* stays with the feature that owns it, and
 * the card hands that value straight back on a tap, so nothing here has to be matched up by an id.
 *
 * [subtitle] is what the row can say from the content before you open it — *39 partenaires*,
 * *Pas d'espèces sur le site*. Null is the ordinary case rather than the exception.
 */
data class YadloEntryUiModel(
    val icon: ImageVector,
    val title: UiText,
    val subtitle: UiText?,
    val mark: YadloLinkMarkUiModel,
)
