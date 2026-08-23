package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.infra.image.ImageCache

/**
 * How much of the picture bank is on this phone.
 *
 * Read rather than observed, because a cache size has no moment worth waking a screen for: it
 * changes as photographs are fetched on screens the visitor is looking at instead of this one, and
 * a number ticking upwards while somebody decides whether to delete it would be movement without
 * information. The screen reads it when it opens and again after it has emptied it, which are the
 * two moments the answer is about something that happened.
 */
class ReadImageCacheSizeUseCase(
    private val imageCache: ImageCache,
) {
    suspend operator fun invoke(): Long = imageCache.sizeInBytes()
}
