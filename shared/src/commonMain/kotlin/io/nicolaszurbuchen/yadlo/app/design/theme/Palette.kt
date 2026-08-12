package io.nicolaszurbuchen.yadlo.app.design.theme

import androidx.compose.ui.graphics.Color

/**
 * Raw colour values. Nothing outside this file should name a hex code, and nothing in this file
 * should name a role — a palette says what a colour *is*, and [AppColors] / [CategoryColors] decide
 * what it is *for*.
 *
 * Every ramp is anchored on the identity fixed in SPEC.md § Identity, which was chosen on measured
 * perceptual separation between the category colours (ΔE 58.5 light, 49.2 dark, best of five
 * directions explored in the visual-identity prototype). The anchors are marked below and are
 * reproduced exactly; the remaining steps were generated around them in OKLab so the ramp is
 * perceptually even and stays inside the sRGB gamut.
 *
 * The anchors are not adjustable by eye. Nudging one to "look nicer" is what collapses the
 * separation that lets two category dots be told apart at arm's length in July sun.
 */
object SkyBluePalette {
    val skyBlue950 = Color(0xFF082E4A)
    val skyBlue900 = Color(0xFF124368)
    val skyBlue800 = Color(0xFF14618F) // anchor — primaire
    val skyBlue700 = Color(0xFF1F72AF)
    val skyBlue600 = Color(0xFF1B86C9) // anchor — eau
    val skyBlue500 = Color(0xFF4D9AD8)
    val skyBlue400 = Color(0xFF74AEE0) // anchor — bandeau / marque, the blue from yadlo.ch
    val skyBlue300 = Color(0xFF98C7F1)
    val skyBlue200 = Color(0xFFBFDFFC)
    val skyBlue100 = Color(0xFFDCEEFF)
    val skyBlue50 = Color(0xFFF0F8FF)
}

object SlatePalette {
    val slate950 = Color(0xFF05121A)
    val slate900 = Color(0xFF12242F) // anchor — encre
    val slate800 = Color(0xFF223A49)
    val slate700 = Color(0xFF355265)
    val slate600 = Color(0xFF4B6B80)
    val slate500 = Color(0xFF64879D)
    val slate400 = Color(0xFF82A3B8)
    val slate300 = Color(0xFFA4BFD1)
    val slate200 = Color(0xFFC7DAE6)
    val slate100 = Color(0xFFE1ECF3)
    val slate50 = Color(0xFFF2F8FC)
}

object RosePalette {
    val rose950 = Color(0xFF390E22)
    val rose900 = Color(0xFF501832)
    val rose800 = Color(0xFF6F2447)
    val rose700 = Color(0xFF8E345D)
    val rose600 = Color(0xFFAB4774)
    val rose500 = Color(0xFFC95E8D)
    val rose400 = Color(0xFFE27BA6) // anchor — accent
    val rose300 = Color(0xFFF59FC1)
    val rose200 = Color(0xFFFFC5DA)
    val rose100 = Color(0xFFFFE2EC)
    val rose50 = Color(0xFFFFF3F7)
}

object MagentaPalette {
    val magenta950 = Color(0xFF42001D)
    val magenta900 = Color(0xFF63012E)
    val magenta800 = Color(0xFF8D0A46)
    val magenta700 = Color(0xFFB6205F)
    val magenta600 = Color(0xFFDD3B7A) // anchor — musique
    val magenta500 = Color(0xFFFA518F)
    val magenta400 = Color(0xFFFF81A8)
    val magenta300 = Color(0xFFFFABC2)
    val magenta200 = Color(0xFFFFCEDA)
    val magenta100 = Color(0xFFFFE5EB)
    val magenta50 = Color(0xFFFFF3F6)
}

object EmeraldPalette {
    val emerald950 = Color(0xFF00290F)
    val emerald900 = Color(0xFF00411C)
    val emerald800 = Color(0xFF00612D)
    val emerald700 = Color(0xFF0B8241)
    val emerald600 = Color(0xFF2FA35A) // anchor — terre
    val emerald500 = Color(0xFF45B96D)
    val emerald400 = Color(0xFF65CE85)
    val emerald300 = Color(0xFF8FE0A4)
    val emerald200 = Color(0xFFB8EFC5)
    val emerald100 = Color(0xFFD8F7DE)
    val emerald50 = Color(0xFFEBFCEF)
}

object AmberPalette {
    val amber950 = Color(0xFF412C00)
    val amber900 = Color(0xFF5C4000)
    val amber800 = Color(0xFF7F5900)
    val amber700 = Color(0xFFA47500)
    val amber600 = Color(0xFFCA9100)
    val amber500 = Color(0xFFF5B000) // anchor — enfants
    val amber400 = Color(0xFFFFC045)
    val amber300 = Color(0xFFFFD387)
    val amber200 = Color(0xFFFFE4B6)
    val amber100 = Color(0xFFFFEFD4)
    val amber50 = Color(0xFFFFF6E6)
}

object VioletPalette {
    val violet950 = Color(0xFF290E48)
    val violet900 = Color(0xFF3D1965)
    val violet800 = Color(0xFF56278C)
    val violet700 = Color(0xFF7039B2)
    val violet600 = Color(0xFF8A4FD4) // anchor — silent
    val violet500 = Color(0xFFA468F3)
    val violet400 = Color(0xFFB98BFF)
    val violet300 = Color(0xFFCDB0FF)
    val violet200 = Color(0xFFE1D1FF)
    val violet100 = Color(0xFFEFE7FF)
    val violet50 = Color(0xFFF8F5FF)
}
