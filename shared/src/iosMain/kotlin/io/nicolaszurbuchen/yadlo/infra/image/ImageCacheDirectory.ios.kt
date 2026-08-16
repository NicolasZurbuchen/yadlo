package io.nicolaszurbuchen.yadlo.infra.image

import coil3.PlatformContext
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

actual fun platformCacheDirectory(context: PlatformContext): Path {
    // One caches directory exists inside the app's own container, so the first entry is the only
    // entry. NSFileManager's URL-based call says the same thing and costs an error out-parameter.
    val caches = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true).first() as String

    return caches.toPath()
}
