package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Assistance

/**
 * *En cas de besoin* — one screen for the situation rather than three entries for three subjects.
 *
 * [numbers] come first because they are the only content in this app that is true whatever happens
 * and needs nobody's confirmation. Everything the prototype put under them — the first aid post,
 * the children's meeting point, how to recognise a volunteer — is unpublished, so the screen is
 * currently its most reliable half and says nothing it cannot stand behind.
 */
data class AssistanceGuide(
    val numbers: List<Assistance.EmergencyNumber>,
    /** Where a bag goes once the festival has packed up. Null when the id resolves to nothing. */
    val lostPropertyEmail: String?,
)
