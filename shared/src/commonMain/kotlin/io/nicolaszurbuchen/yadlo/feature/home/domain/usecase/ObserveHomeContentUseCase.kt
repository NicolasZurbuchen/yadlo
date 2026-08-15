package io.nicolaszurbuchen.yadlo.feature.home.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.home.domain.model.HomeContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * Accueil's slice of the content bundle.
 *
 * Only [ContentStatus.Ready] is mapped, and the other two states are dropped rather than modelled:
 * the tab shell is not composed until the bundle is ready, so a screen inside it never has to
 * render a loading or an unavailable bundle. Dropping them keeps that guarantee visible instead of
 * spreading a second empty-state design across every tab.
 */
class ObserveHomeContentUseCase(
    private val repository: ContentRepository,
) {
    operator fun invoke(): Flow<HomeContent> =
        repository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { it.bundle.toHomeContent() }

    private fun ContentBundle.toHomeContent(): HomeContent =
        HomeContent(
            editionName = edition.name,
            editionYear = edition.year,
            venueName = edition.venue.name,
            days = edition.days,
            // What ANNOUNCED actually means: a programme exists. An edition file with no Slots is
            // a placeholder, and story 64 is explicit that the app must never claim otherwise.
            hasPublishedProgramme = edition.slots.isNotEmpty(),
            // Happenings, not Slots: the hero counts things on the bill, and an activity running
            // all three days is one activity rather than three.
            artistCount = edition.happenings.count { it is Happening.Artist },
            activityCount = edition.happenings.count { it is Happening.Activity },
            // An annonce with no edition is about the festival rather than one year, so it outlives
            // the edition it was written under and stays. One belonging to a past edition does not.
            announcements =
                announcements.filter {
                    it.editionId == null || it.editionId == festival.currentEditionId
                },
            figures = edition.figures,
            figuresAreConfirmed = edition.figures.all { it.provenance == Provenance.CONFIRMED },
            social = festival.social,
        )
}
