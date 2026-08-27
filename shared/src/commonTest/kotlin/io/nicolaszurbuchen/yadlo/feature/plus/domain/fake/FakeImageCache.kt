package io.nicolaszurbuchen.yadlo.feature.plus.domain.fake

import io.nicolaszurbuchen.yadlo.infra.image.ImageCache

/**
 * The picture cache as a number that can be set and emptied.
 *
 * [clear] really empties, the way [io.nicolaszurbuchen.yadlo.core.plan.domain.fake.FakePlanRepository]
 * really toggles: the screen reads the size back after clearing it, and a fake that recorded the
 * call and left the number where it was would let that read go untested.
 *
 * [failsToClear] is the one case the real port can produce that nothing else can be reasoned about —
 * a directory Coil could not fully delete. The screen has to report whatever is left rather than the
 * zero it hoped for, which is why the store re-reads instead of assuming.
 */
class FakeImageCache : ImageCache {
    var bytes: Long = 0L

    var failsToClear: Boolean = false

    var cleared: Int = 0
        private set

    override suspend fun sizeInBytes(): Long = bytes

    override suspend fun clear() {
        cleared++
        if (!failsToClear) bytes = 0L
    }
}
