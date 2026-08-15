package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component.CategoryChipRow
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component.DayChipRow
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component.ProgrammeEmptyMessage
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component.SlotRow

/**
 * Layout B2: the day, the filters, then one chronological list.
 *
 * The two chip rows are chrome and stay put while the list scrolls under them — they are how you
 * change what the list is, so scrolling them away would mean scrolling back to change your mind.
 */
@Composable
fun ProgrammeScreen(
    state: ProgrammeUiModel,
    onDayClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onAllCategoriesClick: () -> Unit,
    onSlotClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (state.days.isNotEmpty()) {
            DayChipRow(days = state.days, onDayClick = onDayClick)
        }

        if (state.categories.isNotEmpty()) {
            CategoryChipRow(
                categories = state.categories,
                onCategoryClick = onCategoryClick,
                onAllClick = onAllCategoriesClick,
            )
        }

        if (state.emptyMessage != null) {
            ProgrammeEmptyMessage(message = state.emptyMessage)
            return@Column
        }

        LazyColumn(
            contentPadding = PaddingValues(vertical = MaterialTheme.spacing.sm),
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(state.rows, key = { _, row -> row.id }) { index, row ->
                // A hairline between neighbours, never around each of them: the shared left edge
                // and the common baseline are the whole reason this is a list of rows, not cards.
                if (index > 0) {
                    HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
                }

                SlotRow(row = row, onClick = onSlotClick)
            }
        }
    }
}
