package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Phase
import io.nicolaszurbuchen.yadlo.feature.home.domain.model.HomeContent
import io.nicolaszurbuchen.yadlo.feature.home.domain.model.SiteMoment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class HomeReducerTest {
    private val reducer = HomeStoreFactory.ReducerImpl

    @Test
    fun contentUpdated_storesContentAndThePhaseDerivedFromIt() {
        val state = HomeState(now = NOW, phase = Phase.OFF_SEASON)
        val content = homeContent()

        val result =
            with(reducer) {
                state.reduce(
                    HomeMessage.ContentUpdated(
                        content = content,
                        phase = Phase.ENDED,
                        siteMoment = SiteMoment.Finished,
                    ),
                )
            }

        assertEquals(content, result.content)
        assertEquals(Phase.ENDED, result.phase)
        assertEquals(SiteMoment.Finished, result.siteMoment)
    }

    @Test
    fun ticked_advancesTheClockAndThePhaseWithoutTouchingContent() {
        val content = homeContent()
        val state = HomeState(now = NOW, phase = Phase.APPROACHING, content = content)
        val later = Instant.parse("2026-07-10T00:00:00+02:00")

        val result =
            with(reducer) {
                state.reduce(
                    HomeMessage.Ticked(
                        now = later,
                        phase = Phase.LIVE,
                        siteMoment = SiteMoment.Open(later),
                    ),
                )
            }

        assertEquals(later, result.now)
        assertEquals(Phase.LIVE, result.phase)
        assertEquals(SiteMoment.Open(later), result.siteMoment)
        assertEquals(content, result.content)
    }

    private fun homeContent() =
        HomeContent(
            editionName = "Yadlo 2026",
            editionYear = 2026,
            venueName = "Plage de Préverenges",
            days = emptyList(),
            hasPublishedProgramme = true,
            artistCount = 13,
            activityCount = 17,
            announcements = emptyList(),
            figures = emptyList(),
            figuresAreConfirmed = true,
            social = emptyList(),
            // Nothing promoted: the reducer only ever swaps one content for another, and what
            // Accueil chooses to raise out of it is the UiMapper's business.
            hasStory = false,
            hasContact = false,
            hasVolunteering = false,
            hasTransport = false,
            hasPayment = false,
            newsletterUrl = null,
        )

    private companion object {
        val NOW = Instant.parse("2026-07-01T12:00:00+02:00")
    }
}
