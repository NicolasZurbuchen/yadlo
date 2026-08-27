package io.nicolaszurbuchen.yadlo.core.content.domain.model

/**
 * How much of a Stand a dietary mark covers — all of what it sells, or only some of it.
 *
 * **The distinction someone scanning a row of trucks is actually making.** "Is vegan" and "has a
 * vegan option" are different answers to the same question, and a stand that answered only the
 * first left a reader with a dietary requirement to open six fiches and read six menus.
 */
enum class DietaryCoverage {
    /** Every dish carries it. `100 % végan`. */
    ALL,

    /** Some do. `Options véganes`. */
    SOME,
}

/**
 * What this Stand can feed you, derived from its own menu.
 *
 * **Derived rather than authored.** A Stand used to carry its own list of marks, meaning "all of it
 * is", beside its items' lists, meaning "this one is" — two levels that could contradict each other,
 * and that one content edit adding a non-vegan dish would silently make false. Reading the menu
 * cannot be wrong about the menu.
 *
 * Which is also why **an untagged dish counts against every mark**: one forgotten drink is the
 * difference between `100 % végan` and `options véganes`, and being wrong in that direction is the
 * safe one. A Stand with no menu published answers nothing rather than answering "all".
 */
fun Happening.Stand.dietaryCoverage(): Map<String, DietaryCoverage> {
    val items = menu.flatMap { group -> group.items }

    if (items.isEmpty()) return emptyMap()

    return items
        .flatMap { it.marks }
        .distinct()
        .associateWith { mark ->
            if (items.all { mark in it.marks }) DietaryCoverage.ALL else DietaryCoverage.SOME
        }
}
