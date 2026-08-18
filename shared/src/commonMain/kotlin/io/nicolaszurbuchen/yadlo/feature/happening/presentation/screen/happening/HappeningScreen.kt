package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import io.nicolaszurbuchen.yadlo.app.design.component.YadloDietaryTags
import io.nicolaszurbuchen.yadlo.app.design.component.YadloFactRow
import io.nicolaszurbuchen.yadlo.app.design.component.YadloLinkTile
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.categoryColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.app.design.uimodel.FactMarkUiModel
import io.nicolaszurbuchen.yadlo.app.design.uimodel.LinkMarkUiModel
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
import yadlo.shared.generated.resources.happening_back
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
 * The toolbar is transparent over the header and takes the Category colour once the title has
 * scrolled under it, which is the collapse the prototype describes. The title rises into the bar at
 * the same moment. The status bar is not tinted with it — that is a system-window concern the app
 * shell has not taken on yet, and doing it from one screen would leave the other four inconsistent.
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

    // The header is the first item, so "the title has gone under the bar" is exactly "we are no
    // longer looking at the top of item zero".
    val isCollapsed by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                listState.firstVisibleItemScrollOffset > COLLAPSE_THRESHOLD_PX
        }
    }

    val barColor by animateColorAsState(if (isCollapsed) category.fill else Color.Transparent)
    val barTitleAlpha by animateFloatAsState(if (isCollapsed) 1f else 0f)
    val barInk = if (isCollapsed) category.ink else MaterialTheme.appColors.textPrimary

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
                            contentDescription = stringResource(Res.string.happening_back),
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
                    // The header runs under the transparent bar on purpose, so the blob and the
                    // Category label start at the top of the screen rather than below a gap.
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item(key = "header") {
                        HappeningHeader(
                            categoryId = state.categoryId,
                            categoryLabel = state.categoryLabel,
                            title = state.title,
                            modifier = Modifier.padding(top = contentPadding.calculateTopPadding()),
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
                                            mark = LinkMarkUiModel.EXTERNAL,
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
                                    YadloFactRow(mark = FactMarkUiModel.CHECK, fact = fact.asString())
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
                                        mark = LinkMarkUiModel.EXTERNAL,
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
 * A third of the header's minimum height in pixels at a typical density — far enough into the scroll
 * that a thumb resting on the list does not flicker the bar, close enough that the bar has taken its
 * colour before the title would otherwise slide under it unannounced.
 */
private const val COLLAPSE_THRESHOLD_PX = 140
