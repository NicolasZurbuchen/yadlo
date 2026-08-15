package io.nicolaszurbuchen.yadlo.app.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * App-level semantic colour layer. This is what our own composables read; `colorScheme` exists only
 * so stock Material components have a sane default, and is mapped from the same palettes in
 * [YadloTheme].
 *
 * Every field below is here because a screen in SPEC.md needs it, and the comment on each says
 * which. A role nothing paints yet is not carried speculatively — the layer is cheap to extend and
 * expensive to guess at.
 */
data class AppColors(
    /** The page ground. The grouped-list backdrop in Plus, and what Programme rows sit directly on. */
    val background: Color,
    /** A card or a grouped-list block raised off [background] — Plus entries, an annonce on Accueil. */
    val surface: Color,
    /** Raised once more: a filter chip's fill, the track a running Slot's progress bar fills in. */
    val surfaceRaised: Color,
    /** Hairline between rows of the same group. */
    val borderSubtle: Color,
    /** A drawn edge that has to be seen on its own — an outlined chip, an unselected filter. */
    val borderStrong: Color,
    /** Titles and body. */
    val textPrimary: Color,
    /** Supporting text: a Slot's time range, a Happening's short description. */
    val textSecondary: Color,
    /** Metadata: the provenance line under a price, a disclosure chevron, a caption. */
    val textTertiary: Color,
    /**
     * Emphasis, pills and active states — the `primaire` of SPEC.md § Identity. The selected filter
     * chip, the active tab, a primary button.
     */
    val primary: Color,
    val onPrimary: Color,
    /**
     * The bandeau blue from yadlo.ch, as the quiet half of the primary role rather than a role of
     * its own: a brand colour that only ever appears as a light fill *is* a subtle fill.
     *
     * It always carries dark ink. The site's own white-on-#74AEE0 is 2.4:1; navy on the same blue is
     * 6.7:1. That rule is asserted in AppColorTest rather than left as prose.
     */
    val primarySubtle: Color,
    val onPrimarySubtle: Color,
    /** The accent of SPEC.md § Identity: a floating action button, a screen title. */
    val accent: Color,
    val onAccent: Color,
    /** The accent as a tinted ground rather than a fill — a highlighted row, a badge. */
    val accentSubtle: Color,
    val onAccentSubtle: Color,
    /**
     * A Slot running now — the filled `en cours` pill on a Programme or Mon Yadlo row.
     *
     * A role of its own rather than [primary] because the two appear on the same row and mean
     * different things: primary is "the app is emphasising this", live is "this is happening while
     * you read it".
     */
    val live: Color,
    val onLive: Color,
    /**
     * The warning ramp behind both time-pressure pills: `dans 15 min` draws it as text on the page
     * ground, `se termine` fills with it.
     *
     * One amber ramp rather than the prototype's amber-then-coral, because the coral was never part
     * of the perceptual-separation measurement the five Category hues were chosen by, and a red
     * pill beside a `musique` magenta dot is the collision DECISIONS.md § Open already flags for the
     * accent. Outlined against filled separates the two states, and the pill is written out either
     * way — the colour is never carrying it alone.
     */
    val warning: Color,
    val onWarning: Color,
    /** Veil over a photograph, so text stays readable on an image nobody vetted. Alpha is baked in. */
    val scrim: Color,
    /** The ink [scrim] exists to make legible. */
    val onScrim: Color,
    /** Not a colour: lets a call site branch without reaching for isSystemInDarkTheme() directly. */
    val isDark: Boolean,
)

// Derived here, not taken from a prototype: 0.6 is the lowest alpha at which white clears 4.5:1 over
// a fully white photograph, which is the worst case a hero image can present. Same in both themes
// because what it covers is a photo, not one of the app grounds. AppColorTest holds it.
private val SCRIM = SlatePalette.slate950.copy(alpha = 0.6f)

val LightAppColors =
    AppColors(
        isDark = false,
        background = SlatePalette.slate50,
        surface = Color.White,
        surfaceRaised = SlatePalette.slate100,
        borderSubtle = SlatePalette.slate200,
        // slate400 is the prettier hairline and it fails: 2.7:1 on white, under the 3:1 WCAG asks
        // of a control's own edge.
        borderStrong = SlatePalette.slate500,
        textPrimary = SlatePalette.slate900,
        textSecondary = SlatePalette.slate700,
        textTertiary = SlatePalette.slate600,
        primary = SkyBluePalette.skyBlue800,
        onPrimary = Color.White,
        primarySubtle = SkyBluePalette.skyBlue400,
        onPrimarySubtle = SlatePalette.slate900,
        accent = RosePalette.rose400,
        onAccent = SlatePalette.slate900,
        accentSubtle = RosePalette.rose100,
        onAccentSubtle = RosePalette.rose900,
        // Two steps darker than the `terre` anchor, not the anchor itself: a filled pill has to
        // carry white, and emerald600 is a category fill chosen to sit beside four other hues
        // rather than to be written on.
        live = EmeraldPalette.emerald800,
        onLive = Color.White,
        warning = AmberPalette.amber800,
        onWarning = Color.White,
        scrim = SCRIM,
        onScrim = Color.White,
    )

val DarkAppColors =
    AppColors(
        isDark = true,
        background = SlatePalette.slate950,
        surface = SlatePalette.slate900,
        surfaceRaised = SlatePalette.slate800,
        borderSubtle = SlatePalette.slate800,
        // slate600 reads as the symmetric counterpart to light's slate500 and fails on two of the
        // three grounds — 2.8:1 on surface, 2.1:1 on surfaceRaised. A dark border has to clear the
        // ground it is drawn on, and the raised grounds are the ones it has least room against.
        borderStrong = SlatePalette.slate500,
        textPrimary = SlatePalette.slate100,
        textSecondary = SlatePalette.slate200,
        // slate400 was the obvious tertiary and it fails: 4.46:1 on surfaceRaised, which is the one
        // ground of the three where a dim text role is most likely to be used.
        textTertiary = SlatePalette.slate300,
        // The blue swaps ends in dark. The light theme's emphasis step is too heavy to read as
        // emphasis against a dark ground, so the bandeau blue takes over as primary and the deep
        // step becomes the subtle fill.
        primary = SkyBluePalette.skyBlue400,
        onPrimary = SlatePalette.slate950,
        primarySubtle = SkyBluePalette.skyBlue900,
        onPrimarySubtle = SkyBluePalette.skyBlue200,
        accent = RosePalette.rose400,
        onAccent = SlatePalette.slate950,
        accentSubtle = RosePalette.rose900,
        onAccentSubtle = RosePalette.rose200,
        // Both swap ends in dark for the same reason the blue does: the light steps are too deep to
        // read as anything against a dark ground, whether they are being written on or written in.
        live = EmeraldPalette.emerald400,
        onLive = SlatePalette.slate950,
        warning = AmberPalette.amber400,
        onWarning = SlatePalette.slate950,
        scrim = SCRIM,
        onScrim = Color.White,
    )

internal val LocalAppColors = staticCompositionLocalOf { LightAppColors }

val MaterialTheme.appColors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current
