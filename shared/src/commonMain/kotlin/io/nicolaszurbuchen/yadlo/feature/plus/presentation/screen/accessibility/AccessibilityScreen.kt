package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.accessibility

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
import yadlo.shared.generated.resources.accessibility_contact_body
import yadlo.shared.generated.resources.accessibility_contact_hint
import yadlo.shared.generated.resources.accessibility_intro
import yadlo.shared.generated.resources.accessibility_nothing_published
import yadlo.shared.generated.resources.accessibility_section_available
import yadlo.shared.generated.resources.accessibility_section_contact
import yadlo.shared.generated.resources.accessibility_section_unavailable
import yadlo.shared.generated.resources.accessibility_title

/**
 * *Accessibilité* — and today, mostly an admission and an address.
 *
 * **The missing data is the page.** Separating what is confirmed from what is confirmed absent is
 * more useful than a page that reassures, because nobody travels on the strength of "site
 * accessible" and the specific "no adapted toilets" is what actually decides the journey. When both
 * lists are empty, as they are today, the most useful thing left is somebody to write to — so that
 * is what the screen becomes rather than an apology.
 *
 * The list of questions still open — step-free routes, a viewing spot at the stage, assistance dogs
 * — lives in content/GAPS.md, addressed to the association. Rendering it here would put French
 * content in Kotlin and need an app release to remove each line as it gets answered.
 */
@Composable
fun AccessibilityScreen(
    state: AccessibilityUiModel,
    onBackClick: () -> Unit,
    onContactClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = stringResource(Res.string.accessibility_title),
        onBackClick = onBackClick,
        isLoading = state.isLoading,
        modifier = modifier,
    ) {
        state.emptyMessage?.let { message ->
            Text(
                text = message.asString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.appColors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (state.nothingPublished) {
            PlusBodyText(text = stringResource(Res.string.accessibility_nothing_published))
        } else if (state.available.isNotEmpty() || state.unavailable.isNotEmpty()) {
            PlusBodyText(text = stringResource(Res.string.accessibility_intro))
        }

        if (state.available.isNotEmpty()) {
            PlusSection(title = stringResource(Res.string.accessibility_section_available)) {
                state.available.forEach { fact ->
                    PlusFactRow(mark = AVAILABLE_MARK, fact = fact.note?.let { "${fact.name} — $it" } ?: fact.name)
                }
            }
        }

        if (state.unavailable.isNotEmpty()) {
            PlusSection(title = stringResource(Res.string.accessibility_section_unavailable)) {
                state.unavailable.forEach { fact ->
                    PlusFactRow(mark = UNAVAILABLE_MARK, fact = fact.note?.let { "${fact.name} — $it" } ?: fact.name)
                }
            }
        }

        state.contactEmail?.let { email ->
            PlusSection(title = stringResource(Res.string.accessibility_section_contact)) {
                PlusBodyText(text = stringResource(Res.string.accessibility_contact_body))

                PlusLinkTile(
                    label = email,
                    mark = MAIL_MARK,
                    onClick = { onContactClick(email) },
                    // A hint rather than a prefilled subject: the app opens a blank mail and the
                    // reader writes it, which needs no accent percent-encoded on two platforms.
                    sublabel = stringResource(Res.string.accessibility_contact_hint),
                )
            }
        }
    }
}

private const val AVAILABLE_MARK = "✓"
private const val UNAVAILABLE_MARK = "✕"

/** `✉` opens mail, per SPEC.md § Interaction rules. */
private const val MAIL_MARK = "✉"
