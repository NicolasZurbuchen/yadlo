package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.privacy

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusFactRow
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_entry_privacy
import yadlo.shared.generated.resources.privacy_body
import yadlo.shared.generated.resources.privacy_fact_no_account
import yadlo.shared.generated.resources.privacy_fact_no_analytics
import yadlo.shared.generated.resources.privacy_fact_plan_is_local
import yadlo.shared.generated.resources.privacy_section_facts

/**
 * *Confidentialité* — required at store submission even for an app that sends nothing.
 *
 * It is short because the truth is short: the app fetches three public JSON files and writes the
 * visitor's Plan to their own device. Saying that in four lines is more useful than a page of
 * boilerplate, and it is also the only version of this document that stays true without anyone
 * remembering to update it.
 */
@Composable
fun PrivacyScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = stringResource(Res.string.plus_entry_privacy),
        onBackClick = onBackClick,
        isLoading = false,
        modifier = modifier,
    ) {
        PlusBodyText(text = stringResource(Res.string.privacy_body))

        PlusSection(title = stringResource(Res.string.privacy_section_facts)) {
            PlusFactRow(mark = FACT_MARK, fact = stringResource(Res.string.privacy_fact_no_account))
            PlusFactRow(mark = FACT_MARK, fact = stringResource(Res.string.privacy_fact_no_analytics))
            PlusFactRow(mark = FACT_MARK, fact = stringResource(Res.string.privacy_fact_plan_is_local))
        }
    }
}

private const val FACT_MARK = "✓"
