package io.nicolaszurbuchen.yadlo.app.navigation

import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation.HappeningDestination
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.navigation.WishlistDestination
import kotlin.test.Test
import kotlin.test.assertNotNull

class NavConfigTest {
    // A destination missing from navConfig cannot be written to saved state, so the tab holding
    // it comes back empty after process death - and nothing fails at build time or on the happy
    // path. These tests are the only thing standing between a forgotten `subclass(...)` line and
    // a bug that only appears on a low-memory phone.

    @Test
    fun navConfig_everyTabRoot_isRegisteredForSavedState() {
        Tab.entries.forEach { tab ->
            assertNotNull(
                navConfig.serializersModule.getPolymorphic(NavKey::class, tab.root),
                "Tab ${tab.name} root ${tab.root} is not registered in navConfig",
            )
        }
    }

    @Test
    fun navConfig_happeningDestination_isRegisteredForSavedState() {
        val destination = HappeningDestination(happeningId = "dubside")

        assertNotNull(navConfig.serializersModule.getPolymorphic(NavKey::class, destination))
    }

    @Test
    fun navConfig_wishlistDestination_isRegisteredForSavedState() {
        // Pushed onto Mon Yadlo's own stack, so a missing registration loses the Wishlist and
        // drops the visitor back onto the timeline after a low-memory kill.
        assertNotNull(navConfig.serializersModule.getPolymorphic(NavKey::class, WishlistDestination))
    }
}
