package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloFactRow
import io.nicolaszurbuchen.yadlo.app.design.component.YadloLinkTile
import io.nicolaszurbuchen.yadlo.app.design.uimodel.FactMarkUiModel
import io.nicolaszurbuchen.yadlo.app.design.uimodel.LinkMarkUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering.component.VolunteeringSkeleton
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_entry_volunteering
import yadlo.shared.generated.resources.volunteering_section_act
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
 * **The two actions sit under a heading of their own.** They were left bare at the foot of the page
 * and read as loose ends rather than as the point of it — every other block on this screen announces
 * itself, and the one the reader is meant to act on was the one that did not.
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
        skeleton = { VolunteeringSkeleton() },
        modifier = modifier,
    ) {
        // The programme's own name as the heading, so the screen says "Hot'Staff" where the row
        // that opened it said what the row had to say in words anyone would recognise.
        state.name?.let { name ->
            PlusSection(title = name) {
                state.body?.let { PlusBodyText(text = it) }
            }
        }

        if (state.perks.isNotEmpty()) {
            PlusSection(title = stringResource(Res.string.volunteering_section_perks)) {
                state.perks.forEach { YadloFactRow(mark = FactMarkUiModel.CHECK, fact = it) }
            }
        }

        if (state.signupUrl != null || state.email != null) {
            PlusSection(title = stringResource(Res.string.volunteering_section_act)) {
                state.signupUrl?.let { url ->
                    YadloLinkTile(
                        label = stringResource(Res.string.volunteering_signup),
                        mark = LinkMarkUiModel.EXTERNAL,
                        onClick = { onSignupClick(url) },
                    )
                }

                state.email?.let { address ->
                    YadloLinkTile(
                        label = address,
                        mark = LinkMarkUiModel.MAIL,
                        onClick = { onEmailClick(address) },
                    )
                }
            }
        }
    }
}
