package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Accessible
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Sailing
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Share
import androidx.compose.ui.graphics.vector.ImageVector
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import org.jetbrains.compose.resources.StringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_entry_about
import yadlo.shared.generated.resources.plus_entry_access
import yadlo.shared.generated.resources.plus_entry_accessibility
import yadlo.shared.generated.resources.plus_entry_assistance
import yadlo.shared.generated.resources.plus_entry_contact
import yadlo.shared.generated.resources.plus_entry_faq
import yadlo.shared.generated.resources.plus_entry_hours
import yadlo.shared.generated.resources.plus_entry_newsletter
import yadlo.shared.generated.resources.plus_entry_partners
import yadlo.shared.generated.resources.plus_entry_payment
import yadlo.shared.generated.resources.plus_entry_privacy
import yadlo.shared.generated.resources.plus_entry_report
import yadlo.shared.generated.resources.plus_entry_responsible
import yadlo.shared.generated.resources.plus_entry_social
import yadlo.shared.generated.resources.plus_entry_stands
import yadlo.shared.generated.resources.plus_entry_story
import yadlo.shared.generated.resources.plus_group_app
import yadlo.shared.generated.resources.plus_group_festival
import yadlo.shared.generated.resources.plus_group_involvement
import yadlo.shared.generated.resources.plus_group_on_site

/**
 * The root of Plus: four cards of rows over everything the festival is that is not its programme.
 *
 * A null-free model — a row that has no section behind it was never built, so the screen never has
 * to decide whether to draw one.
 */
data class PlusUiModel(
    val isLoading: Boolean,
    val groups: List<PlusGroupUiModel>,
)

/**
 * **The groups are not the website's menu, and their order is the point.** They follow what a
 * reader is doing rather than how the association files things: what serves you on site, what tells
 * you about the festival, what asks something of you, and the app itself. That is why payment is
 * the third row of the whole tab instead of being buried, and why it is a decision that lives here
 * — in the screen — rather than in the content or the domain.
 *
 * Declaration order *is* display order, the same contract [io.nicolaszurbuchen.yadlo.app.navigation.Tab]
 * holds for the bottom bar.
 */
enum class PlusGroupUiId(
    val title: StringResource,
) {
    ON_SITE(Res.string.plus_group_on_site),
    FESTIVAL(Res.string.plus_group_festival),
    INVOLVEMENT(Res.string.plus_group_involvement),
    APP(Res.string.plus_group_app),
}

/**
 * One row of the root, and the only thing the screen hands back when it is tapped.
 *
 * The title and icon hang off the entry rather than off the row, because they are fixed properties
 * of *which entry this is* — nothing about the content can change them, and putting them here keeps
 * the mapper deciding only what it actually decides: which rows exist, and what each one says
 * underneath.
 */
enum class PlusEntry(
    val title: StringResource,
    val icon: ImageVector,
    /**
     * Where the tap goes, said on the row itself. `›` stays inside, `↗` leaves for the browser, `✉`
     * opens mail — SPEC.md § Interaction rules, and the reason a reader on one bar of signal knows
     * before tapping whether it is about to cost them a page load.
     */
    val mark: String = DISCLOSURE,
) {
    STANDS(Res.string.plus_entry_stands, Icons.Outlined.Restaurant),
    PAYMENT(Res.string.plus_entry_payment, Icons.Outlined.CreditCard),
    ACCESS(Res.string.plus_entry_access, Icons.Outlined.DirectionsBus),
    ACCESSIBILITY(Res.string.plus_entry_accessibility, Icons.AutoMirrored.Outlined.Accessible),
    HOURS(Res.string.plus_entry_hours, Icons.Outlined.Schedule),
    ASSISTANCE(Res.string.plus_entry_assistance, Icons.Outlined.MedicalServices),
    FAQ(Res.string.plus_entry_faq, Icons.AutoMirrored.Outlined.HelpOutline),
    STORY(Res.string.plus_entry_story, Icons.Outlined.Sailing),
    RESPONSIBLE(Res.string.plus_entry_responsible, Icons.Outlined.Park),
    PARTNERS(Res.string.plus_entry_partners, Icons.Outlined.Groups),
    CONTACT(Res.string.plus_entry_contact, Icons.Outlined.MailOutline),
    NEWSLETTER(Res.string.plus_entry_newsletter, Icons.AutoMirrored.Outlined.Send, EXTERNAL),
    SOCIAL(Res.string.plus_entry_social, Icons.Outlined.Share),
    ABOUT(Res.string.plus_entry_about, Icons.Outlined.Info),
    REPORT(Res.string.plus_entry_report, Icons.Outlined.Flag, MAIL),
    PRIVACY(Res.string.plus_entry_privacy, Icons.Outlined.Lock),
}

private const val DISCLOSURE = "›"
private const val EXTERNAL = "↗"
private const val MAIL = "✉"

data class PlusGroupUiModel(
    val id: PlusGroupUiId,
    val rows: List<PlusRowUiModel>,
)

/** [subtitle] is the little the row can say from the content before you open it. */
data class PlusRowUiModel(
    val entry: PlusEntry,
    val subtitle: UiText?,
)
