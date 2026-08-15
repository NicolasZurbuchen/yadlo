package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

/**
 * What the published content can currently answer, and the little of it the root list writes on a
 * row before you open it.
 *
 * **The four groups and their order are not here.** That they are *Sur place · Le festival ·
 * S'impliquer · L'application* rather than the website's own menu is a design decision, made once,
 * and it belongs to the screen. What the domain owns is narrower and changes on its own: whether
 * the content has anything to say. A row whose section was never published does not get drawn, so
 * the tab can never open a screen with nothing on it.
 *
 * [cashAccepted] is nullable and the booleans are not, because the three answers are genuinely
 * different: cash refused is the fact worth writing on the row, cash accepted is worth saying
 * nothing about, and no payment section at all is a row that should not exist.
 */
data class PlusOverview(
    val standCount: Int,
    val cashAccepted: Boolean?,
    val hasTransport: Boolean,
    val hasAccessibility: Boolean,
    val hasOpeningHours: Boolean,
    val hasAssistance: Boolean,
    val faqCount: Int,
)
