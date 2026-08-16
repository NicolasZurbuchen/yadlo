package io.nicolaszurbuchen.yadlo.infra.image

import coil3.PlatformContext
import okio.Path

/**
 * Where the operating system wants throwaway files kept.
 *
 * A platform seam because there is no cross-platform notion of it: Android hangs one off the
 * `Context`, iOS puts one in the app container. Both are directories the system may empty when the
 * device is short of space, which is the correct home for bytes the app can always fetch again.
 *
 * **This is the seam and nothing else.** Which subdirectory the images go in is [imageCacheDirectory]
 * below, in common code, so the two platforms cannot drift on it.
 */
expect fun platformCacheDirectory(context: PlatformContext): Path

/**
 * A directory of our own inside the platform's cache root, never the root itself: Coil clears the
 * directory it is given, and the root is shared with whatever else the system put there.
 */
fun imageCacheDirectory(cacheRoot: Path): Path = cacheRoot / IMAGE_CACHE_DIRECTORY_NAME

internal const val IMAGE_CACHE_DIRECTORY_NAME = "image_cache"
