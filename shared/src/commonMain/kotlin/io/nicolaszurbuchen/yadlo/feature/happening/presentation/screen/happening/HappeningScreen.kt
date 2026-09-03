package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.design.component.SocialLinksRow
import io.nicolaszurbuchen.yadlo.design.component.YadloDietaryTags
import io.nicolaszurbuchen.yadlo.design.component.YadloFactRow
import io.nicolaszurbuchen.yadlo.design.component.YadloLinkTile
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.categoryColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloFactMarkUiModel
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloLinkMarkUiModel
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component.HappeningHeader
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component.HappeningMenuGroupBlock
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component.HappeningPriceBlock
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component.HappeningSection
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component.HappeningSkeleton
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component.HappeningSlotRow
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component.HappeningTagRow
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component.SavedHeart
import io.nicolaszurbuchen.yadlo.infra.text.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.back
import yadlo.shared.generated.resources.happening_missing
import yadlo.shared.generated.resources.happening_section_good_to_know
import yadlo.shared.generated.resources.happening_section_links
import yadlo.shared.generated.resources.happening_section_price
import yadlo.shared.generated.resources.happening_section_when
import yadlo.shared.generated.resources.share
import yadlo.shared.generated.resources.wishlist_add
import yadlo.shared.generated.resources.wishlist_remove

/**
 * One template for an Artist, an Activity and a Stand — DECISIONS.md § One fiche template for
 * everything. Which sections appear is decided by which lists are non-empty, never by the kind of
 * Happening, so a Stand that publishes no menu and an Activity that costs nothing degrade the same
 * quiet way.
 *
 * The head is a photograph on every fiche, the bundled one where the content has none — see
 * [HappeningHeader].
 *
 * **The Category's colour closes over the whole head, not just over the toolbar**, and the header
 * is what paints it. The bar stays clear until that veil is already solid, then takes over as the
 * header leaves. Tinting both at once composited one colour twice — `1 - (1 - t)²` rather than `t` —
 * which drew the toolbar's own outline across the photograph in the same hue. The handoff is
 * invisible because it happens entirely over pixels that are already opaque Category colour.
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
 * **The bar's ground is drawn here rather than handed to [TopAppBar].** Material3 runs its own
 * container colour through `animateColorAsState` with a spring, which is right for a colour that
 * changes at a threshold and wrong for one that is already a function of the scroll: on a fling the
 * spring cannot keep up, so the bar stayed part-transparent for a few hundred milliseconds after the
 * header had stopped covering it and the page showed through. Slowly, or stopped, the spring caught
 * up and nothing was visible — which is exactly the shape of the bug. The container is transparent
 * and the colour is a background modifier, so it is the scroll position and nothing else.
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
    onShareClick: () -> Unit,
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
    val barColor =
        category.fill.copy(
            alpha = ((collapseProgress - BAR_TAKEOVER_FROM) / (1f - BAR_TAKEOVER_FROM)).coerceIn(0f, 1f),
        )
    // The icons stand on the header's scrim while it is there and on the Category's own ink once
    // the veil has closed, and they travel between the two as the colour arrives.
    val barInk = lerp(MaterialTheme.appColors.onScrim, category.ink, tint)
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
                // Only a Stand has a heart here, because only a Stand is kept whole — everything
                // else keeps its Slots one at a time, on the date rows below. Share is on all
                // three, which is what finally gives this slot a job on an artist and an activity.
                actions = {
                    // Share sits left of the heart rather than right of it. The heart is already
                    // in the corner on a Stand fiche and is the action people come back for;
                    // moving it to make room for a newcomer would cost a habit to save nothing.
                    if (state.shareText != null) {
                        IconButton(onClick = onShareClick) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = stringResource(Res.string.share),
                                tint = barInk,
                            )
                        }
                    }

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
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        navigationIconContentColor = barInk,
                        titleContentColor = barInk,
                        actionIconContentColor = barInk,
                    ),
                modifier = Modifier.background(barColor),
            )
        },
        modifier = modifier,
    ) { contentPadding ->
        when {
            state.isLoading -> {
                // No contentPadding: the photograph runs under the transparent bar, exactly as the
                // real header does, so the two do not start at different heights.
                HappeningSkeleton()
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
                            happeningId = state.id,
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
                            // Each group is a section of the fiche in its own right — *Plats*,
                            // *Boissons* — rather than a sub-heading under one *Au menu*. Two levels
                            // of heading over a carte of fourteen dishes was one more than it needs,
                            // and the outer one only ever said what the dishes underneath already do.
                            Column(
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
                                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                            ) {
                                state.menu.forEach { group ->
                                    HappeningSection(title = group.name) {
                                        HappeningMenuGroupBlock(group = group)
                                    }
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
                                // The footer's row, left-aligned — the same marks Accueil and Plus
                                // end on. A column of full-width tiles gave an artist's five links
                                // more of the fiche than its description, and said nothing the mark
                                // does not: nobody needs the word "Instagram" written beside the
                                // Instagram glyph, and five rows of chevron are five promises that
                                // the tap goes somewhere different each time.
                                SocialLinksRow(
                                    items = state.links,
                                    onSocialClick = onLinkClick,
                                    start = true,
                                )
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
 * Where the bar starts painting the colour itself, having left it to the header until then.
 *
 * Safely after [TINT_FULL], which is the whole point: over this stretch the header behind the bar is
 * already solid Category colour, so the bar fading in over it changes nothing that can be seen. By
 * the end of the travel the bar is opaque, which is exactly when the header stops covering it — the
 * two are the same instant, and the ramp exists only so a frame's rounding cannot land between them.
 */
private const val BAR_TAKEOVER_FROM = 0.85f

/**
 * The bar's title starts appearing only once the header's own copy has gone under the veil, which is
 * at [TINT_FULL] exactly — the two are never both on screen. A fiche showing its title twice, in two
 * sizes ten points apart, is the one thing about this collapse a reader would call a bug.
 *
 * It fades in over solid Category colour rather than over the photograph, because the veil is opaque
 * by then and the bar's own container has not started yet.
 */
private const val TITLE_FADE_FROM = TINT_FULL
