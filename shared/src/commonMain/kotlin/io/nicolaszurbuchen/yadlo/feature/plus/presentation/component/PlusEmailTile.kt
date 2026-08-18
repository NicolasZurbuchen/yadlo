package io.nicolaszurbuchen.yadlo.feature.plus.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloLinkTile
import io.nicolaszurbuchen.yadlo.app.design.uimodel.LinkMarkUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.uimodel.PlusEmailUiModel

/**
 * An address offered to be written to — the concern it covers, who is behind it, and where the mail
 * goes.
 *
 * A component rather than the same three lines on two screens: *Nous écrire* and *Devenir Hot'Staff*
 * offer the identical thing, and the only reason they used to look different is that one was written
 * after the other. Whichever screen a reader reaches `staff@yadlo.ch` through, it should be the same
 * object.
 *
 * The label is what a reader scans; the name is what turns writing into a role into writing to
 * somebody, which for a committee of volunteers is most of whether a reply happens.
 */
@Composable
fun PlusEmailTile(
    email: PlusEmailUiModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    YadloLinkTile(
        label = email.label,
        mark = LinkMarkUiModel.MAIL,
        onClick = { onClick(email.address) },
        sublabel = listOfNotNull(email.responsible, email.address).joinToString(SUBLABEL_SEPARATOR),
        modifier = modifier,
    )
}

// A middle dot rather than a dash: the two halves are a person and an address, not a phrase and its
// continuation, and a hyphen between "Jeremy B." and "musique@yadlo.ch" reads as part of the address.
private const val SUBLABEL_SEPARATOR = " · "
