package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloLinkTile
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.uimodel.LinkMarkUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact.component.ContactSkeleton
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.contact_section_address
import yadlo.shared.generated.resources.contact_section_emails
import yadlo.shared.generated.resources.plus_entry_contact

/**
 * *Nous écrire* — a router, not a form.
 *
 * Every tile carries the mail mark and nothing stays inside. That is the design rather than a
 * limitation — no backend, no stored messages, and the association's own inboxes keep receiving
 * their own mail instead of it landing somewhere that has to forward it by hand in July.
 *
 * All nine addresses are listed under the labels the association wrote. Reducing them to four
 * concerns would be guessing at how a committee divides its work.
 *
 * **The name of whoever is behind an address goes on the tile.** Writing to *Programmation musicale*
 * is writing into a role; writing to Jeremy B. is writing to somebody, and for a committee of
 * volunteers that difference is most of whether a reply happens. It sits under the label with the
 * address, because the label is what a reader scans and the name is what they read once they have
 * stopped.
 *
 * Recruiting used to open from here and now has its own row on the tab — see
 * [io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering.VolunteeringScreen].
 */
@Composable
fun ContactScreen(
    state: ContactUiModel,
    onBackClick: () -> Unit,
    onEmailClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = stringResource(Res.string.plus_entry_contact),
        onBackClick = onBackClick,
        isLoading = state.isLoading,
        skeleton = { ContactSkeleton() },
        modifier = modifier,
    ) {
        if (state.emails.isNotEmpty()) {
            PlusSection(title = stringResource(Res.string.contact_section_emails)) {
                state.emails.forEach { email ->
                    YadloLinkTile(
                        label = email.label,
                        mark = LinkMarkUiModel.MAIL,
                        onClick = { onEmailClick(email.address) },
                        sublabel = listOfNotNull(email.responsible, email.address).joinToString(SUBLABEL_SEPARATOR),
                    )
                }
            }
        }

        state.address?.let { address ->
            PlusSection(title = stringResource(Res.string.contact_section_address)) {
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

// A middle dot rather than a dash: the two halves are a person and an address, not a phrase and its
// continuation, and a hyphen between "Jeremy B." and "musique@yadlo.ch" reads as part of the address.
private const val SUBLABEL_SEPARATOR = " · "
