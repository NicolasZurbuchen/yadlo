package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * A standing call to action the content publishes at the top of `festival.json` — not tied to an
 * Edition, and the same all year.
 *
 * **The app knows these by name rather than by string.** Both of them used to be matched with a
 * `private const val NEWSLETTER_LINK_ID = "newsletter"` and a `firstOrNull { it.id == ... }`, once
 * in [io.nicolaszurbuchen.yadlo.feature.home.domain.usecase.ObserveHomeContentUseCase] and again in
 * `ObservePlusOverviewUseCase`. Two copies of a string that a third file — the content — has to
 * agree with, and nothing anywhere checking that it does: change the published id and the newsletter
 * simply stops appearing, on two screens, with nothing failing and nothing logged.
 *
 * The id is resolved once, in `FestivalRemoteMapper`, and nothing downstream ever sees the raw
 * string. A published id this build has no name for drops there, which is where every other unknown
 * value in this codebase drops — the same call `PlanRepositoryImpl` makes for a `SavedKind` written
 * by a version that knew more than this one.
 *
 * [DONATION] is here although no screen offers it yet. The enum's job is to name the set the content
 * publishes, and a link that exists in `festival.json` but nowhere in the domain is published data
 * lost at the boundary — the same silent failure this exists to stop. Adding the row is then a UI
 * change and nothing else.
 */
enum class StandingLink(
    val id: String,
) {
    NEWSLETTER("newsletter"),
    DONATION("don"),
}
