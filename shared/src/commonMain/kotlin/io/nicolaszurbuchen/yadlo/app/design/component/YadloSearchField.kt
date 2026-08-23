package io.nicolaszurbuchen.yadlo.app.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.search_clear

/**
 * The search field, and the thing on Accueil that looks exactly like it.
 *
 * **One component with two modes, because they have to be indistinguishable.** Accueil carries the
 * block that teaches the app has a search; the search screen carries the field you type into. If
 * those were two composables they would drift, and the whole argument for the Accueil block is that
 * a reader recognises the field again when the magnifier in the toolbar opens it. Passing [onClick]
 * makes this a button dressed as a field: it draws the same shape and the same placeholder, opens
 * the real one, and never takes a keystroke of its own.
 *
 * That is also what keeps *two doors, one implementation* literally true. A real editable field on
 * Accueil would mean two search states, two keyboards, and a decision about what happens to text
 * typed in the one that is not the search screen.
 *
 * **The placeholder is where the scope is stated.** *Rechercher dans tout le festival*, not
 * *Rechercher* — a magnifier that lives in a toolbar shared by four tabs cannot say on its own that
 * it is not scoped to the tab under it, and the five words that do say it are read in the one place
 * the reader is certainly looking. The grouped results say it a second time, by answering with
 * headings from places they did not come from.
 *
 * The clear button only exists in the editable mode, and only with something to clear: an X on an
 * empty field is a control that does nothing, and on the Accueil block it would be a second target
 * on something that is already one big button.
 */
@Composable
fun YadloSearchField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    autoFocus: Boolean = false,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(autoFocus) {
        if (autoFocus && onClick == null) focusRequester.requestFocus()
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = FIELD_HEIGHT)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.appColors.surface)
                .then(if (onClick == null) Modifier else Modifier.clickable { onClick() })
                .padding(horizontal = MaterialTheme.spacing.md),
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            // Decorative in both modes: the placeholder beside it says the same thing in words, and
            // in the button mode the whole row is one target that a reader already hears named.
            contentDescription = null,
            tint = MaterialTheme.appColors.textTertiary,
            modifier = Modifier.size(ICON_SIZE),
        )

        if (onClick != null) {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.appColors.textTertiary,
                modifier = Modifier.weight(1f),
            )
        } else {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.appColors.textPrimary),
                cursorBrush = SolidColor(MaterialTheme.appColors.textPrimary),
                // Search rather than Done, and no action wired to it: the results are already live
                // on every keystroke, so the key's only job is to put the keyboard away.
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                decorationBox = { field ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.appColors.textTertiary,
                        )
                    }

                    field()
                },
                modifier = Modifier.weight(1f).focusRequester(focusRequester),
            )

            if (value.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(Res.string.search_clear),
                    tint = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier.size(ICON_SIZE).clickable { onValueChange("") },
                )
            }
        }
    }
}

/**
 * The same 48 a Material touch target is, which is what the Accueil block has to be to be tapped and
 * what the field wants anyway to sit a line of `bodyLarge` in comfortably.
 */
private val FIELD_HEIGHT = 48.dp

/** Matched to the label's line height rather than Material's 24, the same call [YadloEntryCard] makes. */
private val ICON_SIZE = 20.dp
