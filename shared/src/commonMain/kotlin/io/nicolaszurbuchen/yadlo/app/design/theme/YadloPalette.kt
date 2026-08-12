package io.nicolaszurbuchen.yadlo.app.design.theme

import androidx.compose.ui.graphics.Color

/**
 * Raw colour values. Nothing outside this file should name a hex code.
 *
 * These are direction 5 of five explored in the visual-identity prototype, chosen on measured
 * perceptual separation between the category colours — ΔE 58.5 light, 49.2 dark. They are not
 * adjustable by eye: nudging one to "look nicer" is what collapses the separation that makes two
 * category dots distinguishable at arm's length in sunlight.
 */
object YadloPalette {
    // Brand. The bandeau blue is the website's, and it only ever carries dark ink: the site's own
    // white-on-#74AEE0 is 2.4:1, while navy on the same blue is 5.4:1.
    val brandLight = Color(0xFF74AEE0)
    val onBrandLight = Color(0xFF0E3552)
    val brandDark = Color(0xFF123549)
    val onBrandDark = Color(0xFFBFDFF5)

    val primaryLight = Color(0xFF14618F)
    val primaryDark = Color(0xFF74AEE0)

    // Accent. Still open against musique below — a pink accent and a magenta category dot may
    // read as one signal. See DECISIONS.md § Open.
    val accent = Color(0xFFE27BA6)

    // Surfaces and ink.
    val backgroundLight = Color(0xFFFFFFFF)
    val surfaceLight = Color(0xFFF1F7FC)
    val lineLight = Color(0xFFE4EDF3)
    val inkLight = Color(0xFF12242F)
    val inkMutedLight = Color(0xFF54707F)
    val inkSoftLight = Color(0xFF8FA6B3)

    val backgroundDark = Color(0xFF0C1A26)
    val surfaceDark = Color(0xFF132635)
    val lineDark = Color(0xFF1E3A4D)
    val inkDark = Color(0xFFE1EFF8)
    val inkMutedDark = Color(0xFF8FADBF)
    val inkSoftDark = Color(0xFF5C7789)

    // Category colours. Each is lifted and desaturated in dark rather than reused, because the
    // light values sit too close to the dark background to stay legible.
    val musiqueLight = Color(0xFFDD3B7A)
    val eauLight = Color(0xFF1B86C9)
    val terreLight = Color(0xFF2FA35A)
    val enfantsLight = Color(0xFFF5B000)
    val silentLight = Color(0xFF8A4FD4)

    val musiqueDark = Color(0xFFF26A9E)
    val eauDark = Color(0xFF5BB3EC)
    val terreDark = Color(0xFF5AC47E)
    val enfantsDark = Color(0xFFFFC933)
    val silentDark = Color(0xFFAC7DE8)
}
