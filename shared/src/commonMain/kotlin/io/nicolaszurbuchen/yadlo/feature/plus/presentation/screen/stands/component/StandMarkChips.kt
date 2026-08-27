package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.design.component.YadloFilterChip
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloDietaryMarkUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandChipUiModel
import io.nicolaszurbuchen.yadlo.infra.text.asString

/**
 * The dietary filter — *Tout*, then every mark that any dish on any stand actually carries.
 *
 * The set is derived from the listing rather than declared, so a chip is never offered that matches
 * nothing and a mark the content adds appears without an app release.
 *
 * **Each chip wears its own glyph and its own colour**, the same pair the tags under a stand's name
 * and on every dish use. A reader who has learnt that the leaf means vegan on a menu should not have
 * to learn a second time that this grey word up here means the same thing.
 *
 * **Selected fills with the mark's colour**, the way a Category chip fills with its hue on the
 * Programme, and the glyph stays — turned to the ink that colour carries so it is still there rather
 * than dissolving into its own background. *Tout* has no mark of its own and fills with the app's
 * primary.
 *
 * **On the bar's blue, with the chip's edge in the ink that blue carries.** The glyphs are the one
 * thing here still measured against the page grounds, where they are also spent on the dish tags and
 * the stand rows: on this blue, vegetarian is 2.1:1, gluten-free 2.7:1 and dairy-free 2.8:1 against
 * a 3:1 floor. Every one of them sits beside its own word, so what this costs is three marks reading
 * quieter here than they do on a fiche — not a mark that cannot be read.
 *
 * Horizontally scrolling rather than wrapped, so the row keeps its height whatever the content
 * publishes. There are seven chips today and the same layout survives fifteen. The inset is inside
 * the scroll rather than around it, so the chips run to both screen edges instead of stopping short
 * of them — a row that scrolls but visibly cannot reach the edge looks broken rather than long.
 */
@Composable
fun StandMarkChips(
    chips: List<StandChipUiModel>,
    onMarkClick: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier =
            modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = MaterialTheme.spacing.md),
    ) {
        chips.forEach { chip ->
            val mark = chip.mark?.let(YadloDietaryMarkUiModel::forId)

            YadloFilterChip(
                label = chip.label.asString(),
                isSelected = chip.isSelected,
                onClick = { onMarkClick(chip.mark) },
                selectedFill = mark?.tint ?: MaterialTheme.appColors.primary,
                selectedInk = mark?.ink ?: MaterialTheme.appColors.onPrimary,
                container = MaterialTheme.appColors.primarySubtle,
                outline = MaterialTheme.appColors.onPrimarySubtle,
                leadingIcon =
                    mark?.let {
                        {
                            Icon(
                                imageVector = it.icon,
                                // The label is right beside it. A description here has a screen
                                // reader announce every chip twice.
                                contentDescription = null,
                                tint = if (chip.isSelected) it.ink else it.tint,
                                modifier = Modifier.size(GLYPH_SIZE),
                            )
                        }
                    },
            )
        }
    }
}

// Matched to the chip label's own line height rather than Material's 18dp chip-icon default: the
// glyph qualifies the word beside it and should not outweigh it.
private val GLYPH_SIZE = 14.dp
