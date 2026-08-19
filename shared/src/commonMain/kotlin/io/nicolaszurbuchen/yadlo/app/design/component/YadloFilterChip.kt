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
 * [container] and [outline] exist because the ground is no longer always the page. A filter row on
 * the bandeau blue cannot inherit the page's roles: the outline measures 1.6:1 against that blue and
 * the label 2.4:1. Both rows on the chrome pass the ink the blue carries as their [outline], and
 * their own ground as [container], so the chip is the chrome and its edge is the one thing on it
 * that clears the chrome.
 *
 * What that leaves on the chrome is the [leadingIcon]. The Category dot and the dietary glyph are
 * both chosen against the *page* grounds, and on the blue the dot measures 1.2:1 to 2.1:1 and three
 * of the six dietary tints fall under the 3:1 floor. They are redundant with the label beside them
 * — nothing here is carried by colour alone — so this is a mark reading quieter than it does
 * elsewhere rather than a mark nobody can read. An earlier version filled [container] with the page
 * ground instead, which fixed the numbers and turned the row into white pills on blue.
 *
 * A selected chip has no edge of its own anywhere in the app: the border takes the fill's colour, so
 * what you see is a solid pill of the thing you picked. That is deliberately true on the chrome blue
 * as well, where the fill measures between 1.2:1 and 2.1:1 against the ground — a boundary the eye
 * finds by hue rather than by luminance. The alternative was a drawn edge on every selected chip,
 * which reads as a second state on top of the first and made a filter row look like a toolbar.
 */
@Composable
fun YadloFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedFill: Color = MaterialTheme.appColors.primary,
    selectedInk: Color = MaterialTheme.appColors.onPrimary,
    container: Color = Color.Transparent,
    outline: Color = MaterialTheme.appColors.borderStrong,
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
                containerColor = container,
                labelColor = MaterialTheme.appColors.textSecondary,
                selectedContainerColor = selectedFill,
                selectedLabelColor = selectedInk,
                selectedLeadingIconColor = selectedInk,
            ),
        border =
            FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = isSelected,
                borderColor = outline,
                selectedBorderColor = selectedFill,
            ),
        modifier = modifier,
    )
}
