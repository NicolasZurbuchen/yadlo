package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.categoryColors
import io.nicolaszurbuchen.yadlo.app.design.theme.sizing
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.CatalogueCardUiModel
import org.jetbrains.compose.resources.painterResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.img_placeholder

/**
 * One Happening in the Catalogue. Opens the fiche, which is where its dates and its heart live.
 *
 * **Deliberately the Stand card's three bands** — the photograph, then what it is and what it is
 * called, then the attributes behind a rule (DECISIONS.md § A Stand is a photograph). A visitor who
 * has just browsed *Nourriture & boissons* is doing the same thing here, and giving the two lists
 * two shapes would say they were different activities.
 *
 * **No hours, no day, no live-state pill, and no heart.** Those all belong to a Slot, and this is a
 * Happening — the thing the Slots point at. The card carrying "vendredi · samedi · dimanche" was
 * considered and dropped: it is a timetable fact on the view whose whole definition is not being
 * one, and the fiche one tap away lists every date with a heart against each, which is where a
 * decision about a date belongs anyway.
 *
 * **The Category is written above the name, in the colour of its chip.** This is the only browse
 * list in the app that mixes Categories, so the card has to say which one it is — and the word
 * survives what the colour does not, in July sun on a phone at 30%.
 *
 * The description is clamped rather than shortened in the mapper. The edition's run from 41 to 595
 * characters with no authored short form, so three lines is where a biography stops being an
 * introduction and starts being a page; the fiche has the rest.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CatalogueCard(
    entry: CatalogueCardUiModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // The same bundled photograph of the site the fiche falls back to, for a null url and a failed
    // load alike: on a beach with one bar of signal they are the same fact.
    val placeholder = painterResource(Res.drawable.img_placeholder)
    val category = MaterialTheme.categoryColors.forId(entry.categoryId)

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.appColors.surface)
                .clickable { onClick(entry.id) },
    ) {
        AsyncImage(
            model = entry.imageUrl,
            // The name is written directly under it, so a description here says it twice.
            contentDescription = null,
            contentScale = ContentScale.Crop,
            fallback = placeholder,
            error = placeholder,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(IMAGE_RATIO)
                    .background(MaterialTheme.appColors.surfaceRaised),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.md),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(MaterialTheme.sizing.categoryMark)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(category.fill),
                )

                Text(
                    text = entry.categoryName.uppercase(),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.appColors.textTertiary,
                    modifier = Modifier.weight(1f),
                )
            }

            Text(
                text = entry.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.appColors.textPrimary,
            )

            entry.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.appColors.textSecondary,
                    maxLines = DESCRIPTION_MAX_LINES,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Absent rather than empty, which is most of the Activities: a rule under a name with
        // nothing below it reads as content that failed to load.
        if (entry.genres.isNotEmpty()) {
            HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = MaterialTheme.spacing.md,
                            vertical = MaterialTheme.spacing.sm,
                        ),
            ) {
                entry.genres.forEach { genre ->
                    Text(
                        text = genre.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.appColors.textSecondary,
                        modifier =
                            Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.appColors.surfaceRaised)
                                .padding(horizontal = MaterialTheme.spacing.sm, vertical = TAG_VERTICAL_PADDING),
                    )
                }
            }
        }
    }
}

/**
 * Three by two, the same frame the Stand card takes and for the same reason: every photograph in
 * the bank is four by three, so a wider frame is a centre crop that throws away the top and bottom
 * of the picture.
 */
private const val IMAGE_RATIO = 3f / 2f

/**
 * Three, which is where an artist biography stops introducing and starts explaining.
 *
 * The edition's descriptions run from 41 to 595 characters. Two lines cut *Diggin'* before the
 * words "indie-soul"; four turns the taller cards into a wall of grey and pushes the card below
 * them off the screen. The fiche carries the whole thing, one tap away.
 */
private const val DESCRIPTION_MAX_LINES = 3

/** Below the smallest spacing step, for the same reason as the live-state pill: a tag is not a button. */
private val TAG_VERTICAL_PADDING = 4.dp
