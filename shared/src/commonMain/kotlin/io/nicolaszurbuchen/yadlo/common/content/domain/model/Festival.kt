package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * The live-truth file: everything that is answered the same way whatever year you ask.
 *
 * The test for what belongs here rather than on an [Edition] is not "does it change every year" but
 * **would a past-edition archive need its own copy?** Browsing 2019 shows 2019's line-up and 2019's
 * figures, but *today's* payment rule and *today's* contact address.
 *
 * **The practical sections are nullable and default, and none of the four above them are.** Name,
 * tagline, current edition and networks are what the loading chain and Accueil are built on; the
 * rest is what one Plus screen each renders. A published file missing its transport block should
 * cost the visitor the transport screen, not the festival — which is the same tolerance
 * `ignoreUnknownKeys` gives in the other direction, for content that grows ahead of the app.
 *
 * The defaults exist for the same reason and are not a convenience: "not published" is a state
 * every one of these screens is built around, so it is also the right thing for a test that is
 * about the programme to leave unsaid. `FestivalRemoteMapper` names every field and its test
 * asserts the whole mapping, so a field left out there is caught rather than silently defaulted.
 */
data class Festival(
    val name: String,
    val tagline: String,
    /** Moving this is how a new edition ships without an app release. */
    val currentEditionId: String,
    /**
     * Below this the app shows a soft update row in Plus, and never anything harder. An unofficial
     * festival app that bricks itself on the Saturday afternoon is worse than one showing week-old
     * data. Null means no minimum has been set.
     */
    val minSupportedAppVersion: String?,
    /**
     * Modelled here rather than left to the Plus sections because Accueil shows them in four of the
     * five phases: on the 361 days when nothing is happening, the networks are where the festival
     * actually is.
     */
    val social: List<SocialLink>,
    /** Standing calls to action — the newsletter, the donation page. */
    val links: List<InfoLink> = emptyList(),
    val story: Story? = null,
    val faq: List<FaqEntry> = emptyList(),
    /** Flattened out of `responsable`, which wraps this list and holds nothing else. */
    val charters: List<Charter> = emptyList(),
    val contact: Contact? = null,
    val transport: Transport? = null,
    val payment: Payment? = null,
    val accessibility: Accessibility? = null,
    val assistance: Assistance? = null,
    val involvement: Involvement? = null,
)
