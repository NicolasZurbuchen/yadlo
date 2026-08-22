package io.nicolaszurbuchen.yadlo.app.design.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * The answer, before the page that supports it.
 *
 * **For the blocks whose whole content is one sentence.** *Paiement* is the case that earned it:
 * the fact is three words, everything under it is a consequence of those three words, and a page
 * that opened with a section header called *Accepté partout* made the reader assemble the answer out
 * of a list. Read it, and you can put the phone away. Accueil's thank-you on the Monday after the
 * festival is the same shape doing the same job.
 *
 * **One component rather than three.** They were written weeks apart and had drifted into blocks
 * that meant the same thing and looked different: Paiement set its headline in the 11pt letterspaced
 * category face, which is a label style, so the one sentence the screen exists to say was the
 * smallest text on it; Accueil's *La programmation est là* was the same shape again on a rose ground,
 * with a [kicker] above the title and a tap that opened the Programme. All three are this now, and
 * what separated them turned out to be two optional lines and a chevron.
 *
 * The tinted ground is [io.nicolaszurbuchen.yadlo.app.design.theme.AppColors.primarySubtle] — the
 * bandeau blue, which is the app's quietest way of saying "this is the festival speaking". It always
 * carries dark ink; white on that blue is 2.4:1 and unusable, which AppColorTest holds rather than
 * leaving as prose.
 *
 * **[image] swaps that ground for a photograph, and on Accueil that is now every hero.** It was
 * written as the exception — one picture, on the Monday after, when *Merci.* is the entire page —
 * on the reasoning that a photograph under a hero introducing other content would be louder than
 * the thing introduced. Seeing the phases side by side settled it the other way: Accueil carries at
 * most one hero at a time, it is the first thing the app shows, and a blue card is the app's voice
 * where a photograph is the festival's. The blue remains for a hero inside a page that has more to
 * say — *Paiement* is the case — where the block really is an introduction.
 *
 * Over a photograph the ink is [io.nicolaszurbuchen.yadlo.app.design.theme.AppColors.onScrim] under
 * a flat, full-bleed scrim — the splash's treatment, and for the same reason: it is the alpha at
 * which white clears 4.5:1 over a *white* photograph, so it holds wherever the picture is brightest
 * and whatever is under any given line. The hero's own ink is measured against blue, which a
 * photograph is not. The two treatments are one component so that a hero cannot end up with page
 * ink over a picture.
 *
 * [kicker] is set in the label face, which is what it is: *Nouveau*, *À faire maintenant* — a word
 * about the sentence under it rather than a sentence of its own.
 *
 * A non-null [onClick] makes the whole block the target and draws the chevron that says so. The card
 * rather than a button inside it, which is what the prototype shows and what the rest of the app
 * does — a block you can read is a block you can tap.
 */
@Composable
fun YadloHero(
    title: String,
    modifier: Modifier = Modifier,
    kicker: String? = null,
    body: String? = null,
    image: DrawableResource? = null,
    onClick: (() -> Unit)? = null,
) {
    val ink = if (image != null) MaterialTheme.appColors.onScrim else MaterialTheme.appColors.onPrimarySubtle
    val scrim = MaterialTheme.appColors.scrim

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                // Under the photograph as well as instead of it: a picture that has not decoded on
                // the first frame leaves the block the colour it would otherwise have been.
                .background(MaterialTheme.appColors.primarySubtle)
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .then(if (image != null) Modifier.heightIn(min = IMAGE_HERO_MIN_HEIGHT) else Modifier),
    ) {
        image?.let {
            Image(
                painter = painterResource(it),
                // The words in front of it say what it is. A photograph of the site behind
                // "Merci." adds nothing a screen reader can use and interrupts the sentence.
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            // Flat and full-bleed, the splash's treatment rather than the fiche's gradient.
            //
            // It was a bottom-weighted gradient, on the reasoning that the middle of the picture is
            // the part worth showing. That works on the fiche, where the words are a single line
            // against the very bottom edge; it does not work here, because a hero's text is a
            // kicker, a title and a body stacked up through the lower half, and the gradient only
            // reaches full strength at 1f. The top of that stack was sitting over near-transparent
            // scrim, so on a bright photograph the kicker and the title went first — which is
            // exactly backwards, since the title is the sentence the block exists to say.
            //
            // A flat scrim is the value AppColorTest measures: white over it clears AA against a
            // *pure white* photograph, so every line clears it wherever the picture is brightest.
            // The cost is a picture uniformly darker than a gradient would leave it, which is the
            // right trade for a block whose words are the point.
            Box(
                modifier =
                    Modifier
                        .matchParentSize()
                        .background(scrim),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .align(if (image != null) Alignment.BottomStart else Alignment.Center)
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.lg),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier = Modifier.weight(1f),
            ) {
                kicker?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.displaySmall,
                        color = ink,
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = ink,
                )

                body?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ink,
                    )
                }
            }

            if (onClick != null) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = ink,
                )
            }
        }
    }
}

/**
 * Enough of a landscape photograph to be a photograph rather than a strip of one. Two lines of text
 * and their padding come to about 120dp, so anything less would crop to the band behind the words
 * and show nothing of the picture above them.
 */
private val IMAGE_HERO_MIN_HEIGHT = 200.dp
