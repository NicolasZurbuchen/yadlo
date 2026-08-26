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
 * been written out in several files, and in most of those files the comment above it was a pointer
 * at another file — *the trailing mark a Plus link tile takes*, *the same square the Programme row
 * marks its Category with*, *the same minimum a Plus row takes*. A comment that has to name another
 * file to explain a number is the number asking for a name.
 *
 * **Two naming conventions, because there are two kinds of token here.** [iconSm] and [iconMd] are
 * a scale, named like [Spacing]'s steps, because that is what they are: an icon is cut to the line
 * of text it sits beside, so the only question is which text — and a third step slots in without
 * renaming anything. The other three are not on any scale. A stroke, a row's minimum height and a
 * Category swatch have no smaller and larger; naming them `borderXs` or `iconXs` would invite a
 * reader to reach for them by size, which is the drift these tokens exist to stop.
 *
 * **A dimension used once stays where it is used.** `YadloTopAppBar`'s 28dp mark argues in four
 * lines why it is *not* [iconMd]; `SlotRow`'s 24dp chevron argues why it is Material's default when
 * every other icon here is cut to its label. Promoting either would delete the reasoning and gain
 * nothing, because nothing else wants the value. The test is repetition, not being a dimension —
 * and a token with no rationale in its KDoc is the same failure one layer up.
 */
data class Sizing(
    /**
     * An icon beside small type — a dietary mark on a tag, a mark on a stands chip.
     *
     * Cut to that label's line height rather than to Material's 24dp icon default or its 18dp chip
     * icon, because the glyph qualifies the word and should not outweigh it.
     */
    val iconSm: Dp = 14.dp,
    /**
     * An icon beside a line of body text: the leading icon on a Plus row, the magnifier in the
     * search field, the trailing mark on a link tile or a fact, the mark on a search result.
     *
     * Material's default is 24dp, which is right for an action in a bar and wrong here — these
     * lists are read by their words, and at 24 the icon outweighs the word next to it.
     */
    val iconMd: Dp = 20.dp,
    /**
     * The Category square that opens a row or a card — the Programme's rows, the Catalogue and
     * Wishlist cards, Mon Yadlo's timeline, a search result.
     *
     * Not on the icon scale, and not an icon: it is a filled square with nothing drawn inside it,
     * sized to say *which* Category without competing with the name it sits before. An icon at this
     * size would be a smudge.
     */
    val categoryMark: Dp = 10.dp,
    /**
     * A hairline outline. Material's own default, kept as a token because it is the only border
     * width the app draws at rest and two components had written it out separately.
     */
    val hairline: Dp = 1.dp,
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

val LocalSizing = compositionLocalOf { Sizing() }

val MaterialTheme.sizing: Sizing
    @Composable
    @ReadOnlyComposable
    get() = LocalSizing.current
