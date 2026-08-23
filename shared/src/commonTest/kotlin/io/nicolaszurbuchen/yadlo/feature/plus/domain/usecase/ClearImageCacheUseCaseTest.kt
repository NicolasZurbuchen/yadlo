package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.feature.plus.domain.fake.FakeImageCache
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ClearImageCacheUseCaseTest {
    private val cache = FakeImageCache()
    private val useCase = ClearImageCacheUseCase(cache)

    @Test
    fun invoke_emptiesTheCache() =
        runTest {
            cache.bytes = 4_823_000L

            useCase()

            assertEquals(0L, cache.sizeInBytes())
        }

    @Test
    fun invoke_onAnAlreadyEmptyCache_isAQuietNoOp() =
        runTest {
            useCase()

            assertEquals(1, cache.cleared)
            assertEquals(0L, cache.sizeInBytes())
        }
}
