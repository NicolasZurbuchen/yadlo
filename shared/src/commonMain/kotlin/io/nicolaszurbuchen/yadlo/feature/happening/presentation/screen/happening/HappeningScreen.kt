package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.component.YadloDietaryTags
import io.nicolaszurbuchen.yadlo.app.design.component.YadloFactRow
import io.nicolaszurbuchen.yadlo.app.design.component.YadloLinkTile
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.categoryColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloFactMarkUiModel
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloLinkMarkUiModel
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component.HappeningHeader
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component.HappeningMenuGroupBlock
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component.HappeningPriceBlock
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component.HappeningSection
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component.HappeningSlotRow
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component.HappeningTagRow
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component.SavedHeart
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.back
import yadlo.shared.generated.resources.happening_missing
import yadlo.shared.generated.resources.happening_section_good_to_know
import yadlo.shared.generated.resources.happening_section_links
import yadlo.shared.generated.resources.happening_section_menu
import yadlo.shared.generated.resources.happening_section_price
import yadlo.shared.generated.resources.happening_section_when
import yadlo.shared.generated.resources.wishlist_add
import yadlo.shared.generated.resources.wishlist_remove

/**
 * One template for an Artist, an Activity and a Stand — DECISIONS.md § One fiche template for
 * everything. Which sections appear is decided by which lists are non-empty, never by the kind of
 * Happening, so a Stand that publishes no menu and an Activity that costs nothing degrade the same
 * quiet way.
 *
 * **The Category's colour closes over the whole head, not just over the toolbar.** One number
 * drives both: the bar's container and the veil over the header take the same alpha, so what
 * arrives is a single block of colour rather than a tinted bar sitting on an untinted photograph.
 * It is tied to the scroll rather than switched at a threshold — a slow drag paints the colour on
 * slowly and a fling lands on it, where a threshold made the same journey a jump halfway through a
 * drag.
 *
 * **It starts late and finishes early**, both deliberately. Nothing happens for the first third of
 * the header's travel, because a photograph that starts dissolving the moment a thumb moves reads as
 * fragile; and full opacity is reached with a little of the header still to go, because the last
 * few percent of a linear ramp are a photograph showing faintly through a colour that is supposed to
 * be solid — which was the state that looked broken rather than gradual. The bar's own title fades
 * in later still, since until then the header is carrying it and two copies of one title crossing
 * each other looks like a bug.
 *
 * The status bar is not tinted with it — that is a system-window concern the app shell has not taken
 * on yet, and doing it from one screen would leave the other four inconsistent. The header runs
 * underneath it regardless, which is what puts the photograph against the top of the screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HappeningScreen(
    state: HappeningUiModel,
    onBackClick: () -> Unit,
    onLinkClick: (String) -> Unit,
    onSlotHeartClick: (String) -> Unit,
    onWishlistHeartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val category = MaterialTheme.categoryColors.forId(state.categoryId)
    val density = LocalDensity.current

    // The header is item zero, so how much of it is left is exactly how far item zero has scrolled.
    // Measured rather than assumed: it is a fixed 280dp with a photograph behind it, and as tall as
    // its own title without one.
    var headerHeightPx by remember { mutableIntStateOf(0) }
    val barBottomPx = WindowInsets.statusBars.getTop(density) + with(density) { TOP_BAR_HEIGHT.roundToPx() }

    val collapseProgress by remember(barBottomPx) {
        derivedStateOf {
            // What the header has to give before the bar is standing on the content below it.
            val travel = headerHeightPx - barBottomPx

            when {
                listState.firstVisibleItemIndex > 0 -> 1f
                headerHeightPx == 0 || travel <= 0 -> 0f
                else -> (listState.firstVisibleItemScrollOffset.toFloat() / travel).coerceIn(0f, 1f)
            }
        }
    }

    // The colour's own ramp, which is not the header's: see the note on the two constants.
    val tint = ((collapseProgress - TINT_FROM) / (TINT_FULL - TINT_FROM)).coerceIn(0f, 1f)
    val barColor = category.fill.copy(alpha = tint)
    // Over a photograph the icons stand on the scrim, over the blob they stand on the page. Either
    // way they end on the Category's own ink, and they travel there as the colour arrives.
    val expandedInk =
        if (state.imageUrl != null) MaterialTheme.appColors.onScrim else MaterialTheme.appColors.textPrimary
    val barInk = lerp(expandedInk, category.ink, tint)
    val barTitleAlpha = ((collapseProgress - TITLE_FADE_FROM) / (1f - TITLE_FADE_FROM)).coerceIn(0f, 1f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.alpha(barTitleAlpha),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
                        )
                    }
                },
                // Only a Stand has one, because only a Stand is kept whole — everything else keeps
                // its Slots one at a time, on the date rows below.
                actions = {
                    state.wishlisted?.let { isSaved ->
                        IconButton(onClick = onWishlistHeartClick) {
                            SavedHeart(
                                isSaved = isSaved,
                                contentDescription =
                                    stringResource(
                                        if (isSaved) Res.string.wishlist_remove else Res.string.wishlist_add,
                                    ),
                                tint = barInk,
                            )
                        }
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = barColor,
                        scrolledContainerColor = barColor,
                        navigationIconContentColor = barInk,
                        titleContentColor = barInk,
                    ),
            )
        },
        modifier = modifier,
    ) { contentPadding ->
        when {
            state.isLoading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize().padding(contentPadding),
                ) {
                    CircularProgressIndicator()
                }
            }

            state.isMissing -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize().padding(contentPadding).padding(MaterialTheme.spacing.xl),
                ) {
                    Text(
                        text = stringResource(Res.string.happening_missing),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }
            }

            else -> {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
                    contentPadding = PaddingValues(bottom = MaterialTheme.spacing.xxl),
                    // The list runs under the transparent bar on purpose, so the photograph
                    // starts at the top of the screen rather than below a gap. Nothing else needs
                    // the inset: the header's own words are anchored to its bottom edge.
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item(key = "header") {
                        HappeningHeader(
                            imageUrl = state.imageUrl,
                            categoryId = state.categoryId,
                            categoryLabel = state.categoryLabel,
                            title = state.title,
                            tint = tint,
                            modifier = Modifier.onSizeChanged { headerHeightPx = it.height },
                        )
                    }

                    if (state.tags.isNotEmpty()) {
                        item(key = "tags") {
                            HappeningTagRow(
                                tags = state.tags,
                                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                            )
                        }
                    }

                    if (state.dietary.isNotEmpty()) {
                        item(key = "dietary") {
                            YadloDietaryTags(
                                tags = state.dietary,
                                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                            )
                        }
                    }

                    state.description?.let { description ->
                        item(key = "description") {
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.appColors.textSecondary,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = MaterialTheme.spacing.md),
                            )
                        }
                    }

                    if (state.slots.isNotEmpty()) {
                        item(key = "when") {
                            HappeningSection(
                                title = stringResource(Res.string.happening_section_when),
                                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                            ) {
                                state.slots.forEach { slot ->
                                    HappeningSlotRow(slot = slot, onClick = onSlotHeartClick)
                                }
                            }
                        }
                    }

                    if (state.price != null || state.booking != null) {
                        item(key = "price") {
                            HappeningSection(
                                title = stringResource(Res.string.happening_section_price),
                                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                            ) {
                                state.price?.let { price ->
                                    HappeningPriceBlock(price = price)
                                }

                                state.booking?.let { booking ->
                                    // A booking with a page is a link out; one without is a fact
                                    // there is nothing to do about, so it must not look tappable.
                                    if (booking.url != null) {
                                        YadloLinkTile(
                                            label = booking.label.asString(),
                                            mark = YadloLinkMarkUiModel.EXTERNAL,
                                            onClick = { onLinkClick(booking.url) },
                                        )
                                    } else {
                                        Text(
                                            text = booking.label.asString(),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.appColors.textSecondary,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (state.facts.isNotEmpty()) {
                        item(key = "facts") {
                            HappeningSection(
                                title = stringResource(Res.string.happening_section_good_to_know),
                                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                            ) {
                                state.facts.forEach { fact ->
                                    YadloFactRow(mark = YadloFactMarkUiModel.CHECK, fact = fact.asString())
                                }
                            }
                        }
                    }

                    if (state.menu.isNotEmpty()) {
                        item(key = "menu") {
                            HappeningSection(
                                title = stringResource(Res.string.happening_section_menu),
                                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                            ) {
                                state.menu.forEach { group ->
                                    HappeningMenuGroupBlock(
                                        group = group,
                                        modifier = Modifier.padding(bottom = MaterialTheme.spacing.md),
                                    )
                                }
                            }
                        }
                    }

                    if (state.links.isNotEmpty()) {
                        item(key = "links") {
                            HappeningSection(
                                title = stringResource(Res.string.happening_section_links),
                                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                            ) {
                                state.links.forEach { link ->
                                    YadloLinkTile(
                                        label = link.label.asString(),
                                        mark = YadloLinkMarkUiModel.EXTERNAL,
                                        onClick = { onLinkClick(link.url) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Material's own container height for a small top app bar. Written out because the collapse needs
 * the number rather than the bar, and asking the bar would mean measuring a composable that is drawn
 * above the one whose height decides when it changes colour.
 */
private val TOP_BAR_HEIGHT = 64.dp

/**
 * Where the Category's colour starts arriving, as a fraction of the header's travel. Late enough
 * that a small scroll leaves the photograph alone — the first third of the journey is the part a
 * thumb does by accident.
 */
private const val TINT_FROM = 0.35f

/**
 * And where it is fully arrived, with a fifth of the travel still to go. The alternative is a linear
 * ramp that is still 5% transparent when the image is all but gone, which does not read as nearly
 * opaque — it reads as a photograph faintly showing through something meant to be solid.
 */
private const val TINT_FULL = 0.8f

/**
 * The bar's title starts appearing only in the last 30% of the collapse, after the colour has
 * arrived. Earlier and it overlaps the header's own copy of the title while both are still legible;
 * later and it snaps in.
 */
private const val TITLE_FADE_FROM = 0.7f
