package io.nicolaszurbuchen.yadlo.infra.image

import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ImageCacheDirectoryTest {
    @Test
    fun imageCacheDirectory_isNeverTheCacheRootItself() {
        val root = "/data/user/0/io.nicolaszurbuchen.yadlo/cache".toPath()

        // Coil clears the directory it is handed. Given the root, "clear the image cache" would
        // take the HTTP cache and anything else the system keeps beside it.
        assertNotEquals(root, imageCacheDirectory(root))
    }

    @Test
    fun imageCacheDirectory_isOneNamedChildOfWhateverThePlatformHandsUs() {
        // Composed in common code precisely so the two platform seams cannot drift on the name: an
        // Android build and an iOS build writing to differently-named directories would be
        // invisible until someone compared them.
        assertEquals(
            "/data/user/0/io.nicolaszurbuchen.yadlo/cache/$IMAGE_CACHE_DIRECTORY_NAME".toPath(),
            imageCacheDirectory("/data/user/0/io.nicolaszurbuchen.yadlo/cache".toPath()),
        )
    }
}
