package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.about_body
import yadlo.shared.generated.resources.about_section_why
import yadlo.shared.generated.resources.about_why
import yadlo.shared.generated.resources.plus_entry_about

/**
 * *À propos de cette app* — unofficial, who built it, and why it exists.
 *
 * **This is the screen a committee member will find**, which makes it the app's inbound channel
 * rather than a formality. It says plainly that the app is not the association's, because the
 * alternative is someone discovering that later and reasonably concluding they were misled — and
 * because being straightforwardly unofficial is what makes the offer to become official readable
 * as an offer.
 *
 * Deliberately no version number. Nothing here changes behaviour by build, the update path is a
 * soft row driven by `minSupportedAppVersion`, and a version string is the kind of thing that goes
 * stale in a screenshot rather than being useful in a conversation.
 */
@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = stringResource(Res.string.plus_entry_about),
        onBackClick = onBackClick,
        isLoading = false,
        modifier = modifier,
    ) {
        PlusBodyText(text = stringResource(Res.string.about_body))

        PlusSection(title = stringResource(Res.string.about_section_why)) {
            PlusBodyText(text = stringResource(Res.string.about_why))
        }
    }
}
