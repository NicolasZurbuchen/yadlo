package io.nicolaszurbuchen.yadlo.app.design.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.barlow_bold
import yadlo.shared.generated.resources.barlow_medium
import yadlo.shared.generated.resources.barlow_regular
import yadlo.shared.generated.resources.barlow_semi_condensed_bold
import yadlo.shared.generated.resources.barlow_semi_condensed_medium
import yadlo.shared.generated.resources.barlow_semi_condensed_regular
import yadlo.shared.generated.resources.barlow_semi_condensed_semibold
import yadlo.shared.generated.resources.barlow_semibold

/**
 * Barlow, SIL OFL — licence in `licenses/Barlow-OFL.txt`.
 *
 * The festival's own voice is DIN Neuzeit Grotesk, which is Wix-licensed and cannot be
 * redistributed in an app. Barlow is the openly licensed DIN-adjacent grotesque that ships legally
 * inside the app and reads as the same brand on screen as the posters do in print. Its real width
 * variants are the reason it won over a closer DIN clone: the display face is the condensed one.
 */
val BarlowFamily
    @Composable get() =
        FontFamily(
            Font(Res.font.barlow_regular, FontWeight.Normal),
            Font(Res.font.barlow_medium, FontWeight.Medium),
            Font(Res.font.barlow_semibold, FontWeight.SemiBold),
            Font(Res.font.barlow_bold, FontWeight.Bold),
        )

/** The display face: wordmark, headings, screen titles, and every time on screen. */
val BarlowSemiCondensedFamily
    @Composable get() =
        FontFamily(
            Font(Res.font.barlow_semi_condensed_regular, FontWeight.Normal),
            Font(Res.font.barlow_semi_condensed_medium, FontWeight.Medium),
            Font(Res.font.barlow_semi_condensed_semibold, FontWeight.SemiBold),
            Font(Res.font.barlow_semi_condensed_bold, FontWeight.Bold),
        )

// Tabular figures. Times sit in a column on the Programme and in Mon Yadlo, and proportional
// numerals make that column jitter row to row. This is also what replaces the monospace face the
// template used for times: DIN's numerals are a large part of why the style reads as it does, and
// a mono was making a festival app look like a dashboard.
private const val TABULAR_FIGURES = "tnum"

/**
 * Typography roles mapped to Material3 slots. The mapping matters more than the numbers — keep
 * every slot's purpose comment, even if you change sizes and weights.
 */
val AppTypography
    @Composable get() =
        Typography(
            // The countdown on Accueil — "J-142", the largest thing in the app
            displayLarge =
                TextStyle(
                    fontFamily = BarlowSemiCondensedFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 72.sp,
                    lineHeight = 80.sp,
                    letterSpacing = (-2).sp,
                    fontFeatureSettings = TABULAR_FIGURES,
                ),
            // Yadlo en chiffres — one statistic per tile
            displayMedium =
                TextStyle(
                    fontFamily = BarlowSemiCondensedFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    lineHeight = 44.sp,
                    letterSpacing = (-1).sp,
                    fontFeatureSettings = TABULAR_FIGURES,
                ),
            // The written Category label above a fiche title — "MUSIQUE", "SUR L'EAU"
            displaySmall =
                TextStyle(
                    fontFamily = BarlowSemiCondensedFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 2.sp,
                ),
            // Screen titles — Accueil, Programme, Mon Yadlo, Plus
            headlineLarge =
                TextStyle(
                    fontFamily = BarlowSemiCondensedFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    lineHeight = 40.sp,
                    letterSpacing = (-0.5).sp,
                ),
            // Section headers within a screen — "Bon à savoir", "Sur place"
            headlineMedium =
                TextStyle(
                    fontFamily = BarlowSemiCondensedFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 1.5.sp,
                ),
            // A fiche's title — the artist, activity or stand name in the collapsing toolbar
            headlineSmall =
                TextStyle(
                    fontFamily = BarlowSemiCondensedFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    lineHeight = 30.sp,
                    letterSpacing = (-0.3).sp,
                ),
            // Button labels
            titleLarge =
                TextStyle(
                    fontFamily = BarlowFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 1.sp,
                ),
            // A Happening's name on a Programme or Mon Yadlo row — the thing you scan for
            titleMedium =
                TextStyle(
                    fontFamily = BarlowFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    letterSpacing = 0.sp,
                ),
            // FestivalDay headers, and the date pinned to the rail in Mon Yadlo
            titleSmall =
                TextStyle(
                    fontFamily = BarlowSemiCondensedFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 1.sp,
                ),
            // Descriptions on a fiche, and the body of a text page in Plus
            bodyLarge =
                TextStyle(
                    fontFamily = BarlowFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    letterSpacing = 0.sp,
                ),
            // Supporting text — a row's subtitle, a menu item's description
            bodyMedium =
                TextStyle(
                    fontFamily = BarlowFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.sp,
                ),
            // Captions and provenance — image credits, "prix 2026, à confirmer"
            bodySmall =
                TextStyle(
                    fontFamily = BarlowFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.3.sp,
                ),
            // A Slot's time, written once as a range — "16:00 – 18:00"
            labelLarge =
                TextStyle(
                    fontFamily = BarlowSemiCondensedFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    letterSpacing = 0.sp,
                    fontFeatureSettings = TABULAR_FIGURES,
                ),
            // The live-state pill — "dans 15 min", "en cours", "se termine", "terminé"
            labelMedium =
                TextStyle(
                    fontFamily = BarlowFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.2.sp,
                ),
            // Filter chips on the Programme, and attribute tags on a fiche
            labelSmall =
                TextStyle(
                    fontFamily = BarlowFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    letterSpacing = 1.sp,
                ),
        )
