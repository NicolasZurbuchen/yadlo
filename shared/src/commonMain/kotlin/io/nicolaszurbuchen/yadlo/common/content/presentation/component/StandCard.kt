package io.nicolaszurbuchen.yadlo.common.content.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import io.nicolaszurbuchen.yadlo.app.design.component.YadloDietaryTags
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.StandCardUiModel
import org.jetbrains.compose.resources.painterResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.img_placeholder

/**
 * One Stand, wherever Stands are listed. Opens the fiche, which holds the menu and the single heart
 * that puts it on the Wishlist.
 *
 * **The photograph is the card.** *Reversed: this was a text row with a chevron.* Three lines of
 * grey text with a chevron after them is what a settings entry looks like, and it was being asked
 * to sell dinner. On *Créateurs*, where there is no menu and therefore no dietary line, it was two
 * lines on an otherwise empty tile — the reader had nothing to look at and nothing to choose
 * between. A picture is the fastest true thing this app can say about a food truck, and every one
 * of the eight Stands has one.
 *
 * **Three bands, not one paragraph.** The picture, then the name and what it sells, then what can
 * be eaten there behind a rule. The information had not changed and the wording had not either —
 * what was wrong was that four different kinds of fact were stacked at one indent with 4dp between
 * them, so a card carrying two dietary marks read as a wall. The rule is what makes the third band
 * skippable by someone who does not need it and findable by someone who does.
 *
 * **No chevron and no Category colour.** The picture says the card is a place rather than a row, so
 * the disclosure mark has nothing left to add. Colour stays out because the five measured hues
 * belong to what kind of thing a Happening is, and a second colour system on the same screen makes
 * both of them mean less.
 *
 * The dietary band is absent rather than empty when nothing is published, which is every Stand on
 * *Créateurs* and one of the six on *Nourriture & boissons* — a rule under a name with nothing
 * below it reads as content that failed to load.
 *
 * **It is drawn two to a column, in a staggered grid.** Nothing here is sized for that — the picture
 * is a ratio, the name and the offering wrap, the marks flow — so the card takes whatever width it
 * is handed and is exactly as tall as what it has to say. A plain `LazyVerticalGrid` turned that
 * into dead space: its lines are measured together, so a card with no dietary band left a gap under
 * itself until the taller card beside it finished. Under
 * `androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid` each column runs on
 * its own cursor and the next card starts where the one above it ends, which is the arrangement a
 * card of genuinely variable height wants. The two columns drift out of step, and that drift is the
 * absence of the gap rather than a flaw in it.
 */
@Composable
fun StandCard(
    stand: StandCardUiModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // The same bundled photograph of the site the fiche falls back to, for a null url and a failed
    // load alike: on a beach with one bar of signal they are the same fact.
    val placeholder = painterResource(Res.drawable.img_placeholder)

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.appColors.surface)
                .clickable { onClick(stand.id) },
    ) {
        AsyncImage(
            model = stand.imageUrl,
            // The name is written directly under it. "Photo de Guliko" over a card titled Guliko
            // says it twice.
            contentDescription = null,
            contentScale = ContentScale.Crop,
            fallback = placeholder,
            error = placeholder,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(IMAGE_RATIO)
                    .background(MaterialTheme.appColors.surfaceRaised),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.md),
        ) {
            Text(
                text = stand.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.appColors.textPrimary,
            )

            stand.offering?.let { offering ->
                Text(
                    text = offering,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
        }

        if (stand.dietary.isNotEmpty()) {
            HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)

            YadloDietaryTags(
                tags = stand.dietary,
                modifier =
                    Modifier.padding(
                        horizontal = MaterialTheme.spacing.md,
                        vertical = MaterialTheme.spacing.sm,
                    ),
            )
        }
    }
}

/**
 * Three by two rather than the sixteen by nine a card like this usually gets.
 *
 * Every photograph in the bank is four by three, so any wider frame is a centre crop that throws
 * away the top and bottom of the picture — at 16:9 that is a quarter of the height, which on the
 * one portrait among the eight takes the top of the subject's head off. 3:2 keeps all but a tenth
 * and still reads as a banner rather than as a photograph shown whole.
 */
private const val IMAGE_RATIO = 3f / 2f
