package io.nicolaszurbuchen.yadlo.infra.text

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TextFoldTest {
    @Test
    fun foldForSearch_thePlaceName_isFoundWithoutItsAccent() {
        // The case SPEC.md names: "preverenges" has to reach Préverenges.
        assertEquals("preverenges", "Préverenges".foldForSearch())
    }

    @Test
    fun foldForSearch_everyAccentedVowelFrenchUses_losesItsAccent() {
        assertEquals("aaaaaaeeeeiiiiooooouuuuyy", "àâäáãåéèêëíìîïóòôöõúùûüýÿ".foldForSearch())
    }

    @Test
    fun foldForSearch_theTwoConsonants_fold() {
        assertEquals("ca ne", "Ça ñe".foldForSearch())
    }

    @Test
    fun foldForSearch_theLigatures_becomeTwoLetters() {
        assertEquals("coeur", "cœur".foldForSearch())
        assertEquals("taenia", "tænia".foldForSearch())
    }

    @Test
    fun foldForSearch_theCurlyApostrophe_becomesTheStraightOne() {
        // The festival writes l’entrée one way and a phone keyboard types it the other. Neither
        // reader should have to guess which.
        assertEquals("l'entree", "l’entrée".foldForSearch())
    }

    @Test
    fun foldForSearch_isCaseInsensitive() {
        assertEquals("dj alf", "DJ ALF".foldForSearch())
    }

    @Test
    fun foldForSearch_aFoldedQuery_isASubstringOfItsFoldedTarget() {
        // The property the whole feature rests on: fold both sides and compare.
        assertTrue("cafe".foldForSearch() in "Le Café des Sports".foldForSearch())
    }

    @Test
    fun foldForSearch_anythingItHasNoRuleFor_isLeftAlone() {
        // Not a general Unicode fold, on purpose — an unmapped character still matches itself.
        assertEquals("東京 42 ok", "東京 42 OK".foldForSearch())
    }

    @Test
    fun foldForSearch_anEmptyString_staysEmpty() {
        assertEquals("", "".foldForSearch())
    }
}
