package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Assistance

/**
 * *En cas de besoin* — one screen for the situation rather than three entries for three subjects.
 *
 * [numbers] come first because they are the only content in this app that is true whatever happens
 * and needs nobody's confirmation. The first aid post and the children's meeting point are still
 * unpublished; [recognition] is the one thing the prototype put under the numbers that the content
 * now carries, so the screen says it and still says nothing it cannot stand behind.
 */
data class AssistanceGuide(
    val numbers: List<Assistance.EmergencyNumber>,
    /** How to tell who works here. Empty until the content says. */
    val recognition: List<Assistance.Recognition>,
    /** Where a bag goes once the festival has packed up. Null when the id resolves to nothing. */
    val lostPropertyEmail: String?,
)
