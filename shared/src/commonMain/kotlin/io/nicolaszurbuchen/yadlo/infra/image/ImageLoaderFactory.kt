package io.nicolaszurbuchen.yadlo.infra.image

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.CachePolicy
import io.ktor.client.HttpClient

/**
 * One image loader for the whole app, built to survive a field with no signal.
 *
 * **The disk cache is configured rather than left to a default, because there is no default.** Coil
 * builds an `ImageLoader` with `diskCache = null` unless told otherwise, so every artist photo was
 * being re-fetched on each cold start. That is the wrong behaviour anywhere and the wrong behaviour
 * *badly* at Préverenges, where a few thousand people share one cell and the programme is the thing
 * they are trying to look at.
 *
 * The memory cache stays on Coil's default. It is sized against the device's own memory and only
 * ever affects a session; the disk is the part that decides whether the app works on the beach.
 *
 * Sharing [httpClient] rather than letting Coil build its own is what puts image requests through
 * the same engine, timeouts and logging as the content fetch.
 */
fun createImageLoader(
    context: PlatformContext,
    httpClient: HttpClient,
): ImageLoader =
    ImageLoader.Builder(context)
        .components { add(KtorNetworkFetcherFactory(httpClient = { httpClient })) }
        .diskCache {
            DiskCache.Builder()
                .directory(imageCacheDirectory(platformCacheDirectory(context)))
                .maxSizeBytes(MAX_DISK_CACHE_BYTES)
                .build()
        }
        // Explicit rather than inherited: reading and writing the disk is the whole point of the
        // block above, and a policy that silently changed under us would be invisible until a
        // visitor with no signal saw grey rectangles.
        .diskCachePolicy(CachePolicy.ENABLED)
        .build()

/**
 * 128 MB — comfortably more than the picture bank can be, on purpose.
 *
 * The corpus is bounded by content rather than by browsing: one edition publishes a few dozen
 * happening photos and about as many partner logos, so a realistic full cache is single-digit MB.
 * The headroom is for the archive editions a visitor may open, and for the day the association
 * starts publishing photographs at the resolution their camera actually takes them at. A byte cap
 * rather than a percentage of free space, because a bounded corpus has a knowable size and a
 * percentage would hand a 512 GB phone a quota nothing will ever fill.
 */
private const val MAX_DISK_CACHE_BYTES = 128L * 1024 * 1024
