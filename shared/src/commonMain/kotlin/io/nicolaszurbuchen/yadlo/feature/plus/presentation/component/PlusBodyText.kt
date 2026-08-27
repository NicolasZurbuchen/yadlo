package io.nicolaszurbuchen.yadlo.feature.plus.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing

/**
 * Prose published by the association, in its own paragraphs.
 *
 * Split on blank lines rather than rendered as one block, because the content files hold a
 * paragraph break as exactly that and a wall of eight lines is what the website already does. The
 * app has no rich text and wants none: everything these screens say is plain French, which is what
 * makes the content editable by someone who is not a developer.
 */
@Composable
fun PlusBodyText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        text.split(PARAGRAPH_BREAK).forEach { paragraph ->
            Text(
                text = paragraph.trim(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.appColors.textSecondary,
            )
        }
    }
}

private const val PARAGRAPH_BREAK = "\n\n"
