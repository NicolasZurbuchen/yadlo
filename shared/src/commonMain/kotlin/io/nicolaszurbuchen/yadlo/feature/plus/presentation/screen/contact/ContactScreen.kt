package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusEmailTile
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
 * The tile is [PlusEmailTile], shared with *Devenir Hot'Staff*: whichever screen a reader reaches
 * an address through, it names the concern, whoever is behind it, and where the mail goes.
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
                    PlusEmailTile(email = email, onClick = onEmailClick)
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
