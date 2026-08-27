package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.uimodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Sailing
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloLinkMarkUiModel
import org.jetbrains.compose.resources.StringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_entry_access
import yadlo.shared.generated.resources.plus_entry_contact
import yadlo.shared.generated.resources.plus_entry_newsletter
import yadlo.shared.generated.resources.plus_entry_payment
import yadlo.shared.generated.resources.plus_entry_story
import yadlo.shared.generated.resources.plus_entry_volunteering

/**
 * The six things Accueil is allowed to promote, and everything fixed about each one.
 *
 * **This is a shortlist, not a second copy of the Plus tab.** Sixteen rows live over there and six
 * appear here, because a row is only worth promoting where the thing behind it is *actionable at
 * this point in the year* — the payment rule while you can still stop at an ATM, the newsletter on
 * the Monday after. The UiMapper decides which of the six a Phase gets; this enum decides nothing
 * except what each one looks like when it is chosen.
 *
 * **The labels are the Plus tab's own strings, deliberately.** Two rows that open the same screen
 * have to call it the same thing, or the app has two names for one page and the reader has to work
 * out that they match. The `plus_` prefix records where the words were written first, not who is
 * allowed to read them. The rows themselves are the Plus tab's too — see `QuickAccessBlock`.
 *
 * [mark] is where the tap goes, drawn in the row's trailing column exactly as the Plus tab draws it.
 * Everything here stays in the app but the newsletter, and that one row is why the column earns its
 * keep: on a beach with one bar of signal, whether a tap is about to cost a page load is worth a
 * glyph. `QuickAccessEntryUiModelTest` is what keeps a second leaving entry from being added
 * without `HomeRoute` learning to send it out.
 */
enum class QuickAccessEntryUiModel(
    val title: StringResource,
    val icon: ImageVector,
    val mark: YadloLinkMarkUiModel = YadloLinkMarkUiModel.DISCLOSURE,
) {
    PAYMENT(Res.string.plus_entry_payment, Icons.Outlined.CreditCard),
    ACCESS(Res.string.plus_entry_access, Icons.Outlined.DirectionsBus),

    /**
     * *Devenir bénévole*, and it is promoted in ANNOUNCED alone. Recruiting is a campaign rather
     * than a standing fact: the association staffs the edition once the programme exists, so an
     * off-season Accueil asking for volunteers is asking before there is anything to volunteer for.
     */
    VOLUNTEERING(Res.string.plus_entry_volunteering, Icons.Outlined.VolunteerActivism),

    /**
     * *Nous écrire*, and OFF_SEASON's version of the same instinct. Between editions the useful
     * offer is not "come and help" but "here is how to reach us" — which is also the months when
     * anyone reaching out gets an answer.
     */
    CONTACT(Res.string.plus_entry_contact, Icons.Outlined.MailOutline),
    STORY(Res.string.plus_entry_story, Icons.Outlined.Sailing),
    NEWSLETTER(Res.string.plus_entry_newsletter, Icons.AutoMirrored.Outlined.Send, YadloLinkMarkUiModel.EXTERNAL),
}
