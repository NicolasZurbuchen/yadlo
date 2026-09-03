package io.nicolaszurbuchen.yadlo.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.nicolaszurbuchen.yadlo.design.theme.WAVE_DEPTH
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.design.theme.waveEdgeBackground
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.back
import yadlo.shared.generated.resources.ic_yadlo

/**
 * The toolbar, everywhere the app draws one.
 *
 * **It is the bandeau blue, and that is the whole reason this exists as a component.** There were
 * four hand-written bars — the tab shell's, the Plus frame's, and one each on the annonces and the
 * wishlist — agreeing on nothing but Material's defaults, which meant `surface`: white in light, so
 * the bar and the screen under it were one undifferentiated field and the app opened looking like
 * nothing in particular. The colour is what separates chrome from page now, so it has to be the
 * same colour on every screen, and the cheapest way to guarantee that is for there to be one bar.
 *
 * [io.nicolaszurbuchen.yadlo.design.theme.AppColors.primarySubtle] always carries dark ink,
 * which AppColorTest holds rather than leaving as prose — the site's own white-on-#74AEE0 is 2.4:1.
 *
 * [subtitle] sits beside the title on the same baseline rather than under it or across the bar: it
 * qualifies the title, and pinned to the right it reads as an unrelated status field. Only the tab
 * shell has one, where it is the edition's dates.
 *
 * [actions] is the trailing slot, empty on every bar but the one Plus screen with something to
 * offer there. It takes the same ink as the chevron, so a screen adding one cannot end up with an
 * icon in a colour the bar does not use.
 *
 * A null [onBackClick] is a bar with no way back, which is what a tab root is. The fiche keeps a bar
 * of its own instead of taking this one: it is transparent over the header image and collapses into
 * the Category's colour as the title scrolls under it, and neither half of that is chrome.
 *
 * **The mark takes the navigation slot on a tab root, because that is the one bar with nothing in
 * it.** It is drawn off the same condition as the chevron rather than off a parameter of its own:
 * "no way back" already means "tab root" here, and a second flag would let the two disagree about
 * which bar this is. The mark and a back arrow are therefore mutually exclusive by construction,
 * which is also what a detail screen wants — the way out of it is the only thing that slot may say.
 *
 * **On a tab root the title is a wordmark rather than a heading**, and it is set as one — sized and
 * weighted against the mark so the two read as a single lockup rather than as a drawing with a
 * caption. See [wordmarkStyle], where both numbers are measured rather than picked. Everywhere else
 * the title names the screen, and stays the heading it has always been.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YadloTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    /**
     * False on the two tab roots whose own chrome carries the wave instead — see
     * [io.nicolaszurbuchen.yadlo.app.navigation.Tab.continuesChrome]. The chrome can only end
     * once, and this bar is not where it ends on those two.
     */
    wavyEdge: Boolean = true,
) {
    // The same condition the mark is drawn off, read once: a bar with no way back is a tab root,
    // where the title is the festival's own name and belongs in the lockup beside the mark. On a
    // detail screen the title is that screen's name — "Annonces", "Nous écrire" — and stays the
    // heading it has always been.
    val isTabRoot = onBackClick == null

    // The wave is added under the bar rather than cut out of it, so the flat part is exactly
    // what it always was and only the page below starts lower. See [WaveEdge].
    val waveDepth = if (wavyEdge) WAVE_DEPTH else 0.dp

    Column(
        modifier =
            modifier.then(
                if (wavyEdge) {
                    Modifier.waveEdgeBackground(MaterialTheme.appColors.primarySubtle)
                } else {
                    Modifier.background(MaterialTheme.appColors.primarySubtle)
                },
            ),
    ) {
        TopAppBar(
            title = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = title,
                        // Set explicitly: TopAppBar defaults its title to titleLarge, which in this
                        // project is the button-label role rather than a heading.
                        style = if (isTabRoot) wordmarkStyle() else MaterialTheme.typography.headlineSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.alignByBaseline(),
                    )

                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.alignByBaseline(),
                        )
                    }
                }
            },
            navigationIcon = {
                if (isTabRoot) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_yadlo),
                        // Decorative: the title beside it is the word this mark stands for, so a
                        // description here would have a screen reader say "Yadlo" twice before the
                        // dates. The same call YadloDietaryTags makes, for the same reason.
                        contentDescription = null,
                        tint = MaterialTheme.appColors.onPrimarySubtle,
                        modifier = Modifier.padding(horizontal = MARK_GUTTER).size(MARK_SIZE),
                    )
                } else {
                    // Smart-cast through isTabRoot, which is the same null check by another name.
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
                        )
                    }
                }
            },
            actions = actions,
            colors =
                TopAppBarDefaults.topAppBarColors(
                    // Transparent both ways: the Column behind it owns the ground now, because
                    // a TopAppBar paints a rectangle and the shape has to be on the thing that
                    // also holds the wave.
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.appColors.onPrimarySubtle,
                    actionIconContentColor = MaterialTheme.appColors.onPrimarySubtle,
                    titleContentColor = MaterialTheme.appColors.onPrimarySubtle,
                ),
        )

        // The wave itself. A Spacer rather than padding on the bar, so the bar keeps the height
        // Material gave it and the decoration is visibly a thing added under it.
        Spacer(modifier = Modifier.fillMaxWidth().height(waveDepth))
    }
}

