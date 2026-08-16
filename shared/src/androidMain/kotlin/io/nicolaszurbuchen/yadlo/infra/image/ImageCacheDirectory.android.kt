package io.nicolaszurbuchen.yadlo.infra.image

import coil3.PlatformContext
import okio.Path
import okio.Path.Companion.toOkioPath

actual fun platformCacheDirectory(context: PlatformContext): Path = context.cacheDir.toOkioPath()
