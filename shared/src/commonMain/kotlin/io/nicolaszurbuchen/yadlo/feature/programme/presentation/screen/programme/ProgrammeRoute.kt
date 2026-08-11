package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ProgrammeRoute(
    onNavigateToHappening: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ProgrammeScreen(
        onHappeningClick = onNavigateToHappening,
        modifier = modifier,
    )
}
