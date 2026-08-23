package io.nicolaszurbuchen.yadlo.infra.image

/**
 * The bytes [createImageLoader] has put on disk, as something the app can read and empty.
 *
 * A port rather than a call to Coil from the screen, for the ordinary reason: the domain may talk to
 * `infra` and may not talk to a third-party singleton, and a screen that reached for
 * `SingletonImageLoader` would be a presentation file deciding how images are stored.
 *
 * **It is `infra` rather than `common` because it knows nothing about the festival.** No Happening,
 * no Edition, no Stand — it is a directory of bytes the app can always fetch again, which is the
 * definition of plumbing in CLAUDE.md's placement rule.
 *
 * Both members are suspending because both touch the filesystem. Neither is observable: a cache size
 * has no moment worth waking a screen for, and the one thing that changes it from inside the app is
 * [clear], which the caller already knows it has done.
 */
interface ImageCache {
    /**
     * What the disk cache currently holds, in bytes. Zero when nothing has been fetched yet, which
     * is a real answer rather than a missing one.
     */
    suspend fun sizeInBytes(): Long

    /**
     * Empties the disk cache **and the memory cache**, which is one operation from where the visitor
     * is standing: leaving the decoded bitmaps in memory would have the screens they are already on
     * keep drawing pictures the app has just been told to forget, and the reported size drop to zero
     * beside them.
     */
    suspend fun clear()
}
