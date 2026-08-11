package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HappeningRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HappeningScreen(
        onBackClick = onNavigateBack,
        modifier = modifier,
    )
}
