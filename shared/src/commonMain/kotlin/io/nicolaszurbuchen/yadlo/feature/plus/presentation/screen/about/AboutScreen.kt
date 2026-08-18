package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloLinkTile
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloLinkMarkUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.about_author_email
import yadlo.shared.generated.resources.about_author_name
import yadlo.shared.generated.resources.about_body
import yadlo.shared.generated.resources.about_section_author
import yadlo.shared.generated.resources.about_section_version
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
 * **The author's own address, not the festival's.** *Nous écrire* routes to the association's nine
 * inboxes, and a question about this app is the one thing none of them is for — sending "your app
 * shows the wrong stage times" to `musique@` reaches nine volunteers who did not build it. This is
 * the only place in the app that opens a mail to somebody who is not the festival, and it is the
 * reason the last line of *Pourquoi elle existe* no longer points at *Nous écrire*.
 *
 * [AboutUiModel.version] sits above it rather than below, because it is the thing the reader is
 * meant to have already read by the time they tap the address.
 */
@Composable
fun AboutScreen(
    state: AboutUiModel,
    onBackClick: () -> Unit,
    onEmailClick: (String) -> Unit,
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

        PlusSection(title = stringResource(Res.string.about_section_version)) {
            PlusBodyText(text = state.version)
        }

        PlusSection(title = stringResource(Res.string.about_section_author)) {
            val address = stringResource(Res.string.about_author_email)

            YadloLinkTile(
                label = stringResource(Res.string.about_author_name),
                mark = YadloLinkMarkUiModel.MAIL,
                onClick = { onEmailClick(address) },
                sublabel = address,
            )
        }
    }
}
