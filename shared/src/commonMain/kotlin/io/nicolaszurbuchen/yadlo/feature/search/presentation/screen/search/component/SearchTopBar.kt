package io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloSearchField
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.back
import yadlo.shared.generated.resources.search_placeholder

/**
 * A way back and the field, on one line.
 *
 * **No title, and that is the point.** Every other detail screen in the app names itself in the bar,
 * because the name is what tells you where you are. Here the field says it better than a heading
 * could — *Rechercher dans tout le festival* is both the label and the scope — and a *Recherche*
 * above it would be the same word twice on two consecutive lines. This is also the screen where a
 * row of chrome costs the most: it is one text field and a list, and the list is the answer.
 *
 * It wears [YadloTopAppBar]'s blue and its insets rather than the bar itself, because the bar's
 * title slot takes a `String` and what belongs there is a control.
 *
 * The keyboard is up on arrival. Nobody opens this screen to look at it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .background(MaterialTheme.appColors.primarySubtle)
                .windowInsetsPadding(TopAppBarDefaults.windowInsets)
                .padding(
                    end = MaterialTheme.spacing.md,
                    top = MaterialTheme.spacing.sm,
                    bottom = MaterialTheme.spacing.sm,
                ),
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.back),
                tint = MaterialTheme.appColors.onPrimarySubtle,
            )
        }

        YadloSearchField(
            value = query,
            placeholder = stringResource(Res.string.search_placeholder),
            onValueChange = onQueryChange,
            autoFocus = true,
            modifier = Modifier.weight(1f),
        )
    }
}
