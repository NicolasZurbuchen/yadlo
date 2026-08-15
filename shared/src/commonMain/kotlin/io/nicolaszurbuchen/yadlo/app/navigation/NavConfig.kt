package io.nicolaszurbuchen.yadlo.app.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation.HappeningDestination
import io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation.AnnouncementsDestination
import io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation.HomeDestination
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.navigation.MonYadloDestination
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
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PlusDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PrivacyDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.StandsDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.StoryDestination
import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.presentation.navigation.DetailDestination
import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.presentation.navigation.MainDestination
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.navigation.ProgrammeDestination
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * Every destination that can sit on a back stack has to be registered here, or it cannot be
 * written to saved state and the stack it is on comes back empty after process death. Each tab
 * keeps its own stack, so a missing entry loses that tab's depth rather than the whole app's.
 */
val navConfig =
    SavedStateConfiguration {
        serializersModule =
            SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(HomeDestination::class)
                    subclass(AnnouncementsDestination::class)
                    subclass(ProgrammeDestination::class)
                    subclass(MonYadloDestination::class)
                    subclass(WishlistDestination::class)
                    subclass(PlusDestination::class)
                    subclass(StandsDestination::class)
                    subclass(PaymentDestination::class)
                    subclass(AccessDestination::class)
                    subclass(AccessibilityDestination::class)
                    subclass(HoursDestination::class)
                    subclass(AssistanceDestination::class)
                    subclass(FaqDestination::class)
                    subclass(StoryDestination::class)
                    subclass(PartnersDestination::class)
                    subclass(ContactDestination::class)
                    subclass(PageDestination::class)
                    subclass(AboutDestination::class)
                    subclass(PrivacyDestination::class)
                    subclass(HappeningDestination::class)

                    // Template example feature, unreachable from the tab shell. Registered only
                    // so its screens still restore while it is kept as a working reference.
                    subclass(MainDestination::class)
                    subclass(DetailDestination::class)
                }
            }
    }
