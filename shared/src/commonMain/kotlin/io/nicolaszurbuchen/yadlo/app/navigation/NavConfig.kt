package io.nicolaszurbuchen.yadlo.app.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation.HappeningDestination
import io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation.AnnouncementsDestination
import io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation.HomeMainDestination
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.navigation.MonYadloMainDestination
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.navigation.WishlistDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AboutDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AccessDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AssistanceDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.ClearDataDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.ContactDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.FaqDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.HoursDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.NotificationsDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PartnersDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PaymentDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PlusMainDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PrivacyDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.ResponsibleDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.StandsDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.StoryDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.VolunteeringDestination
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.navigation.ProgrammeDestination
import io.nicolaszurbuchen.yadlo.feature.search.presentation.navigation.SearchDestination
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * Every destination that can sit on a back stack has to be registered here, or it cannot be
 * written to saved state and the stack it is on comes back empty after process death. Each tab
 * keeps its own stack, so a missing entry loses that tab's depth rather than the whole app's.
 *
 * Registration is per concrete key, not per feature: the sealed roots each feature declares are
 * what group them in their own file, and polymorphic serialization still needs to be told about
 * every leaf by name.
 */
val navConfig =
    SavedStateConfiguration {
        serializersModule =
            SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(HomeMainDestination::class)
                    subclass(AnnouncementsDestination::class)
                    subclass(ProgrammeDestination::class)
                    subclass(MonYadloMainDestination::class)
                    subclass(WishlistDestination::class)
                    subclass(PlusMainDestination::class)
                    subclass(StandsDestination::class)
                    subclass(PaymentDestination::class)
                    subclass(AccessDestination::class)
                    subclass(HoursDestination::class)
                    subclass(AssistanceDestination::class)
                    subclass(FaqDestination::class)
                    subclass(StoryDestination::class)
                    subclass(PartnersDestination::class)
                    subclass(ContactDestination::class)
                    subclass(VolunteeringDestination::class)
                    subclass(ResponsibleDestination::class)
                    subclass(NotificationsDestination::class)
                    subclass(AboutDestination::class)
                    subclass(PrivacyDestination::class)
                    subclass(ClearDataDestination::class)
                    subclass(HappeningDestination::class)
                    subclass(SearchDestination::class)
                }
            }
    }
