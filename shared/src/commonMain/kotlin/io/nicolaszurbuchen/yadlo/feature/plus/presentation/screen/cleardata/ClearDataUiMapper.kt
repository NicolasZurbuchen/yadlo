package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.cleardata

import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.clear_data_images_action
import yadlo.shared.generated.resources.clear_data_images_body
import yadlo.shared.generated.resources.clear_data_images_empty
import yadlo.shared.generated.resources.clear_data_images_kilobytes
import yadlo.shared.generated.resources.clear_data_images_megabytes
import yadlo.shared.generated.resources.clear_data_images_title
import yadlo.shared.generated.resources.clear_data_saved_action
import yadlo.shared.generated.resources.clear_data_saved_body
import yadlo.shared.generated.resources.clear_data_saved_empty
import yadlo.shared.generated.resources.clear_data_saved_slots_one
import yadlo.shared.generated.resources.clear_data_saved_slots_other
import yadlo.shared.generated.resources.clear_data_saved_stands_one
import yadlo.shared.generated.resources.clear_data_saved_stands_other
import yadlo.shared.generated.resources.clear_data_saved_title

/**
 * **A half that is zero is absent, not written as zero.** *0 créneaux · 2 stands* spends the loudest
 * half of the line on something the visitor does not have, and the whole job of this line is telling
 * them what they are about to lose. Both halves empty is the one case that gets a sentence of its
 * own, because a line disappearing altogether reads as a screen that has not finished loading.
 *
 * **The size is rounded rather than exact, and it rounds *up* below a megabyte.** A cache holding
 * one byte is not "0 Ko" — that is the same reading as an empty one, on a row whose button is
 * enabled, which is the one contradiction this screen can produce. Above a megabyte it keeps a
 * tenth: the disk cache is capped at 128 MB and a realistic edition fills single-digit megabytes,
 * so whole numbers would put most of the festival's photographs at "4 Mo" whatever it did.
 */
fun ClearDataState.toUiModel(): ClearDataUiModel {
    val count = savedCount
    val bytes = imageCacheBytes

    val slots =
        count?.slots?.takeIf { it > 0 }?.let {
            UiText.Resource(
                if (it == 1) Res.string.clear_data_saved_slots_one else Res.string.clear_data_saved_slots_other,
                listOf(it),
            )
        }
    val stands =
        count?.stands?.takeIf { it > 0 }?.let {
            UiText.Resource(
                if (it == 1) Res.string.clear_data_saved_stands_one else Res.string.clear_data_saved_stands_other,
                listOf(it),
            )
        }
    val parts = listOfNotNull(slots, stands)

    val tenthsOfMegabyte = (bytes ?: 0L) * TENTHS / BYTES_PER_MEGABYTE

    return ClearDataUiModel(
        // Both halves, because the screen is one card of two rows and a skeleton that filled in one
        // of them while the other was still shimmering would read as the second one being empty.
        isLoading = count == null || bytes == null,
        saved =
            ClearDataRowUiModel(
                title = UiText.Resource(Res.string.clear_data_saved_title),
                body = UiText.Resource(Res.string.clear_data_saved_body),
                detail =
                    if (parts.isEmpty()) {
                        UiText.Resource(Res.string.clear_data_saved_empty)
                    } else {
                        // The middot comes from here rather than from either string, so the two
                        // halves can be shown alone without carrying a separator with them.
                        UiText.Composite(
                            parts.flatMapIndexed { index, part ->
                                if (index == 0) listOf(part) else listOf(UiText.Raw(SEPARATOR), part)
                            },
                        )
                    },
                action = UiText.Resource(Res.string.clear_data_saved_action),
                isEnabled = count?.isEmpty == false,
            ),
        images =
            ClearDataRowUiModel(
                title = UiText.Resource(Res.string.clear_data_images_title),
                body = UiText.Resource(Res.string.clear_data_images_body),
                detail =
                    when {
                        bytes == null || bytes == 0L -> {
                            UiText.Resource(Res.string.clear_data_images_empty)
                        }

                        bytes < BYTES_PER_MEGABYTE -> {
                            UiText.Resource(
                                Res.string.clear_data_images_kilobytes,
                                // Rounded up: anything on disk at all has to read as something.
                                listOf((bytes + BYTES_PER_KILOBYTE - 1) / BYTES_PER_KILOBYTE),
                            )
                        }

                        else -> {
                            UiText.Resource(
                                Res.string.clear_data_images_megabytes,
                                listOf("${tenthsOfMegabyte / TENTHS},${tenthsOfMegabyte % TENTHS}"),
                            )
                        }
                    },
                action = UiText.Resource(Res.string.clear_data_images_action),
                isEnabled = (bytes ?: 0L) > 0L,
            ),
        isConfirmingSaved = isAskingAboutSaved,
    )
}

private const val SEPARATOR = " · "

private const val BYTES_PER_KILOBYTE = 1024L
private const val BYTES_PER_MEGABYTE = 1024L * 1024L

/** One decimal place, done in integers because there is no locale-aware formatter in common code. */
private const val TENTHS = 10L
