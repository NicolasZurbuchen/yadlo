package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.categoryColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.CategoryChipUiModel
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.programme_categories_all

/**
 * Tout · Musique · Silent Party · Sur l'eau — filtering by the kind of thing, which is the only
 * grouping axis the app has.
 *
 * Unselected chips carry a dot in their Category's colour and selected ones fill with it, so the
 * colour is stated before it has to be recognised. It never carries the meaning alone: the label
 * beside it is the Category's own French name, out of the content.
 *
 * *Tout* is not a Category — it is the absence of a filter, which is why deselecting the last chip
 * lands back on it rather than on an empty list.
 */
@Composable
fun CategoryChipRow(
    categories: List<CategoryChipUiModel>,
    onCategoryClick: (String) -> Unit,
    onAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val noneSelected = categories.none { it.isSelected }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.md),
        modifier = modifier.fillMaxWidth(),
    ) {
        item(key = ALL_CHIP_KEY) {
            CategoryChip(
                label = stringResource(Res.string.programme_categories_all),
                isSelected = noneSelected,
                fill = MaterialTheme.appColors.primary,
                ink = MaterialTheme.appColors.onPrimary,
                showDot = false,
                onClick = onAllClick,
            )
        }

        items(categories, key = { it.id }) { category ->
            val colors = MaterialTheme.categoryColors.forId(category.id)

            CategoryChip(
                label = category.name,
                isSelected = category.isSelected,
                fill = colors.fill,
                ink = colors.ink,
                showDot = true,
                onClick = { onCategoryClick(category.id) },
            )
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    fill: Color,
    ink: Color,
    showDot: Boolean,
    onClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .clip(MaterialTheme.shapes.small)
                .background(if (isSelected) fill else Color.Transparent)
                .border(
                    width = BORDER_WIDTH,
                    color = if (isSelected) fill else MaterialTheme.appColors.borderStrong,
                    shape = MaterialTheme.shapes.small,
                )
                .selectable(selected = isSelected, onClick = onClick)
                .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs),
    ) {
        // The dot says what the fill would say, while the chip is off. Once it is on, the whole chip
        // is that colour and a second swatch inside it repeats the statement.
        if (showDot && !isSelected) {
            Box(
                modifier =
                    Modifier
                        .size(DOT_SIZE)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(fill),
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) ink else MaterialTheme.appColors.textSecondary,
        )
    }
}

/** Stable, so the leading chip is never re-keyed onto a Category's id. */
private const val ALL_CHIP_KEY = "programme:all-categories"

private val BORDER_WIDTH = 1.dp
private val DOT_SIZE = 8.dp
