package io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel

/**
 * The span every bar under it is drawn against, as three readings.
 *
 * Three rather than an hourly ruler: the bar is for the shape of a day, not for reading a time off,
 * and the exact hours are written on the row itself. Once above the list rather than once per row is
 * what keeps this from becoming the right-hand time column layout B2 exists to avoid.
 *
 * It sits in `common/content` beside [SlotSegmentUiModel] because the Programme and Mon Yadlo both
 * draw segments and both owe the reader the axis those segments are measured on. What the two
 * screens do differ on is which span it describes — see each UiMapper.
 */
data class SlotScaleUiModel(
    val startText: String,
    val middleText: String,
    val endText: String,
)
