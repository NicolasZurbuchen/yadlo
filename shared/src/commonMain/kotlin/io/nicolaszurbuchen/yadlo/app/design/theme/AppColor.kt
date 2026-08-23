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
     * The accent as ink on a page ground — the emergency numbers, and so far only those.
     *
     * A role of its own because [accent] is a fill: it is chosen to carry [onAccent], and as text it
     * measures 2.3:1 on the lightest ground, which is unreadable. This is the same rose ramp at the
     * step that can be written with.
     */
    val accentInk: Color,
    /**
     * The accent as a fill **on the chrome's blue** — the selected tab's pill, and so far only that.
     *
     * A pair of its own for the same reason [accentInk] is one: [accent] and [accentSubtle] are
     * chosen against the page grounds, and the bar is neither of them. Measured on
     * [primarySubtle]: `accentSubtle` lands at 1.95:1 in light and **1.34:1 in dark**, which is a
     * selected-tab indicator that cannot be seen at all; `accent` itself is 1.16:1 in light, the
     * same luminance as the bar in a different hue, so the shape survives colour and vanishes in
     * greyscale.
     *
     * It swaps ends with the theme like every other pair here, and for the ordinary reason: the two
     * bars sit at opposite ends of the ramp, so the step that clears one cannot clear the other.
     * The ink follows the step rather than the theme — white on the deep light-mode pill, near-black
     * on the vivid dark-mode one.
     */
    val accentChrome: Color,
    val onAccentChrome: Color,
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
     * `dans 45 min`, drawn as text on the page ground — the heads-up half of time pressure.
     *
     * Amber, because a countdown is a caution: nothing is being lost yet, and the pill is telling
     * you how long you have rather than that you are out of it. It has to be legible as ink on all
     * three grounds, which is what keeps it at the deep end of the ramp in light.
     */
    val warning: Color,
    val onWarning: Color,
    /**
     * `se termine` — the fill under the one pill on the screen asking for something to be *done*.
     *
     * A role of its own rather than the amber above it, because the two are not the same fact at two
     * distances: one says how long you have, the other says you are nearly out. Sharing a hue made
     * the second read as more of the first. Orange-red is the prototype's coral, refused once on the
     * grounds it had never been part of the perceptual-separation measurement the Category hues were
     * chosen by; it has been measured since — 112 and 119 from [live], the pair that appears on
     * adjacent rows, and 54 to 59 from [warning], the pair that has to be told apart at a glance.
     * 24 and 18 from `musique` is the closest it comes to anything, which is why the pill is still
     * written out in words.
     *
     * **The same colour in both themes, carrying dark ink in both** — like [accent], and unlike every
     * surface role here. A signal is not a ground: it is not being read *against* the theme, it is
     * interrupting it, and the vivid step is the only one on this ramp that both looks urgent and
     * holds text. Its own boundary against a light page is 2.5:1, which is under the non-text floor
     * and is accepted for the same reason an `enfants` chip's amber fill is: the pill is a word on a
     * colour, not a control, and the word inside it measures 6.4:1.
     */
    val urgent: Color,
    val onUrgent: Color,
    /** Tints a ✓ read as a judgement. The same emerald as [live]: one green in the app to learn. */
    val positive: Color,
    /** Tints a ✕. Deep enough on the magenta ramp to clear the grounds and the `musique` dot. */
    val negative: Color,
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
        // White is the page and every block on it is one step further into the blue. It ran the
        // other way — a slate-tinted ground under white cards — which is Material's own elevation
        // model and reads on a phone as a grey app with white boxes on it. The festival's colour is
        // a blue, and a tinted card under the bandeau blue bar is the quietest place to spend it.
        //
        // The two steps are the size they always were: white → skyBlue50 is 1.07, the same as
        // slate50 → white was, and skyBlue50 → skyBlue100 is 1.11 against slate100's 1.12. Nothing
        // measured against these grounds moved, which is why the AppColorTest matrix still holds
        // with the same roles.
        background = Color.White,
        surface = SkyBluePalette.skyBlue50,
        surfaceRaised = SkyBluePalette.skyBlue100,
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
        accentInk = RosePalette.rose700,
        // The same step accentInk takes, arrived at from the other direction: on skyBlue400 it is
        // the first one deep enough to be a shape at 3.45:1, and it carries white at 8.16:1 — the
        // one place in the light theme where the accent takes white rather than the navy `onAccent`.
        accentChrome = RosePalette.rose700,
        onAccentChrome = Color.White,
        // Two steps darker than the `terre` anchor, not the anchor itself: a filled pill has to
        // carry white, and emerald600 is a category fill chosen to sit beside four other hues
        // rather than to be written on.
        live = EmeraldPalette.emerald800,
        onLive = Color.White,
        // Deep enough to be written with, which is all this role does in light: it is the ink of
        // `dans 45 min` on three grounds and never a fill of its own.
        warning = AmberPalette.amber800,
        onWarning = Color.White,
        urgent = OrangePalette.orange600,
        onUrgent = SlatePalette.slate950,
        positive = EmeraldPalette.emerald800,
        negative = MagentaPalette.magenta800,
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
        // The ramp swaps ends in dark, like every other pair here: rose700 is invisible on a dark
        // ground, and rose400 — the accent itself — lands just under 4.5:1 on the raised one.
        accentInk = RosePalette.rose300,
        // The anchor itself, which the dark bar is deep enough to carry: rose400 is 3.76:1 on
        // skyBlue900 and takes the theme's own `onAccent` at 7.48:1. The one role whose dark half
        // is the accent unmodified.
        accentChrome = RosePalette.rose400,
        onAccentChrome = SlatePalette.slate950,
        // Both swap ends in dark for the same reason the blue does: the light steps are too deep to
        // read as anything against a dark ground, whether they are being written on or written in.
        live = EmeraldPalette.emerald400,
        onLive = SlatePalette.slate950,
        warning = AmberPalette.amber400,
        onWarning = SlatePalette.slate950,
        // The one role that does not swap ends. orange600 fails as ink on surfaceRaised at 4.0:1,
        // which would matter if this were ever written with — it is not, it is only ever filled, and
        // a signal that changed colour with the theme would be a different signal.
        urgent = OrangePalette.orange600,
        onUrgent = SlatePalette.slate950,
        positive = EmeraldPalette.emerald400,
        negative = MagentaPalette.magenta400,
        scrim = SCRIM,
        onScrim = Color.White,
    )

internal val LocalAppColors = staticCompositionLocalOf { LightAppColors }

val MaterialTheme.appColors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current
