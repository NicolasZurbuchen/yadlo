package io.nicolaszurbuchen.yadlo.app.navigation

import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation.HappeningDestination
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.navigation.WishlistDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AboutDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AccessDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AccessibilityDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AssistanceDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.ContactDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.FaqDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.HoursDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PageDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PartnersDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PaymentDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PrivacyDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.StandsDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.StoryDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page.PageKind
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

    @Test
    fun navConfig_everyPlusDestination_isRegisteredForSavedState() {
        // Plus is the deepest stack in the app — seven screens hang off one root — so it is also
        // where a forgotten registration costs the most. Listed rather than looped because there is
        // no enum of them: the tab's rows are built from the content, not from a fixed set.
        listOf(
            StandsDestination,
            PaymentDestination,
            AccessDestination,
            AccessibilityDestination,
            HoursDestination,
            AssistanceDestination,
            FaqDestination,
            StoryDestination,
            PartnersDestination,
            ContactDestination,
            AboutDestination,
            PrivacyDestination,
            // The one that carries a value: a restored stack has to come back on the same page,
            // not on whichever the enum happens to declare first.
            PageDestination(PageKind.SOCIAL),
        ).forEach { destination ->
            assertNotNull(
                navConfig.serializersModule.getPolymorphic(NavKey::class, destination),
                "$destination is not registered in navConfig",
            )
        }
    }
}
