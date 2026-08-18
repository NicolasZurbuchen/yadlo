package io.nicolaszurbuchen.yadlo.app.design.component

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors

/**
 * One filter chip, wherever the app filters something.
 *
 * **The three chip rows in the app were three different controls.** The Programme's days were a
 * `Text` on a rounded background, its categories were a bordered `Row`, and the stands' dietary
 * marks were a Material `FilterChip` — three heights, three paddings, three label styles, on two
 * screens a visitor moves between. Anything that filters a list is this now, and the height comes
 * from Material rather than from whichever padding each one happened to be given.
 *
 * [leadingIcon] is always on the left and always optional: it is the Category's colour dot, or the
 * glyph for a dietary mark. A chip that has nothing to put there simply reads as its label.
 *
 * [selectedFill] and [selectedInk] exist because a Category chip fills with its own hue rather than
 * with the app's primary — the colour is the Category, and a selected *musique* chip that turned
 * blue would be saying something false. Everything else takes the defaults.
 *
 * [ink] and [outline] exist because the ground is no longer always the page. The Programme's filters
 * sit on the bandeau blue, where the page-ground roles do not survive: the outline measures 1.6:1
 * against it and the label 2.4:1. A chip on a coloured chrome passes the ink that chrome carries.
 *
 * [selectedOutline] defaults to the fill, which is the borderless Material look and the right one on
 * the page. On the chrome it becomes the same drawn edge the unselected chip has, because a filled
 * chip's boundary is otherwise the fill itself — and a Category fill on that blue is between 1.2:1
 * and 2.1:1, so the control would have no visible edge at all. The outline gives it one without
 * touching the hue, which is the part that carries the meaning.
 */
@Composable
fun YadloFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedFill: Color = MaterialTheme.appColors.primary,
    selectedInk: Color = MaterialTheme.appColors.onPrimary,
    ink: Color = MaterialTheme.appColors.textSecondary,
    outline: Color = MaterialTheme.appColors.borderStrong,
    selectedOutline: Color = selectedFill,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
            )
        },
        leadingIcon = leadingIcon,
        colors =
            FilterChipDefaults.filterChipColors(
                containerColor = Color.Transparent,
                labelColor = ink,
                selectedContainerColor = selectedFill,
                selectedLabelColor = selectedInk,
                selectedLeadingIconColor = selectedInk,
            ),
        border =
            FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = isSelected,
                borderColor = outline,
                selectedBorderColor = selectedOutline,
            ),
        modifier = modifier,
    )
}
