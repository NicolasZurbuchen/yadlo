package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours.component.HoursSkeleton
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours.component.OpeningDayCard
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.hours_intro
import yadlo.shared.generated.resources.hours_title

/**
 * *Horaires* — deduced from the programme, never authored.
 *
 * A FestivalDay's window *is* the opening hours, so this screen needed no new content field and
 * could ship while the association had published nothing about times.
 *
 * **One time per day and nothing else.** It also printed the programme window under each day, plus
 * a note explaining that a 10:00 activity on a site opening at 12:00 is legitimate — both true, and
 * together they made a reader sort three facts to find the one they opened the screen for. The
 * domain still derives the programme instants; they come back the day somebody asks for them.
 */
@Composable
fun HoursScreen(
    state: HoursUiModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = stringResource(Res.string.hours_title),
        onBackClick = onBackClick,
        isLoading = state.isLoading,
        skeleton = { HoursSkeleton() },
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

        if (state.days.isNotEmpty()) {
            PlusBodyText(text = stringResource(Res.string.hours_intro))
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier.fillMaxWidth(),
        ) {
            state.days.forEach { OpeningDayCard(day = it) }
        }

        state.caveat?.let { caveat ->
            Text(
                text = caveat.asString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.appColors.textTertiary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
