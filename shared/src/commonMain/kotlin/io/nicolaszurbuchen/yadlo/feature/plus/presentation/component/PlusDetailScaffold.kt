package io.nicolaszurbuchen.yadlo.feature.plus.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.ShimmerPulse
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * [PlusScreenScaffold] plus the one scrolling column most of these screens are.
 *
 * Shared rather than repeated because there are a dozen of these and the frame is the part with no
 * decisions left in it. What each screen still owns is everything inside — a payment screen and a
 * timetable have nothing else in common, which is why this stops at the scroll container rather
 * than trying to be a page template as well. A screen whose body is a long lazy list wears the bar
 * directly instead.
 *
 * [isLoading] is the same content-arriving state every Plus screen has, since all of them read one
 * bundle that lands once at launch.
 *
 * **[skeleton] is a slot rather than a spinner**, and that is the one thing about waiting the frame
 * does *not* decide for the screen. A centred `CircularProgressIndicator` is the same picture on
 * twelve screens that look nothing alike; what each of them actually knows is its own shape, and
 * drawing that means the real content arrives into a layout the eye has already settled on. Most
 * pages are prose under headings and take [PlusPageSkeleton] without saying so; the ones whose
 * shape is genuinely different pass their own.
 *
 * It is drawn inside the same padded scroll column as the content, so the placeholder sits exactly
 * where the thing it stands in for will, and it is wrapped in one [ShimmerPulse] so every block on
 * the screen breathes off a single animated value.
 */
@Composable
fun PlusDetailScaffold(
    title: String,
    onBackClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    skeleton: @Composable ColumnScope.() -> Unit = { PlusPageSkeleton() },
    content: @Composable ColumnScope.() -> Unit,
) {
    PlusScreenScaffold(
        title = title,
        onBackClick = onBackClick,
        actions = actions,
        modifier = modifier,
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = MaterialTheme.spacing.md,
                        end = MaterialTheme.spacing.md,
                        top = MaterialTheme.spacing.md,
                        bottom = MaterialTheme.spacing.xl,
                    ),
        ) {
            if (isLoading) {
                // The receiver is carried in by hand because ShimmerPulse takes a plain content
                // lambda: it provides one value to everything beneath it and has no business
                // knowing it is inside a Column.
                val columnScope = this
                ShimmerPulse { columnScope.skeleton() }
            } else {
                content()
            }
        }
    }
}