/**
 * The festival's name set as a lockup with the mark rather than as a heading beside it.
 *
 * **The em box matches the mark, not the ink.** Barlow SemiCondensed puts its cap height and its
 * ascender at exactly 0.70em — measured from the font, and equal to each other because this is a
 * grotesque — and *Yadlo* has no descender, so the word's ink is 0.70 × the font size and nothing
 * else. Setting the size to [MARK_SIZE] therefore draws a word 19.6dp tall beside a 28dp mark: the
 * name sits *within* the mark's height rather than squaring up to it. Matching the ink exactly would
 * take 40sp, which was tried and is too much bar for a word this short.
 *
 * **The weight is measured, and it is why this is not the Bold it started as.** Compare the two as
 * ratios rather than in points, because that is what the eye reads and it holds whatever size the
 * word is set at. The mark's own stroke — the wave bands and the swan, not the hairline ring around
 * them — is 11.1% of the mark's height. Barlow SemiCondensed's stem against its cap height is 10.1%
 * Regular, 13.7% Medium, 16.6% SemiBold and 20.1% Bold. Regular is the one carrying the mark's own
 * ink; Bold is nearly twice it, which is what made the word read as shouting next to a drawing.
 *
 * The ring is thinner still — 4.1% of the mark, a hairline no weight in the family reaches. That is
 * the right thing to miss: an outline is not what gives a mark its weight.
 *
 * Line height equal to the font size, the ordinary tight setting for display type, and it clears the
 * ink here with room to spare: 0.70em of ink in a 1.00em box.
 */
@Composable
private fun wordmarkStyle() =
    MaterialTheme.typography.headlineSmall.copy(
        fontWeight = FontWeight.Normal,
        fontSize = WORDMARK_SIZE,
        lineHeight = WORDMARK_SIZE,
    )

/**
 * Larger than the 24dp of an action icon, because it is not one — nothing about it is tappable, so
 * it is not competing for a touch target, and the mark is a ring with fine internal detail that
 * closes up below about this size.
 */
private val MARK_SIZE = 28.dp

/** The mark's size read as an em box, which puts 19.6dp of ink beside it — see [wordmarkStyle]. */
private val WORDMARK_SIZE = 28.sp

/**
 * The twelve an `IconButton` would have put around the mark, added by hand because there is no
 * button here — and needed on *both* sides, for two different reasons.
 *
 * At the start it is what lands the mark on the page's own 16dp gutter: Material insets the
 * navigation slot by 4, and a back chevron's glyph reaches the same 16 as 4 + (48 - 24) / 2. At the
 * end it is what keeps the title off the mark. Material places the title at the *measured width* of
 * this slot, so without it the title would begin on the exact point the mark stops — the chevron
 * only looks otherwise because its own button carries twelve points of air it does not draw.
 */
private val MARK_GUTTER = 12.dp
