package io.nicolaszurbuchen.yadlo.app.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * How big a thing is, as opposed to how far apart two things are.
 *
 * **A second scale beside [Spacing] rather than more steps inside it.** Spacing is a rhythm — the
 * gaps *between* things — and folding sizes into it would make `spacing.sm` sometimes a gap and
 * sometimes a glyph, which is how the next person picks whichever number looks right instead of the
 * one that means the right thing.
 *
 * **Everything here was measured, not designed.** Each token replaces a private constant that had
 * been written out in three or more files, and in most of those files the comment above it was a
 * pointer at another file — *the trailing mark a Plus link tile takes*, *the same square the
 * Programme row marks its Category with*, *the same minimum a Plus row takes*. A comment that has to
 * name another file to explain a number is the number asking for a name.
 *
 * **A dimension used once stays where it is used.** `YadloTopAppBar`'s 28dp mark argues in four
 * lines why it is *not* [icon]; `SlotRow`'s 24dp chevron argues why it is Material's default when
 * every other icon here is cut to its label. Promoting either would delete the reasoning and gain
 * nothing, because nothing else wants the value. The test is repetition, not being a dimension —
 * and a token with no rationale in its KDoc is the same failure one layer up.
 */
data class AppSizing(
    /**
     * A hairline outline. Material's own default, kept as a token because it is the only border
     * width the app draws at rest and two components had written it out separately.
     */
    val hairline: Dp = 1.dp,
    /**
     * The Category square that opens a row or a card — the Programme's rows, the Catalogue and
     * Wishlist cards, Mon Yadlo's timeline, a search result.
     *
     * It is small on purpose: a swatch beside a word that already names the Category, so it says
     * *which* without competing with the name it sits before.
     */
    val categoryMark: Dp = 10.dp,
    /**
     * A glyph standing beside its own word — a dietary mark on a tag, a mark on a stands chip.
     *
     * Cut to the label's line height rather than to Material's 24dp icon default or its 18dp chip
     * icon, because the glyph qualifies the word and should not outweigh it.
     */
    val glyph: Dp = 14.dp,
    /**
     * An icon cut to the line of text it belongs to: a leading icon on a Plus row, the magnifier in
     * the search field, the trailing mark on a link tile or a fact.
     *
     * Material's default is 24dp, which is right for an action in a bar and wrong here — these
     * lists are read by their words, and at 24 the icon outweighs the word next to it.
     */
    val icon: Dp = 20.dp,
    /**
     * The least tall a tappable row or tile gets, whether or not the content gave it a second line.
     *
     * A minimum rather than a fixed height, so the largest accessibility text sizes can still grow
     * it — clipping a subtitle to keep the rhythm would be the wrong trade. What it buys is a column
     * of sixteen rows that does not comb up and down as the content publishes one line here and two
     * there.
     */
    val rowMinHeight: Dp = 64.dp,
)

val LocalSizing = compositionLocalOf { AppSizing() }

val MaterialTheme.sizing: AppSizing
    @Composable
    @ReadOnlyComposable
    get() = LocalSizing.current
