package io.nicolaszurbuchen.yadlo.app.design.uimodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import org.jetbrains.compose.resources.StringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.fact_mark_no
import yadlo.shared.generated.resources.fact_mark_yes

/**
 * What a stated fact *is* — accepted, refused, or merely worth knowing.
 *
 * A closed set rather than three `private const val "✓"` sitting in whichever screen needed one.
 * They were already copied into four files and had already drifted: the same glyph meant "accepted"
 * on Paiement and "included" on Hot'Staff, and nothing stopped a fifth screen inventing a sixth
 * meaning. Three named values is the whole vocabulary, and it is now a compile error to want a
 * fourth without deciding what it means.
 *
 * **Icons rather than the `✓` `✕` `ⓘ` characters they replace**, for the reasons
 * [YadloLinkMarkUiModel] gives at length: identical rendering on both platforms, the row's own tint
 * instead of a font's idea of the glyph, and a screen reader that says "coché" rather than "signe
 * de multiplication".
 *
 * [tint] is the second half of the answer and the reason this is an enum rather than a set of icons.
 * Colour is never the only thing carrying polarity — [CHECK] and [CROSS] both take a
 * [contentDescription], and the mock's Paiement screen puts them under a header that already says
 * which is which — but on a list that mixes the two, tinting is what makes the odd one out findable
 * without reading every line.
 */
enum class YadloFactMarkUiModel(
    val icon: ImageVector,
    val contentDescription: StringResource?,
) {
    /** True of the festival: a card that is taken, a meal that is included. */
    CHECK(Icons.Outlined.Check, Res.string.fact_mark_yes),

    /** Not true of it, and usually the line the whole screen exists to carry. */
    CROSS(Icons.Outlined.Close, Res.string.fact_mark_no),

    /**
     * Neither. A caveat, a detail, a thing to know — and deliberately without a description, because
     * "information : la consigne se paie par carte" reads the row twice.
     */
    INFO(Icons.Outlined.Info, null),
    ;

    val tint: Color
        @Composable
        @ReadOnlyComposable
        get() =
            when (this) {
                CHECK -> MaterialTheme.appColors.positive
                CROSS -> MaterialTheme.appColors.negative
                INFO -> MaterialTheme.appColors.textTertiary
            }
}
