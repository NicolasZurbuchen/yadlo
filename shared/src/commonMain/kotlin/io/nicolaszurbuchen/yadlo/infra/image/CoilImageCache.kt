package io.nicolaszurbuchen.yadlo.infra.image

import coil3.PlatformContext
import coil3.SingletonImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * [ImageCache] against the one loader the app installs — the same instance every `AsyncImage` on
 * every screen draws through, reached the way Coil intends rather than by holding a second
 * reference to it.
 *
 * `SingletonImageLoader.get` returns whatever `App.kt` set, so the directory emptied here is by
 * construction the directory [createImageLoader] configured. Asking for the loader rather than
 * being handed one is what keeps that true if the builder ever changes.
 *
 * **Neither cache is null in this app and both are treated as if they could be.** The disk cache is
 * configured explicitly and the memory cache is Coil's default, so today both are there; a build
 * that turned one off should report zero and clear nothing, not crash on a settings screen.
 *
 * [PlatformContext] is a constructor parameter rather than something this reaches for, because it
 * is the one thing here that differs by platform — an Android `Context` against an iOS singleton —
 * and `infra/di/PlatformModule.kt` is where that difference already lives.
 */
class CoilImageCache(
    private val context: PlatformContext,
) : ImageCache {
    // Reading the size walks the cache's journal on first access, and clearing deletes a directory
    // of files. Neither belongs on the caller's thread, which on this port is always the main one.
    override suspend fun sizeInBytes(): Long =
        withContext(Dispatchers.Default) {
            SingletonImageLoader.get(context).diskCache?.size ?: 0L
        }

    override suspend fun clear() {
        withContext(Dispatchers.Default) {
            val loader = SingletonImageLoader.get(context)

            loader.diskCache?.clear()
            loader.memoryCache?.clear()
        }
    }
}
