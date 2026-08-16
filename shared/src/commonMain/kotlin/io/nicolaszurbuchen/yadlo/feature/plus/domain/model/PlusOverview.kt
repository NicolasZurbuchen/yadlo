package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.SocialLink

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
    /**
     * Counted apart because they are two entries: nobody looking for dinner is also browsing for a
     * second-hand costume, and a row is only drawn when its own half has something in it.
     */
    val foodStandCount: Int,
    val makerStandCount: Int,
    val cashAccepted: Boolean?,
    val hasTransport: Boolean,
    val hasAccessibility: Boolean,
    val hasOpeningHours: Boolean,
    val hasAssistance: Boolean,
    val faqCount: Int,
    val foundedYear: Int?,
    val charterNames: List<String>,
    val partnerCount: Int,
    /** False when the edition has closed its applications — recruiting is a campaign, not a fact. */
    val hasVolunteering: Boolean,
    val hasContact: Boolean,
    /**
     * The links themselves rather than a count, because the tab now *draws* them at its foot
     * instead of counting them into a row that opened a screen of four links.
     */
    val socials: List<SocialLink>,
    /**
     * The two rows that leave the app from the root rather than opening a screen. Carried as URLs
     * because the tab is where they are tapped, and a row that cannot say where it goes should not
     * be drawn at all.
     */
    val newsletterUrl: String?,
    val reportEmail: String?,
)
