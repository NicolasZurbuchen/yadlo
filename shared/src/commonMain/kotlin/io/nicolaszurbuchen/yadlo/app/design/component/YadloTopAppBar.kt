package io.nicolaszurbuchen.yadlo.app.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
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
 * [io.nicolaszurbuchen.yadlo.app.design.theme.AppColors.primarySubtle] always carries dark ink,
 * which AppColorTest holds rather than leaving as prose — the site's own white-on-#74AEE0 is 2.4:1.
 *
 * [subtitle] sits beside the title on the same baseline rather than under it or across the bar: it
 * qualifies the title, and pinned to the right it reads as an unrelated status field. Only the tab
 * shell has one, where it is the edition's dates.
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
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YadloTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
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
                    style = MaterialTheme.typography.headlineSmall,
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
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.back),
                    )
                }
            } else {
                Icon(
                    painter = painterResource(Res.drawable.ic_yadlo),
                    // Decorative: the title beside it is the word this mark stands for, so a
                    // description here would have a screen reader say "Yadlo" twice before the
                    // dates. The same call YadloDietaryTags makes, for the same reason.
                    contentDescription = null,
                    tint = MaterialTheme.appColors.onPrimarySubtle,
                    modifier = Modifier.padding(horizontal = MARK_GUTTER).size(MARK_SIZE),
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.appColors.primarySubtle,
                scrolledContainerColor = MaterialTheme.appColors.primarySubtle,
                navigationIconContentColor = MaterialTheme.appColors.onPrimarySubtle,
                titleContentColor = MaterialTheme.appColors.onPrimarySubtle,
            ),
        modifier = modifier,
    )
}

/**
 * Larger than the 24dp of an action icon, because it is not one — nothing about it is tappable, so
 * it is not competing for a touch target, and the mark is a ring with fine internal detail that
 * closes up below about this size.
 */
private val MARK_SIZE = 28.dp

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
