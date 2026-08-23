package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.infra.image.ImageCache

/**
 * Empties the downloaded photographs — the recoverable half of *Effacer mes données*.
 *
 * **The only thing on this screen that undoes itself.** Every picture here came off the network and
 * comes back off the network, so this costs data rather than anything the visitor made, which is
 * why it is the one of the two actions that is not asked about first. It is also the one worth
 * offering on the Sunday of a festival with no signal to spare, and the trade is stated on the
 * screen rather than assumed to be understood.
 */
class ClearImageCacheUseCase(
    private val imageCache: ImageCache,
) {
    suspend operator fun invoke() {
        imageCache.clear()
    }
}
