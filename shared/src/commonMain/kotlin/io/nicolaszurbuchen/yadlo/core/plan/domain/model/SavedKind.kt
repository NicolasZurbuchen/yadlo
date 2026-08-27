package io.nicolaszurbuchen.yadlo.core.plan.domain.model

/**
 * Which of the two things a heart tap saved — DECISIONS.md § Two verbs: Plan and Wishlist.
 *
 * There is one heart in the UI and the app decides the bucket from what was tapped: a date row on a
 * fiche saves that [SLOT], the bar button on a Stand's fiche saves the [STAND] itself. The
 * distinction is not cosmetic — a Slot lands on a timeline and could carry a reminder, a Stand lands
 * on a checklist that has no times on it at all.
 */
enum class SavedKind {
    /** An Artist or Activity Slot. Stands never reach the timeline, however long their hours. */
    SLOT,

    /** The whole Stand, never one of its menu items — DECISIONS.md § Wishlist granularity. */
    STAND,
}
