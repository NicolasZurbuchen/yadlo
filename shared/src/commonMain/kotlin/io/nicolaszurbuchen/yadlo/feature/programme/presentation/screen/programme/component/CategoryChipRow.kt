package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.design.component.YadloFilterChip
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.categoryColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
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
 *
 * On the chrome blue, with the chip's edge in the ink that blue carries. The dot is the one thing
 * here still measured against the page — 1.2:1 to 2.1:1 against this ground — and it stays because
 * it is a swatch beside a word that already says the same thing. See [ProgrammeHeader].
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
            YadloFilterChip(
                label = stringResource(Res.string.programme_categories_all),
                isSelected = noneSelected,
                onClick = onAllClick,
                container = MaterialTheme.appColors.primarySubtle,
                outline = MaterialTheme.appColors.onPrimarySubtle,
            )
        }

        items(categories, key = { it.id }) { category ->
            val colors = MaterialTheme.categoryColors.forId(category.id)

            YadloFilterChip(
                label = category.name,
                isSelected = category.isSelected,
                onClick = { onCategoryClick(category.id) },
                selectedFill = colors.fill,
                selectedInk = colors.ink,
                container = MaterialTheme.appColors.primarySubtle,
                outline = MaterialTheme.appColors.onPrimarySubtle,
                // The dot says what the fill would say, while the chip is off. Once it is on, the
                // whole chip is that colour and a second swatch inside it repeats the statement.
                leadingIcon =
                    if (category.isSelected) {
                        null
                    } else {
                        {
                            Box(
                                modifier =
                                    Modifier
                                        .size(DOT_SIZE)
                                        .clip(MaterialTheme.shapes.extraSmall)
                                        .background(colors.fill),
                            )
                        }
                    },
            )
        }
    }
}

/** Stable, so the leading chip is never re-keyed onto a Category's id. */
private const val ALL_CHIP_KEY = "programme:all-categories"

private val DOT_SIZE = 8.dp
