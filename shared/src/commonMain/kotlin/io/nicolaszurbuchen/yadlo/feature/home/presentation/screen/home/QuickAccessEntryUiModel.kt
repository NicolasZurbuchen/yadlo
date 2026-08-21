package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Sailing
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloLinkMarkUiModel
import org.jetbrains.compose.resources.StringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_entry_access
import yadlo.shared.generated.resources.plus_entry_newsletter
import yadlo.shared.generated.resources.plus_entry_payment
import yadlo.shared.generated.resources.plus_entry_story
import yadlo.shared.generated.resources.plus_entry_volunteering

/**
 * The five things Accueil is allowed to promote, and everything fixed about each one.
 *
 * **This is a shortlist, not a second copy of the Plus tab.** Sixteen rows live over there and five
 * appear here, because a tile is only worth drawing where the thing behind it is *actionable at
 * this point in the year* — the payment rule while you can still stop at an ATM, the newsletter on
 * the Monday after. The UiMapper decides which of the five a Phase gets; this enum decides nothing
 * except what each one looks like when it is chosen.
 *
 * **The labels are the Plus tab's own strings, deliberately.** A tile and a row that open the same
 * screen have to call it the same thing, or the app has two names for one page and the reader has
 * to work out that they match. The `plus_` prefix records where the words were written first, not
 * who is allowed to read them.
 *
 * [mark] is where the tap goes, and only [YadloLinkMarkUiModel.EXTERNAL] is ever drawn — a tile has
 * no trailing column to put a chevron in, and the chevron was already the mark that says nothing
 * the tap has not said. Leaving the app is the one fact the tile cannot convey on its own.
 * `QuickAccessEntryUiModelTest` is what keeps a second leaving entry from being added without
 * `HomeRoute` learning to send it out.
 */
enum class QuickAccessEntryUiModel(
    val title: StringResource,
    val icon: ImageVector,
    val mark: YadloLinkMarkUiModel = YadloLinkMarkUiModel.DISCLOSURE,
) {
    PAYMENT(Res.string.plus_entry_payment, Icons.Outlined.CreditCard),
    ACCESS(Res.string.plus_entry_access, Icons.Outlined.DirectionsBus),
    VOLUNTEERING(Res.string.plus_entry_volunteering, Icons.Outlined.VolunteerActivism),
    STORY(Res.string.plus_entry_story, Icons.Outlined.Sailing),
    NEWSLETTER(Res.string.plus_entry_newsletter, Icons.AutoMirrored.Outlined.Send, YadloLinkMarkUiModel.EXTERNAL),
}
