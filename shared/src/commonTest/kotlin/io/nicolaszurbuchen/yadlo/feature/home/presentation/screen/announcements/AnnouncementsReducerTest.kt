package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.announcements

import io.nicolaszurbuchen.yadlo.core.content.domain.model.Announcement
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class AnnouncementsReducerTest {
    private val reducer = AnnouncementsStoreFactory.ReducerImpl

    @Test
    fun announcementsUpdated_storesThemAndClearsLoading() {
        val items = listOf(announcement("programme"))

        val result =
            with(reducer) {
                AnnouncementsState().reduce(AnnouncementsMessage.AnnouncementsUpdated(items))
            }

        assertEquals(false, result.isLoading)
        assertEquals(items, result.announcements)
    }

    @Test
    fun announcementsUpdated_withNothingPublished_stillStopsLoading() {
        // An empty feed is an answer, not a pending one — the screen has an empty state to show.
        val result =
            with(reducer) {
                AnnouncementsState().reduce(AnnouncementsMessage.AnnouncementsUpdated(emptyList()))
            }

        assertEquals(false, result.isLoading)
        assertEquals(emptyList(), result.announcements)
    }

    private fun announcement(id: String) =
        Announcement(
            id = id,
            publishedAt = Instant.parse("2026-06-02T12:00:00+02:00"),
            title = id,
            body = null,
            editionId = "2026",
            url = null,
            provenance = Provenance.UNVERIFIED,
        )
}
