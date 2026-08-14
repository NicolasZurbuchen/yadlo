package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.announcements

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Announcement
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class AnnouncementsUiMapperTest {
    @Test
    fun toUiModel_showsEverythingRatherThanTheTwoAccueilSummarises() {
        val state =
            AnnouncementsState(
                isLoading = false,
                announcements = listOf(announcement("un"), announcement("deux"), announcement("trois")),
            )

        assertEquals(listOf("un", "deux", "trois"), state.toUiModel().items.map { it.id })
    }

    @Test
    fun toUiModel_formatsDatesTheSameWayAccueilDoes() {
        val state = AnnouncementsState(isLoading = false, announcements = listOf(announcement("un")))

        assertEquals("02.06.2026", state.toUiModel().items.single().dateText)
    }

    @Test
    fun toUiModel_annonceWithNoBody_carriesAnEmptyStringRatherThanNull() {
        val state = AnnouncementsState(isLoading = false, announcements = listOf(announcement("un")))

        assertEquals("", state.toUiModel().items.single().body)
    }

    @Test
    fun toUiModel_annonceWithNoUrl_staysUntappable() {
        val state = AnnouncementsState(isLoading = false, announcements = listOf(announcement("un")))

        assertNull(state.toUiModel().items.single().url)
    }

    @Test
    fun toUiModel_stillLoading_carriesTheFlagThrough() {
        assertEquals(true, AnnouncementsState().toUiModel().isLoading)
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
