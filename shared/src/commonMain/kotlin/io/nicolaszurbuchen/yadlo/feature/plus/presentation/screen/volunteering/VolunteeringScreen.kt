package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusFactRow
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusLinkTile
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.uimodel.PlusMarkUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_entry_volunteering
import yadlo.shared.generated.resources.volunteering_section_perks
import yadlo.shared.generated.resources.volunteering_signup

/**
 * *Devenir Hot'Staff* — the commitment, then what it earns, then the two ways to act.
 *
 * **Its own screen rather than a section of *Nous écrire*.** Recruiting is the single thing in
 * *S'impliquer* the association is campaigning for, and putting it behind a row called "write to us"
 * meant it was found only by someone who had already decided to send an email. The website gives it
 * the same emphasis, for the same reason.
 *
 * What is asked comes before what is offered. Six hours is the fact that decides it, and a list of
 * perks read first would make the ask read like small print underneath.
 *
 * The signup link leaves for the association's own recruitment site. There is no form here and
 * there will not be one — their pipeline keeps receiving its applications instead of a personal
 * inbox forwarding them by hand during the busiest month of their year.
 */
@Composable
fun VolunteeringScreen(
    state: VolunteeringUiModel,
    onBackClick: () -> Unit,
    onSignupClick: (String) -> Unit,
    onEmailClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = stringResource(Res.string.plus_entry_volunteering),
        onBackClick = onBackClick,
        isLoading = state.isLoading,
        modifier = modifier,
    ) {
        state.emptyMessage?.let {
            Text(
                text = it.asString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.appColors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // The programme's own name as the heading, so the screen says "Hot'Staff" where the row
        // that opened it said what the row had to say in words anyone would recognise.
        state.name?.let { name ->
            PlusSection(title = name) {
                state.body?.let { PlusBodyText(text = it) }
            }
        }

        if (state.perks.isNotEmpty()) {
            PlusSection(title = stringResource(Res.string.volunteering_section_perks)) {
                state.perks.forEach { PlusFactRow(mark = PERK_MARK, fact = it) }
            }
        }

        state.signupUrl?.let { url ->
            PlusLinkTile(
                label = stringResource(Res.string.volunteering_signup),
                mark = PlusMarkUiModel.EXTERNAL,
                onClick = { onSignupClick(url) },
            )
        }

        state.email?.let { address ->
            PlusLinkTile(
                label = address,
                mark = PlusMarkUiModel.MAIL,
                onClick = { onEmailClick(address) },
            )
        }
    }
}

// Not coloured, for the reason PlusFactRow gives: polarity is carried by the glyph and the section
// it sits under, never by colour alone.
private const val PERK_MARK = "✓"
