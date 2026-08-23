package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.feature.plus.domain.fake.FakeImageCache
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadImageCacheSizeUseCaseTest {
    private val cache = FakeImageCache()
    private val useCase = ReadImageCacheSizeUseCase(cache)

    @Test
    fun invoke_reportsWhatTheCacheHolds() =
        runTest {
            cache.bytes = 4_823_000L

            assertEquals(4_823_000L, useCase())
        }

    @Test
    fun invoke_anEmptyCache_isZeroRatherThanAbsent() =
        runTest {
            assertEquals(0L, useCase())
        }

    @Test
    fun invoke_readsAgainEachTimeRatherThanRememberingTheFirstAnswer() =
        runTest {
            // The screen calls this a second time after emptying the cache, and that call is the
            // only thing that corrects the number on it.
            cache.bytes = 4_823_000L
            useCase()

            cache.bytes = 0L

            assertEquals(0L, useCase())
        }
}
