package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Accessibility
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Contact
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ObserveAccessibilityGuideUseCaseTest {
    @Test
    fun invoke_noSectionPublished_isNullRatherThanAnEmptyGuide() =
        runTest {
            // Different from a published section with nothing in it: one is a content bug, the
            // other is the honest state the screen was designed around.
            assertNull(guideFrom(FakeContentRepository().apply { emitStatus(ready()) }))
        }

    @Test
    fun invoke_theSectionIsPublishedAndEmpty_stillCarriesTheAddress() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withGuide(emptyList()))) }

            val guide = assertNotNull(guideFrom(repository))

            // This is the 2026 state. When the data is missing, somebody to ask is the most useful
            // thing the screen can offer — so losing the address would leave nothing at all.
            assertTrue(guide.available.isEmpty())
            assertTrue(guide.unavailable.isEmpty())
            assertEquals("hello@yadlo.ch", guide.contactEmail)
        }

    @Test
    fun invoke_splitsWhatWorksFromWhatDoesNot() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            festival =
                                withGuide(
                                    listOf(
                                        item("parking", available = true),
                                        item("toilettes", available = false),
                                        item("entree", available = true),
                                    ),
                                ),
                        ),
                    )
                }

            val guide = guideFrom(repository)

            assertEquals(listOf("parking", "entree"), guide?.available?.map { it.id })
            assertEquals(listOf("toilettes"), guide?.unavailable?.map { it.id })
        }

    @Test
    fun invoke_whatIsNotAvailable_isKeptRatherThanFilteredAway() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(festival = withGuide(listOf(item("toilettes", available = false)))))
                }

            // "No accessible toilets" is what decides whether someone travels thirty kilometres.
            // A guide that listed only what works would be the reassuring page story 40 forbids.
            assertEquals(1, guideFrom(repository)?.unavailable?.size)
        }

    @Test
    fun invoke_theNoteOnAnItem_survivesTheSplit() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            festival =
                                withGuide(
                                    listOf(item("toilettes", available = false, note = "Le site est une plage.")),
                                ),
                        ),
                    )
                }

            assertEquals("Le site est une plage.", guideFrom(repository)?.unavailable?.single()?.note)
        }

    @Test
    fun invoke_anEmailIdThatResolvesToNothing_dropsTheRowRatherThanLinkingNowhere() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            festival =
                                festival {
                                    copy(
                                        accessibility =
                                            Accessibility(
                                                items = emptyList(),
                                                contactEmailId = "quelquun",
                                                provenance = Provenance.UNVERIFIED,
                                            ),
                                        contact = contact(),
                                    )
                                },
                        ),
                    )
                }

            // A content bug the screen renders as one fewer row rather than as a mailto: to nothing.
            assertNull(guideFrom(repository)?.contactEmail)
        }

    @Test
    fun invoke_noContactBlockAtAll_doesNotTakeTheGuideDownWithIt() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            festival =
                                festival {
                                    copy(
                                        accessibility =
                                            Accessibility(
                                                items = listOf(item("parking", available = true)),
                                                contactEmailId = "hello",
                                                provenance = Provenance.CONFIRMED,
                                            ),
                                    )
                                },
                        ),
                    )
                }

            val guide = guideFrom(repository)

            assertEquals(1, guide?.available?.size)
            assertNull(guide?.contactEmail)
        }

    private suspend fun guideFrom(repository: FakeContentRepository) = ObserveAccessibilityGuideUseCase(repository)().first()

    private fun withGuide(items: List<Accessibility.Item>) =
        festival {
            copy(
                accessibility =
                    Accessibility(items = items, contactEmailId = "hello", provenance = Provenance.UNVERIFIED),
                contact = contact(),
            )
        }

    private fun contact() =
        Contact(
            addressLines = listOf("Avenue de la Plage 1"),
            phone = null,
            emails =
                listOf(
                    Contact.Email(id = "hello", address = "hello@yadlo.ch", label = "Informations générales"),
                    Contact.Email(id = "staff", address = "staff@yadlo.ch", label = "Staff"),
                ),
            provenance = Provenance.CONFIRMED,
        )

    private fun item(
        id: String,
        available: Boolean,
        note: String? = null,
    ) = Accessibility.Item(id = id, name = id, available = available, note = note)
}
