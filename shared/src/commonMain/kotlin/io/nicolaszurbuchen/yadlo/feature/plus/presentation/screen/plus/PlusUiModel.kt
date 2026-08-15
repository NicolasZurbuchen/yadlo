package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Accessible
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.ui.graphics.vector.ImageVector
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import org.jetbrains.compose.resources.StringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_entry_access
import yadlo.shared.generated.resources.plus_entry_accessibility
import yadlo.shared.generated.resources.plus_entry_assistance
import yadlo.shared.generated.resources.plus_entry_faq
import yadlo.shared.generated.resources.plus_entry_hours
import yadlo.shared.generated.resources.plus_entry_payment
import yadlo.shared.generated.resources.plus_entry_stands
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
) {
    STANDS(Res.string.plus_entry_stands, Icons.Outlined.Restaurant),
    PAYMENT(Res.string.plus_entry_payment, Icons.Outlined.CreditCard),
    ACCESS(Res.string.plus_entry_access, Icons.Outlined.DirectionsBus),
    ACCESSIBILITY(Res.string.plus_entry_accessibility, Icons.AutoMirrored.Outlined.Accessible),
    HOURS(Res.string.plus_entry_hours, Icons.Outlined.Schedule),
    ASSISTANCE(Res.string.plus_entry_assistance, Icons.Outlined.MedicalServices),
    FAQ(Res.string.plus_entry_faq, Icons.AutoMirrored.Outlined.HelpOutline),
}

data class PlusGroupUiModel(
    val id: PlusGroupUiId,
    val rows: List<PlusRowUiModel>,
)

/** [subtitle] is the little the row can say from the content before you open it. */
data class PlusRowUiModel(
    val entry: PlusEntry,
    val subtitle: UiText?,
)
