package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.shimmerBlock
import io.nicolaszurbuchen.yadlo.app.design.theme.sizing
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * *Paiement* while it is arriving: the hero, the four marks, and two headed paragraphs.
 *
 * The hero placeholder keeps the tinted ground rather than being drawn as two more grey bars. It is
 * the block the eye lands on and the only coloured thing on the screen, so a skeleton without it
 * would rearrange the page the moment the content landed — which is the one thing a skeleton exists
 * to avoid.
 */
@Composable
fun PaymentSkeleton(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.appColors.primarySubtle)
                    .padding(MaterialTheme.spacing.md),
        ) {
            Spacer(modifier = Modifier.fillMaxWidth(0.7f).height(HERO_HEADLINE_HEIGHT).shimmerBlock())

            Spacer(modifier = Modifier.fillMaxWidth(0.85f).height(LINE_HEIGHT).shimmerBlock())
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.width(HEADER_WIDTH).height(HEADER_HEIGHT).shimmerBlock())

            METHOD_LINES.forEach { fraction ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Spacer(modifier = Modifier.size(MaterialTheme.sizing.icon).shimmerBlock())

                    Spacer(modifier = Modifier.fillMaxWidth(fraction).height(LINE_HEIGHT).shimmerBlock())
                }
            }
        }

        NOTE_LINES.forEach { widths ->
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.width(HEADER_WIDTH).height(HEADER_HEIGHT).shimmerBlock())

                widths.forEach { fraction ->
                    Spacer(modifier = Modifier.fillMaxWidth(fraction).height(LINE_HEIGHT).shimmerBlock())
                }
            }
        }
    }
}

// Four methods: three accepted and the refusal. The published count, so the block does not resize.
private val METHOD_LINES = listOf(0.86f, 0.3f, 0.62f, 0.34f)

// Vous n'avez pas TWINT ?, then Pourquoi.
private val NOTE_LINES = listOf(listOf(1f, 0.72f), listOf(1f, 1f, 0.55f))

private val HERO_HEADLINE_HEIGHT = 30.dp
private val HEADER_WIDTH = 96.dp
private val HEADER_HEIGHT = 12.dp
private val LINE_HEIGHT = 16.dp
