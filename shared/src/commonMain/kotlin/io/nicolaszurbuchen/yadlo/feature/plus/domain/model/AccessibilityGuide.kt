package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Accessibility

/**
 * *Accessibilité*, split the way someone deciding whether to travel reads it.
 *
 * **Both lists matter and the second is not a failure.** "No accessible toilets" is something a
 * person needs before committing to thirty kilometres, and a page that only lists what works is the
 * reassuring, vague page SPEC.md story 40 exists to prevent.
 *
 * Both are empty today, because the festival publishes nothing on the subject. That is the honest
 * state rather than a gap to paper over, and it is why [contactEmail] is not decoration: when the
 * data is missing, somebody to ask is the most useful thing the screen can offer.
 *
 * There is deliberately no third list of open questions. The candidates exist — step-free routes,
 * a viewing spot at the stage, assistance dogs — but they live in content/GAPS.md, where they are
 * addressed to the association. Rendering them here would mean writing French content into Kotlin
 * and then having to ship a build to remove each one as it gets answered.
 */
data class AccessibilityGuide(
    val available: List<Accessibility.Item>,
    val unavailable: List<Accessibility.Item>,
    /** Null when the content names an address id that its own contact list does not hold. */
    val contactEmail: String?,
)
