package io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel

/**
 * One Slot as a row draws it: when it runs, where that falls on the day, and where it is against
 * the clock.
 *
 * **A row is a Happening on a day, and it can hold several of these.** SUP Yoga runs three separate
 * hours on the Saturday, and as three rows it read as three activities that happen to share a name —
 * DECISIONS.md § A row is a Happening on a day.
 *
 * [timeText] and the two fractions are the same fact twice on purpose: the text is what a reader
 * takes away and the fractions are what the eye reads at a glance, and they must come from one
 * Slot or the bar and the line under it can disagree.
 *
 * [barStart] and [barEnd] are fractions of the **day's** span, not of the row's — the whole point of
 * the bar is comparing this Slot with the ones above and below it. They are here on every Slot,
 * finished ones included: a segment that vanished when its hour ended would take the shape of the
 * day with it, exactly when most of the list is past and reading the day is the whole job.
 *
 * [state] is per Slot rather than per row, because a row of three hours can have one that is over,
 * one running and one still to come. The row's own state is the loudest of them; this is what dims
 * the individual time and what raises the individual segment.
 *
 * It lives in `common/content` beside [SlotLiveStateUiModel] because the Programme's rows and Mon
 * Yadlo's draw the same segment against the same axis.
 */
data class SlotSegmentUiModel(
    val id: String,
    val timeText: String,
    val state: SlotLiveStateUiModel,
    val barStart: Float,
    val barEnd: Float,
)

/**
 * The state a row shows when it holds several Slots: the loudest one, never the first.
 *
 * *En cours* beats everything, because a row with something happening on it right now has exactly
 * one useful thing to say. *Terminé* is last and only wins unopposed — a row whose 14:00 is over and
 * whose 18:00 has not started is not finished, and saying so would hide the rest of the afternoon.
 * Upcoming outranks Over for the same reason and carries no label of its own, so a row in that state
 * simply says nothing, which is correct.
 *
 * Ties are broken by the order the Slots are in, which is chronological — so two Slots that are both
 * merely upcoming are represented by the nearer one, whose countdown is the one worth reading.
 *
 * It sits beside [SlotSegmentUiModel] rather than in a UiMapper because two UiMapper files need it
 * and each may hold nothing but its own State-to-UiModel function.
 */
fun List<SlotSegmentUiModel>.loudestState(): SlotLiveStateUiModel =
    maxByOrNull { segment ->
        when (segment.state) {
            is SlotLiveStateUiModel.Running, is SlotLiveStateUiModel.Ending -> RANK_LIVE
            is SlotLiveStateUiModel.StartingSoon -> RANK_SOON
            SlotLiveStateUiModel.Upcoming -> RANK_UPCOMING
            SlotLiveStateUiModel.Over -> RANK_OVER
        }
    }?.state ?: SlotLiveStateUiModel.Over

private const val RANK_LIVE = 3
private const val RANK_SOON = 2
private const val RANK_UPCOMING = 1
private const val RANK_OVER = 0
