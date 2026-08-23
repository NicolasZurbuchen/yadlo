package io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Sailing
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_entry_about
import yadlo.shared.generated.resources.plus_entry_access
import yadlo.shared.generated.resources.plus_entry_assistance
import yadlo.shared.generated.resources.plus_entry_contact
import yadlo.shared.generated.resources.plus_entry_faq
import yadlo.shared.generated.resources.plus_entry_hours
import yadlo.shared.generated.resources.plus_entry_notifications
import yadlo.shared.generated.resources.plus_entry_partners
import yadlo.shared.generated.resources.plus_entry_payment
import yadlo.shared.generated.resources.plus_entry_privacy
import yadlo.shared.generated.resources.plus_entry_responsible
import yadlo.shared.generated.resources.plus_entry_stands_food
import yadlo.shared.generated.resources.plus_entry_stands_makers
import yadlo.shared.generated.resources.plus_entry_story
import yadlo.shared.generated.resources.plus_entry_volunteering

/**
 * The presentation twin of the domain `SearchTopic`, for the same reason `PhaseUiModel` is one: the
 * matching is a domain decision and what a result is called is not, so the enum crosses at the Store
 * boundary and the UiMapper never imports the domain.
 *
 * **The titles are the Plus tab's own strings, deliberately reused.** A screen has one name, and a
 * search result that called *Accès & transports* something else would be a second name for the same
 * place — findable, tappable, and wrong the moment either is edited. What the search adds is the
 * vocabulary for finding it, which lives on the domain enum and is never displayed.
 *
 * The icons are matched to `PlusEntryUiModel`'s for the same reason: a reader who has seen the row
 * on the Plus tab should recognise it here without reading it again.
 */
enum class SearchTopicUiModel(
    val title: StringResource,
    val icon: ImageVector,
) {
    STANDS_FOOD(Res.string.plus_entry_stands_food, Icons.Outlined.Restaurant),
    STANDS_MAKERS(Res.string.plus_entry_stands_makers, Icons.Outlined.Storefront),
    PAYMENT(Res.string.plus_entry_payment, Icons.Outlined.CreditCard),
    ACCESS(Res.string.plus_entry_access, Icons.Outlined.DirectionsBus),
    HOURS(Res.string.plus_entry_hours, Icons.Outlined.Schedule),
    ASSISTANCE(Res.string.plus_entry_assistance, Icons.Outlined.MedicalServices),
    FAQ(Res.string.plus_entry_faq, Icons.AutoMirrored.Outlined.HelpOutline),
    STORY(Res.string.plus_entry_story, Icons.Outlined.Sailing),
    RESPONSIBLE(Res.string.plus_entry_responsible, Icons.Outlined.Park),
    PARTNERS(Res.string.plus_entry_partners, Icons.Outlined.Groups),
    VOLUNTEERING(Res.string.plus_entry_volunteering, Icons.Outlined.VolunteerActivism),
    CONTACT(Res.string.plus_entry_contact, Icons.Outlined.MailOutline),
    NOTIFICATIONS(Res.string.plus_entry_notifications, Icons.Outlined.NotificationsNone),
    PRIVACY(Res.string.plus_entry_privacy, Icons.Outlined.Lock),
    ABOUT(Res.string.plus_entry_about, Icons.Outlined.Info),
}
