package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * The live-truth file, narrowed to what the loading chain actually reads.
 *
 * `festival.json` also carries the Plus sections — histoire, faq, contact, transports, paiement,
 * accessibilité — and they are deliberately **not** modelled here yet. Content nobody reads is
 * content nobody notices going stale, which is why `entry` and `openingNote` were removed rather
 * than kept warm. `ignoreUnknownKeys` means adding them later costs nothing.
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
)
