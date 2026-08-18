package io.nicolaszurbuchen.yadlo.app.design.uimodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.link_mark_external
import yadlo.shared.generated.resources.link_mark_mail

/**
 * Where a tap goes, said on the row before it is taken — SPEC.md § Interaction rules. On a beach
 * with one bar of signal, the difference between "this opens instantly" and "this is about to cost
 * you a page load" is worth a glyph in the trailing column.
 *
 * **Icons rather than the `›` `↗` `✉` characters they replace.** Three reasons, and the third is
 * the one that settles it:
 *  - they render identically on both platforms, where the characters do not — `✉` in particular
 *    carries emoji presentation on iOS and comes out as a full-colour envelope beside a monochrome
 *    list;
 *  - they take the row's tint like every other icon in the app, instead of inheriting a text
 *    colour and a font's own idea of the glyph's weight;
 *  - a character is announced literally by a screen reader, so `↗` becomes "north-east arrow" and
 *    `✉` becomes "envelope" — words that describe the shape rather than what happens next.
 *
 * [contentDescription] is null for [DISCLOSURE] on purpose. A chevron on a row that is already
 * clickable says nothing the row has not said, and reading it aloud lengthens every entry in the
 * tab. The two that leave the app do carry one, because *that* is a fact the visual mark is the
 * only thing conveying.
 *
 * Polarity — a ✓ or a ✕ on a fact — is deliberately **not** here. That is content about a thing
 * rather than a statement about a tap, and it has an enum of its own in [FactMarkUiModel].
 */
enum class LinkMarkUiModel(
    val icon: ImageVector,
    val contentDescription: StringResource?,
) {
    /** Stays in the app. */
    DISCLOSURE(Icons.AutoMirrored.Filled.KeyboardArrowRight, null),

    /** Leaves for the browser. */
    EXTERNAL(Icons.AutoMirrored.Outlined.OpenInNew, Res.string.link_mark_external),

    /** Hands the address to the visitor's own mail app. */
    MAIL(Icons.Outlined.MailOutline, Res.string.link_mark_mail),
}
