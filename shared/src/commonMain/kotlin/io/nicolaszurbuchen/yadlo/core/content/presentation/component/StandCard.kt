package io.nicolaszurbuchen.yadlo.core.content.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.StandCardUiModel
import io.nicolaszurbuchen.yadlo.design.component.YadloDietaryMarks
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.stands_card_options

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
 * **The third band is one word and the marks, not the marks written out.** *Reversed: it was the
 * full [YadloDietaryTags].* On a card the tags were the layout — *100 % végan · Options sans gluten
 * · Tout sans lactose* is three lines of small print under a name and an offering that take two,
 * so the smallest thing on the card outweighed what the card is for. None of it was information a
 * browser acts on either: how *much* of a carte is vegan matters when choosing a dish, and on a
 * grid of eight trucks the question is only whether there is anything here at all. *Options* is
 * the honest reduction of the three wordings to the one fact they share, and the coverage is on
 * the fiche one tap away, in full, where the dish list needs it as a legend anyway.
 *
 * The filter row above the grid names every mark in words, so a reader arriving at these glyphs
 * has already been given the vocabulary — which is the same argument [YadloDietaryMarks] makes for
 * a carte, one screen further in.
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
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.appColors.surface)
                .clickable { onClick(stand.id) },
    ) {
        ContentImage(
            imageUrl = stand.imageUrl,
            sharedKey = stand.id,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(CONTENT_IMAGE_RATIO)
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = MaterialTheme.spacing.md,
                            vertical = MaterialTheme.spacing.sm,
                        ),
            ) {
                Text(
                    text = stringResource(Res.string.stands_card_options),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.appColors.textSecondary,
                )

                YadloDietaryMarks(tags = stand.dietary)
            }
        }
    }
}
