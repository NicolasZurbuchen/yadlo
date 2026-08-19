package io.nicolaszurbuchen.yadlo.app.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.back

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
            onBackClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.back),
                    )
                }
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
