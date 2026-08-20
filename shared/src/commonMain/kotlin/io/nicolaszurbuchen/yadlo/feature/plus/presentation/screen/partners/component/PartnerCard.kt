package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import io.nicolaszurbuchen.yadlo.app.design.theme.SlatePalette
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners.PartnerUiModel
import kotlin.math.sqrt

/**
 * One partner, as a logo on a white card.
 *
 * **The ground is white in both themes, and so are its edge and its ink.** These marks were drawn
 * for print and for a white website; the app has no curated set of dark-theme variants and will not
 * get one, because thirty-nine of them belong to thirty-nine companies who each own their own. A
 * fixed white card is the one ground every logo in the bank is already correct against. It follows
 * that nothing on this card may take a theme colour: a hairline in `borderSubtle` would vanish on
 * white in dark mode, and a name in `textPrimary` would be near-white text on a white card. The card
 * is a fixed ground, so its edge and its ink are fixed with it.
 *
 * The edge earns its keep in the *light* theme, where the page behind is `Color.White` too — without
 * it the cards would not exist as objects at all.
 *
 * **A logo is normalised by area, not fitted to the box.** See [logoScaleFor]. Plain
 * [ContentScale.Fit] is geometrically correct and optically wrong here: the bank runs from 0.83
 * (Volt-A, all but square) to 6.38 (VSM, six times wider than tall), and fitting each to the same
 * frame makes the square ones look several times heavier than the wide ones — on a screen whose
 * whole job is to give thirty-nine companies their due.
 *
 * **The name is the fallback, for a null url and a failed load alike.** A logo the network could not
 * fetch and a partner who never supplied one are the same fact to someone standing on the beach, and
 * the name is what the logo was standing for.
 */
@Composable
fun PartnerCard(
    partner: PartnerUiModel,
    onClick: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val painter = rememberAsyncImagePainter(model = partner.logoUrl)
    val state by painter.state.collectAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .clip(MaterialTheme.shapes.extraSmall)
                .background(LOGO_GROUND)
                .border(EDGE_WIDTH, LOGO_EDGE, MaterialTheme.shapes.extraSmall)
                .clickable { onClick(partner.url) }
                .padding(MaterialTheme.spacing.sm),
    ) {
        if (partner.logoUrl == null || state is AsyncImagePainter.State.Error) {
            Text(
                text = partner.name,
                style = MaterialTheme.typography.bodySmall,
                color = LOGO_INK,
                textAlign = TextAlign.Center,
            )
        } else {
            // The box has to be measured rather than assumed: its width comes from a weight in the
            // row above, and the normalisation is a statement about this logo against this box.
            BoxWithConstraints(contentAlignment = Alignment.Center) {
                val ratio =
                    painter.intrinsicSize
                        .takeIf { it.isSpecified && it.height > 0f }
                        ?.let { it.width / it.height }

                Image(
                    painter = painter,
                    contentDescription = partner.name,
                    contentScale = ContentScale.Fit,
                    // A draw-time transform rather than a smaller layout box, so the size the
                    // logo is *requested* at never changes and Coil is not asked for the same
                    // file twice at two sizes when the first one lands and the ratio is known.
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .scale(ratio?.let { logoScaleFor(it, maxWidth / maxHeight) } ?: UNSCALED),
                )
            }
        }
    }
}

/**
 * How far below [ContentScale.Fit] a logo of [aspectRatio] is drawn, inside a box of
 * [boxAspectRatio], so that every logo on the screen covers about the same area.
 *
 * **The problem.** Fit gives a logo the largest size that stays inside the box, which means a square
 * logo comes out as tall as the box and a 6:1 logo as wide as it. Those are wildly different amounts
 * of ink: in a three-column cell the square covers 3136 square points and VSM covers 1214, so the
 * square reads as almost three times the sponsor.
 *
 * **The rule.** Give every logo the same *area* instead. Normalising the box to height 1 — so its
 * width and its area are both [boxAspectRatio] — a logo takes `boxAspectRatio² / aspectRatio` when
 * it is wide enough to be width-bound, and `aspectRatio` when it is height-bound. Scaling by `s`
 * scales area by `s²`, so the factor is the square root of the ratio between the area wanted and the
 * area Fit gives.
 *
 * **It only ever shrinks.** A logo wider than the box can never reach the target area — there is no
 * room — so the factor is capped at 1 and the widest few are drawn exactly as Fit would draw them.
 * That is the real limit of this: it evens out everything up to about 4.5:1 and leaves the last two
 * of the thirty-nine, VSM and Von Auw, lighter than the rest. Growing them would mean a taller cell
 * for every tier, which spends a screen of scroll on two logos.
 */
internal fun logoScaleFor(
    aspectRatio: Float,
    boxAspectRatio: Float,
    balance: Float = LOGO_AREA_BALANCE,
): Float {
    if (aspectRatio <= 0f || boxAspectRatio <= 0f) return UNSCALED

    val fitArea =
        if (aspectRatio >= boxAspectRatio) {
            boxAspectRatio * boxAspectRatio / aspectRatio
        } else {
            aspectRatio
        }

    return sqrt(balance * boxAspectRatio / fitArea).coerceAtMost(UNSCALED)
}

/**
 * The share of the cell one logo is meant to cover, and the one number in here that was tuned rather
 * than derived.
 *
 * It is what makes the areas actually meet: at 0.35 a square logo and a 3:1 logo come out at the
 * same 1725 square points in a three-column cell, and everything up to 4.5:1 joins them. Higher and
 * the cap starts biting early, so the wide logos fall out of the set the rule is levelling; lower
 * and every logo is smaller than it needs to be inside a card that is already the right size.
 */
private const val LOGO_AREA_BALANCE = 0.35f

/** Drawn exactly as [ContentScale.Fit] would. */
private const val UNSCALED = 1f

// Fixed, not theme roles — see the note on PartnerCard. White because that is the ground these marks
// were drawn against; the ink is the light theme's own body colour, which clears 8.25:1 on it.
private val LOGO_GROUND = Color.White
private val LOGO_EDGE = SlatePalette.slate200
private val LOGO_INK = SlatePalette.slate700

/** A hairline. The card is separated by its ground everywhere except light, where it needs an edge. */
private val EDGE_WIDTH = 1.dp
