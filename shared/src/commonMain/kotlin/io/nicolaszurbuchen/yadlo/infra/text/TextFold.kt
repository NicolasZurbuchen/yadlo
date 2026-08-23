package io.nicolaszurbuchen.yadlo.infra.text

/**
 * Lowercased and stripped of its accents, so that `preverenges` finds *Préverenges* and `cafe`
 * finds *café*.
 *
 * **A table rather than `java.text.Normalizer`.** There is no `Normalizer` in commonMain, and the
 * two platform answers — `Normalizer.normalize` on Android, `CFStringTransform` on iOS — would make
 * this an `expect`/`actual` pair with two implementations to keep in agreement and neither testable
 * on the host JVM. French has about thirty accented letters and the app is French only (SPEC.md §
 * Out of Scope), so the table is the whole alphabet rather than a subset of one, and one commonMain
 * function is the entire surface.
 *
 * It is deliberately not a general Unicode fold: it does not touch Greek, Cyrillic or the Latin
 * letters no French word carries. A name the content publishes in another script still matches
 * itself, because an unmapped character is passed through unchanged.
 *
 * The curly apostrophe folds to the straight one because both are typed at the festival's own
 * spelling of *l'entrée* and neither reader should have to guess which one the content used.
 */
fun String.foldForSearch(): String =
    buildString(length) {
        for (char in this@foldForSearch.lowercase()) {
            val index = ACCENTED.indexOf(char)
            when {
                index >= 0 -> append(PLAIN[index])
                char == 'œ' -> append("oe")
                char == 'æ' -> append("ae")
                char == '’' -> append('\'')
                else -> append(char)
            }
        }
    }

/**
 * Positionally paired with [PLAIN] — the character at index *n* of one folds to the character at
 * index *n* of the other, which is why `TextFoldTest` asserts the two are the same length before it
 * asserts anything else. The ligatures are handled beside the table instead of in it, because they
 * fold to two characters and a positional pair cannot express that.
 */
private const val ACCENTED = "àâäáãåçéèêëíìîïñóòôöõúùûüýÿ"

private const val PLAIN = "aaaaaaceeeeiiiinooooouuuuyy"
