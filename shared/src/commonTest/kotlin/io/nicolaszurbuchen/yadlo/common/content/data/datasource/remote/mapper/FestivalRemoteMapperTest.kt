package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.mapper

import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.AccessibilityDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.AssistanceDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.CharterDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.ContactDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.FaqEntryDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.FestivalDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.InfoLinkDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.InvolvementDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.PaymentDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.StoryDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.TransportDto
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.SocialLink
import io.nicolaszurbuchen.yadlo.common.error.AppException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FestivalRemoteMapperTest {
    // region the loading chain

    @Test
    fun toDomain_carriesTheFieldsTheLoadingChainReads() {
        val dto =
            minimal().copy(
                minSupportedAppVersion = "1.2.0",
                social =
                    listOf(
                        FestivalDto.SocialDto(id = "instagram", name = "Instagram", url = "https://example.ch/insta"),
                    ),
            )

        val result = dto.toDomain()

        assertEquals(
            Festival(
                name = "Yadlo",
                tagline = "Mouille ton corps, arrose ton esprit",
                currentEditionId = "2026",
                minSupportedAppVersion = "1.2.0",
                social = listOf(SocialLink(id = "instagram", name = "Instagram", url = "https://example.ch/insta")),
            ),
            result,
        )
    }

    @Test
    fun toDomain_noNetworksPublished_isAnEmptyListRatherThanAnAbsentBlock() {
        assertEquals(emptyList(), minimal().toDomain().social)
    }

    @Test
    fun toDomain_aFileWithNoPracticalSections_stillProducesAFestival() {
        // Not a hypothetical: this is what every version of this file looked like before the Plus
        // screens existed, and it is what a rolled-back publish would look like again. Losing the
        // transport screen is the cost; losing the festival would not be.
        val festival = minimal().toDomain()

        assertNull(festival.transport)
        assertNull(festival.payment)
        assertNull(festival.story)
        assertTrue(festival.faq.isEmpty())
        assertTrue(festival.charters.isEmpty())
    }

    // endregion

    // region the Plus sections

    @Test
    fun toDomain_story_carriesItsPassageWithItsOwnProvenance() {
        val dto =
            minimal().copy(
                story =
                    StoryDto(
                        foundedYear = 2015,
                        body = "Yadlo est né en 2015.",
                        passage =
                            StoryDto.PassageDto(
                                title = "Une journée à Yadlo",
                                body = "Tôt le matin, les paddles glissent.",
                                provenance = "unverified",
                            ),
                        provenance = "confirmed",
                    ),
            )

        val story = dto.toDomain().story

        assertEquals(2015, story?.foundedYear)
        assertEquals(Provenance.CONFIRMED, story?.provenance)
        // The origin is the association's own words and the day is a retelling. One block, two
        // levels of confidence, and the screen has to be able to say so.
        assertEquals(Provenance.UNVERIFIED, story?.passage?.provenance)
    }

    @Test
    fun toDomain_storyWithNoPassage_keepsTheOriginRatherThanDroppingBoth() {
        val dto = minimal().copy(story = StoryDto(foundedYear = 2015, body = "…", provenance = "confirmed"))

        assertEquals(2015, dto.toDomain().story?.foundedYear)
        assertNull(dto.toDomain().story?.passage)
    }

    @Test
    fun toDomain_charters_areLiftedOutOfTheirWrapper() {
        val dto =
            minimal().copy(
                responsible =
                    FestivalDto.ResponsibleDto(
                        charters =
                            listOf(
                                CharterDto(
                                    id = "festiplus",
                                    name = "FestiPlus",
                                    body = "Une charte vaudoise.",
                                    url = "https://festiplus.ch/",
                                    provenance = "confirmed",
                                ),
                            ),
                    ),
            )

        assertEquals(listOf("FestiPlus"), dto.toDomain().charters.map { it.name })
    }

    @Test
    fun toDomain_faq_keepsTheQuestionAndItsProvenanceTogether() {
        val dto =
            minimal().copy(
                faq =
                    listOf(
                        FaqEntryDto(
                            id = "entree",
                            question = "L'entrée est-elle payante ?",
                            answer = "Non. L'entrée est libre les trois jours.",
                            provenance = "confirmed",
                        ),
                    ),
            )

        val entry = dto.toDomain().faq.single()

        assertEquals("L'entrée est-elle payante ?", entry.question)
        assertEquals(Provenance.CONFIRMED, entry.provenance)
    }

    @Test
    fun toDomain_contact_flattensTheAddressToItsLines() {
        val contact = minimal().copy(contact = contactDto()).toDomain().contact

        assertEquals(listOf("Avenue de la Plage 1", "1028 Préverenges"), contact?.addressLines)
        assertNull(contact?.phone)
        assertEquals(listOf("hello", "staff"), contact?.emails?.map { it.id })
    }

    @Test
    fun toDomain_nightBusTimes_stayTheStringsTheyWerePublishedAs() {
        val dto = minimal().copy(transport = transportDto())

        val nightBus = dto.toDomain().transport?.modes?.single { it.id == "bus-nuit" }
        val saturday = nightBus?.departures?.single()

        assertEquals("Samedi", saturday?.night)
        assertEquals(listOf("00:59", "03:00"), saturday?.times?.map { it.time })
        // The one that matters: the last bus with no onward connection carries its note rather than
        // being buried in a list of identical rows.
        assertEquals("Pas de correspondance pour Lausanne.", saturday?.times?.last()?.note)
        assertNull(saturday?.times?.first()?.note)
    }

    @Test
    fun toDomain_aModeWithNoTimetable_readsAsEmptyRatherThanAbsent() {
        // Every mode but the night bus publishes `departures: null`. Flattening the two into one
        // empty list is what stops each screen inventing its own answer to the same question.
        val onFoot = minimal().copy(transport = transportDto()).toDomain().transport?.modes?.first()

        assertEquals("pied", onFoot?.id)
        assertTrue(onFoot?.departures?.isEmpty() == true)
    }

    @Test
    fun toDomain_transportLinks_keepBothLinesTheContentWrote() {
        val bus = minimal().copy(transport = transportDto()).toDomain().transport?.modes?.single { it.id == "bus" }

        assertEquals("Horaires ligne 701", bus?.links?.single()?.label)
        assertEquals("PDF · MBC", bus?.links?.single()?.sublabel)
    }

    @Test
    fun toDomain_payment_keepsTheRefusedMethodRatherThanFilteringItOut() {
        val payment = minimal().copy(payment = paymentDto()).toDomain().payment

        // "Espèces, non" is the whole point of this screen. A list of only the accepted methods
        // would answer a question nobody is asking.
        assertEquals(listOf("carte" to true, "especes" to false), payment?.methods?.map { it.id to it.accepted })
        assertEquals(listOf("pas-de-twint"), payment?.notes?.map { it.id })
        // The link belongs to the note that needs it rather than to the block.
        assertEquals("twint.ch", payment?.notes?.single()?.links?.single()?.label)
    }

    @Test
    fun toDomain_accessibility_keepsTheContactEvenWhenNothingIsPublished() {
        val dto = minimal().copy(accessibility = AccessibilityDto(contactEmailId = "hello", provenance = "unverified"))

        val accessibility = assertNotNull(dto.toDomain().accessibility)

        // The list is empty because the festival publishes nothing, and the address is the only
        // useful thing left on the screen. Losing it would leave a page that says nothing at all.
        assertTrue(accessibility.items.isEmpty())
        assertEquals("hello", accessibility.contactEmailId)
    }

    @Test
    fun toDomain_accessibilityItem_carriesWhatIsNotAvailableAsPlainlyAsWhatIs() {
        val dto =
            minimal().copy(
                accessibility =
                    AccessibilityDto(
                        items =
                            listOf(
                                AccessibilityDto.ItemDto(
                                    id = "toilettes",
                                    name = "Toilettes adaptées",
                                    available = false,
                                    note = "Le site est une plage.",
                                ),
                            ),
                        contactEmailId = "hello",
                        provenance = "confirmed",
                    ),
            )

        val item = dto.toDomain().accessibility?.items?.single()

        assertEquals(false, item?.available)
        assertEquals("Le site est une plage.", item?.note)
    }

    @Test
    fun toDomain_emergencyNumbers_stayTextBecauseTheyAreDialledNotCounted() {
        val dto =
            minimal().copy(
                assistance =
                    AssistanceDto(
                        emergencyNumbers =
                            listOf(AssistanceDto.EmergencyNumberDto(id = "ambulance", label = "Ambulance", number = "144")),
                        lostPropertyEmailId = "hello",
                        provenance = "unverified",
                    ),
            )

        assertEquals("144", dto.toDomain().assistance?.emergencyNumbers?.single()?.number)
        assertEquals("hello", dto.toDomain().assistance?.lostPropertyEmailId)
    }

    @Test
    fun toDomain_involvement_keepsTheTwoOffersApart() {
        val dto =
            minimal().copy(
                involvement =
                    InvolvementDto(
                        volunteering =
                            InvolvementDto.VolunteeringDto(
                                name = "Hot'Staff",
                                body = "Six heures minimum.",
                                perks = listOf("Tote bag et t-shirt"),
                                signupUrl = "https://ehro.app/o/yadlo/",
                                contactEmailId = "staff",
                                provenance = "confirmed",
                            ),
                        partnership =
                            InvolvementDto.PartnershipDto(
                                name = "Devenir partenaire",
                                body = null,
                                contactEmailId = "hello",
                                provenance = "unverified",
                            ),
                    ),
            )

        val involvement = dto.toDomain().involvement

        // Only one of the two has perks and a signup site, which is why they are two shapes rather
        // than one shape with holes in it.
        assertEquals(listOf("Tote bag et t-shirt"), involvement?.volunteering?.perks)
        assertEquals("https://ehro.app/o/yadlo/", involvement?.volunteering?.signupUrl)
        assertNull(involvement?.partnership?.body)
        assertEquals("hello", involvement?.partnership?.contactEmailId)
    }

    @Test
    fun toDomain_links_carryTheStandingCallsToAction() {
        val dto =
            minimal().copy(
                links = listOf(InfoLinkDto(id = "newsletter", label = "Newsletter", url = "https://example.ch/n")),
            )

        assertEquals(listOf("newsletter"), dto.toDomain().links.map { it.id })
        assertNull(dto.toDomain().links.single().sublabel)
    }

    // endregion

    // region rejection

    @Test
    fun toDomain_anUnknownProvenanceInASection_rejectsTheFileRatherThanGuessing() {
        val dto = minimal().copy(payment = paymentDto().copy(provenance = "probably"))

        assertFailsWith<AppException> { dto.toDomain() }
    }

    // endregion

    // region the wire format

    @Test
    fun published_json_parsesTheFrenchSectionNamesIntoTheEnglishModel() {
        // The section keys are the association's French and stay that way on the wire. This is the
        // one test that would catch a @SerialName going missing, because every other test here
        // builds the DTO directly and never crosses the JSON boundary at all.
        val json =
            """
            {
              "schemaVersion": 1,
              "name": "Yadlo",
              "tagline": "Mouille ton corps, arrose ton esprit",
              "currentEditionId": "2026",
              "minSupportedAppVersion": null,
              "histoire": {
                "foundedYear": 2015,
                "body": "Yadlo est né en 2015.",
                "journee": { "title": "Une journée à Yadlo", "body": "Tôt le matin.", "provenance": "unverified" },
                "provenance": "confirmed"
              },
              "faq": [{ "id": "entree", "question": "Payant ?", "answer": "Non.", "provenance": "confirmed" }],
              "responsable": {
                "charters": [
                  { "id": "festiplus", "name": "FestiPlus", "body": "Charte.", "url": null, "provenance": "confirmed" }
                ]
              },
              "contact": {
                "address": { "lines": ["Avenue de la Plage 1"], "provenance": "confirmed" },
                "phone": null,
                "emails": [{ "id": "hello", "address": "hello@yadlo.ch", "label": "Informations" }],
                "provenance": "confirmed"
              },
              "social": [{ "id": "instagram", "name": "Instagram", "url": "https://example.ch/" }],
              "links": [{ "id": "newsletter", "label": "Newsletter", "url": "https://example.ch/n" }],
              "transports": {
                "modes": [
                  { "id": "pied", "name": "À pied", "body": "35 minutes.", "links": [], "departures": null }
                ],
                "provenance": "confirmed"
              },
              "paiement": {
                "methods": [{ "id": "twint", "name": "TWINT", "accepted": true }],
                "headline": "Carte et TWINT uniquement",
                "summary": "Pas d'espèces.",
                "notes": [
                  { "id": "pourquoi", "title": "Pourquoi", "body": "Les files avancent plus vite.", "links": [] }
                ],
                "provenance": "confirmed"
              },
              "accessibilite": { "items": [], "contactEmailId": "hello", "provenance": "unverified" },
              "besoin": {
                "emergencyNumbers": [{ "id": "ambulance", "label": "Ambulance", "number": "144" }],
                "lostPropertyEmailId": "hello",
                "provenance": "unverified"
              },
              "simpliquer": {
                "hotstaff": {
                  "name": "Hot'Staff", "body": "Six heures.", "perks": ["Tote bag"],
                  "signupUrl": "https://example.ch/s", "contactEmailId": "staff", "provenance": "confirmed"
                },
                "partenaire": {
                  "name": "Devenir partenaire", "body": null,
                  "contactEmailId": "hello", "provenance": "unverified"
                }
              },
              "unknownSectionPublishedAheadOfTheApp": { "whatever": true }
            }
            """.trimIndent()

        val festival = contentJson.decodeFromString<FestivalDto>(json).toDomain()

        assertEquals("Une journée à Yadlo", festival.story?.passage?.title)
        assertEquals(listOf("entree"), festival.faq.map { it.id })
        assertEquals(listOf("festiplus"), festival.charters.map { it.id })
        assertEquals(listOf("hello@yadlo.ch"), festival.contact?.emails?.map { it.address })
        assertEquals(listOf("À pied"), festival.transport?.modes?.map { it.name })
        assertEquals(listOf("TWINT"), festival.payment?.methods?.map { it.name })
        assertEquals("hello", festival.accessibility?.contactEmailId)
        assertEquals("144", festival.assistance?.emergencyNumbers?.single()?.number)
        assertEquals("Hot'Staff", festival.involvement?.volunteering?.name)
        assertEquals("Devenir partenaire", festival.involvement?.partnership?.name)
        assertEquals(listOf("Newsletter"), festival.links.map { it.label })
    }

    // endregion

    private fun minimal() =
        FestivalDto(
            schemaVersion = 1,
            name = "Yadlo",
            tagline = "Mouille ton corps, arrose ton esprit",
            currentEditionId = "2026",
            minSupportedAppVersion = null,
        )

    private fun contactDto() =
        ContactDto(
            address =
                ContactDto.AddressDto(
                    lines = listOf("Avenue de la Plage 1", "1028 Préverenges"),
                    provenance = "confirmed",
                ),
            phone = null,
            emails =
                listOf(
                    ContactDto.EmailDto(id = "hello", address = "hello@yadlo.ch", label = "Informations générales"),
                    ContactDto.EmailDto(id = "staff", address = "staff@yadlo.ch", label = "Staff"),
                ),
            provenance = "confirmed",
        )

    private fun transportDto() =
        TransportDto(
            modes =
                listOf(
                    TransportDto.ModeDto(id = "pied", name = "À pied", body = "35 minutes depuis Morges."),
                    TransportDto.ModeDto(
                        id = "bus",
                        name = "En bus",
                        body = "Lignes 701 et 705.",
                        links =
                            listOf(
                                InfoLinkDto(
                                    id = "701",
                                    label = "Horaires ligne 701",
                                    sublabel = "PDF · MBC",
                                    url = "https://example.ch/701.pdf",
                                ),
                            ),
                    ),
                    TransportDto.ModeDto(
                        id = "bus-nuit",
                        name = "Bus de nuit",
                        body = "Vers Morges, gare.",
                        departures =
                            listOf(
                                TransportDto.DepartureDto(
                                    id = "samedi",
                                    night = "Samedi",
                                    times =
                                        listOf(
                                            TransportDto.TimeDto(time = "00:59"),
                                            TransportDto.TimeDto(
                                                time = "03:00",
                                                note = "Pas de correspondance pour Lausanne.",
                                            ),
                                        ),
                                ),
                            ),
                    ),
                ),
            provenance = "confirmed",
        )

    private fun paymentDto() =
        PaymentDto(
            methods =
                listOf(
                    PaymentDto.MethodDto(id = "carte", name = "Cartes", accepted = true),
                    PaymentDto.MethodDto(id = "especes", name = "Espèces", accepted = false),
                ),
            notes =
                listOf(
                    PaymentDto.NoteDto(
                        id = "pas-de-twint",
                        title = "Vous n'avez pas TWINT ?",
                        body = "L'application dépend de votre banque.",
                        links =
                            listOf(
                                InfoLinkDto(
                                    id = "twint",
                                    label = "twint.ch",
                                    sublabel = "Site officiel",
                                    url = "https://www.twint.ch/",
                                ),
                            ),
                    ),
                ),
            provenance = "confirmed",
        )
}

/** Configured like the app's own client, so the wire-format test exercises the real leniency. */
private val contentJson = Json { ignoreUnknownKeys = true }
