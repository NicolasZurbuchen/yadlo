package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

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
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.contact_section_address
import yadlo.shared.generated.resources.contact_section_emails
import yadlo.shared.generated.resources.contact_signup
import yadlo.shared.generated.resources.plus_entry_contact

/**
 * *Nous écrire* — a router, not a form.
 *
 * Three marks say where each tap goes: `↗` to the association's own recruitment site, `✉` to the
 * visitor's own mail app, and nothing stays inside. That is the design rather than a limitation —
 * no backend, no stored messages, and their existing pipeline keeps receiving its applications
 * instead of landing in a personal inbox that has to forward them by hand in July.
 *
 * All nine addresses are listed under the labels the association wrote. Reducing them to four
 * concerns would be guessing at how a committee divides its work.
 */
@Composable
fun ContactScreen(
    state: ContactUiModel,
    onBackClick: () -> Unit,
    onEmailClick: (String) -> Unit,
    onSignupClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = stringResource(Res.string.plus_entry_contact),
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

        state.volunteering?.let { volunteering ->
            PlusSection(title = volunteering.name) {
                PlusBodyText(text = volunteering.body)

                volunteering.perks.forEach { PlusFactRow(mark = PERK_MARK, fact = it) }

                volunteering.signupUrl?.let { url ->
                    PlusLinkTile(
                        label = stringResource(Res.string.contact_signup),
                        mark = EXTERNAL_MARK,
                        onClick = { onSignupClick(url) },
                    )
                }

                volunteering.email?.let { address ->
                    PlusLinkTile(
                        label = address,
                        mark = MAIL_MARK,
                        onClick = { onEmailClick(address) },
                    )
                }
            }
        }

        if (state.emails.isNotEmpty()) {
            PlusSection(title = stringResource(Res.string.contact_section_emails)) {
                state.emails.forEach { email ->
                    PlusLinkTile(
                        label = email.label,
                        mark = MAIL_MARK,
                        onClick = { onEmailClick(email.address) },
                        sublabel = email.address,
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

private const val PERK_MARK = "✓"
private const val EXTERNAL_MARK = "↗"
private const val MAIL_MARK = "✉"
