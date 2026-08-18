package io.nicolaszurbuchen.yadlo.common.content.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DietaryCoverageTest {
    @Test
    fun dietaryCoverage_aMarkEveryDishCarries_coversTheWholeStand() {
        assertEquals(
            mapOf("vegan" to DietaryCoverage.ALL),
            stand(listOf("vegan"), listOf("vegan")).dietaryCoverage(),
        )
    }

    @Test
    fun dietaryCoverage_aMarkOnlySomeDishesCarry_isAnOption() {
        assertEquals(
            mapOf("vegan" to DietaryCoverage.ALL, "piquant" to DietaryCoverage.SOME),
            stand(listOf("vegan", "piquant"), listOf("vegan")).dietaryCoverage(),
        )
    }

    @Test
    fun dietaryCoverage_oneUntaggedDish_isEnoughToMakeEveryMarkAnOption() {
        // One forgotten drink is the difference between "100 % végan" and "options véganes", and
        // being wrong in this direction is the safe one.
        assertEquals(
            mapOf("vegan" to DietaryCoverage.SOME),
            stand(listOf("vegan"), emptyList()).dietaryCoverage(),
        )
    }

    @Test
    fun dietaryCoverage_aStandWithNoMenu_answersNothingRatherThanEverything() {
        // Vacuously true is the wrong answer here: `all` over an empty list would make every
        // unpublished stand match every chip, which is the one failure a dietary filter must not
        // have.
        assertTrue(stand().dietaryCoverage().isEmpty())
    }

    @Test
    fun dietaryCoverage_readsAcrossGroups_becauseADrinkIsADishToo() {
        val standWithTwoGroups =
            base().copy(
                menu =
                    listOf(
                        group("plats", listOf(item(listOf("vegan")))),
                        group("boissons", listOf(item(listOf("vegan", "sans-gluten")))),
                    ),
            )

        // A stand whose plats are vegan and whose boissons are not is not a vegan stand, and the
        // split into groups is a heading rather than a boundary.
        assertEquals(
            mapOf("vegan" to DietaryCoverage.ALL, "sans-gluten" to DietaryCoverage.SOME),
            standWithTwoGroups.dietaryCoverage(),
        )
    }

    private fun stand(vararg dishes: List<String>) = base().copy(menu = listOf(group("plats", dishes.map(::item))))

    private fun base() =
        Happening.Stand(
            id = "vegan-fabrik",
            name = "Vegan Fabrik",
            category = Category(id = "restauration", name = "Restauration", order = 6),
            description = null,
            images = emptyList(),
            provenance = Provenance.CONFIRMED,
            offering = null,
            links = emptyList(),
            menu = emptyList(),
        )

    private fun group(
        id: String,
        items: List<MenuGroup.Item>,
    ) = MenuGroup(id = id, name = id, description = null, source = null, items = items)

    private fun item(marks: List<String>) =
        MenuGroup.Item(
            name = "Plat",
            price = null,
            description = null,
            marks = marks,
            provenance = Provenance.UNVERIFIED,
        )
}
