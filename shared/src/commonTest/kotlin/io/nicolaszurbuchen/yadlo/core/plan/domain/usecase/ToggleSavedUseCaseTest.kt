package io.nicolaszurbuchen.yadlo.core.plan.domain.usecase

import io.nicolaszurbuchen.yadlo.core.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Venue
import io.nicolaszurbuchen.yadlo.core.plan.domain.fake.FakePlanRepository
import io.nicolaszurbuchen.yadlo.core.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.core.plan.domain.model.SavedKind
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToggleSavedUseCaseTest {
    private val contentRepository = FakeContentRepository()
    private val planRepository = FakePlanRepository()
    private val useCase = ToggleSavedUseCase(planRepository, contentRepository)

    @Test
    fun invoke_stampsTheRowWithTheEditionOnScreenRatherThanAskingTheCaller() =
        runTest {
            contentRepository.emitStatus(ready("2027"))

            useCase(id = "2027:dj-alf-fri", kind = SavedKind.SLOT)

            assertEquals(
                listOf(SavedItem(id = "2027:dj-alf-fri", kind = SavedKind.SLOT, editionId = "2027")),
                planRepository.toggled,
            )
        }

    @Test
    fun invoke_twiceOnTheSameThing_leavesItUnsaved() =
        runTest {
            contentRepository.emitStatus(ready())

            useCase(id = "vegan-fabrik", kind = SavedKind.STAND)
            useCase(id = "vegan-fabrik", kind = SavedKind.STAND)

            assertTrue(planRepository.observeSaved().first().isEmpty())
        }

    @Test
    fun invoke_carriesTheKindTheTapDecided() =
        runTest {
            contentRepository.emitStatus(ready())

            useCase(id = "vegan-fabrik", kind = SavedKind.STAND)

            assertEquals(listOf(SavedKind.STAND), planRepository.toggled.map { it.kind })
        }

    @Test
    fun invoke_beforeTheContentIsReady_savesNothingRatherThanARowThatCannotNameItsEdition() =
        runTest {
            useCase(id = "2026:dj-alf-fri", kind = SavedKind.SLOT)

            assertTrue(planRepository.toggled.isEmpty())
        }

    private fun ready(editionId: String = "2026") =
        ContentStatus.Ready(
            bundle =
                ContentBundle(
                    festival =
                        Festival(
                            name = "Yadlo",
                            tagline = "Trois jours au bord du lac",
                            website = "https://www.yadlo.ch/",
                            currentEditionId = editionId,
                            minSupportedAppVersion = null,
                            social = emptyList(),
                        ),
                    edition =
                        Edition(
                            id = editionId,
                            year = editionId.toInt(),
                            name = "Yadlo $editionId",
                            venue =
                                Venue(
                                    name = "Plage de Préverenges",
                                    address = "Préverenges",
                                    latitude = 46.5,
                                    longitude = 6.5,
                                    provenance = Provenance.CONFIRMED,
                                ),
                            days = emptyList(),
                            categories = emptyList(),
                            happenings = emptyList(),
                            slots = emptyList(),
                            partners = emptyList(),
                            figures = emptyList(),
                        ),
                    announcements = emptyList(),
                ),
            updateRequired = false,
        )
}
