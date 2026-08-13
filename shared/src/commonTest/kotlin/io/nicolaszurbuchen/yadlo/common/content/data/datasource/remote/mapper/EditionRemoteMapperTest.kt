package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.mapper

import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.CategoryDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.EditionDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.FestivalDayDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.FigureDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.HappeningDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.ImageDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.LinkDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.MenuGroupDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.MoneyDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.PartnerDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.PartnerTierDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.PriceDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.SlotDto
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.VenueDto
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Money
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.error.AppError
import io.nicolaszurbuchen.yadlo.common.error.AppException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class EditionRemoteMapperTest {
    // region reference resolution

    @Test
    fun toDomain_resolvesSlotToItsHappeningAndDay() {
        val dto = editionDto()

        val result = dto.toDomain()

        val slot = result.slots.single()
        assertEquals("dj-alf", slot.happening.id)
        assertEquals("2026:fri", slot.day.id)
    }

    @Test
    fun toDomain_resolvesHappeningToItsCategory() {
        val dto = editionDto()

        val result = dto.toDomain()

        assertEquals("Musique", result.happenings.single().category.name)
    }

    @Test
    fun toDomain_slotDayIsTheAuthoredOneNotTheOneItsInstantFallsIn() {
        // A 01:30 Saturday-morning set authored on Friday stays on Friday. Deriving the day from
        // the instant is the single mistake this whole field exists to prevent.
        val dto =
            editionDto(
                days = listOf(dayDto(id = "2026:fri"), dayDto(id = "2026:sat", date = "2026-07-11")),
                slots =
                    listOf(
                        slotDto(
                            dayId = "2026:fri",
                            start = "2026-07-11T01:30:00+02:00",
                            end = "2026-07-11T03:00:00+02:00",
                        ),
                    ),
            )

        val result = dto.toDomain()

        assertEquals("2026:fri", result.slots.single().day.id)
    }

    @Test
    fun toDomain_slotNamingAnUndeclaredHappening_rejectsTheBundle() {
        val dto = editionDto(slots = listOf(slotDto(id = "2026:ghost", happeningId = "nobody")))

        val exception = assertFailsWith<AppException> { dto.toDomain() }

        assertEquals(
            AppError.Content.UnresolvedReference(field = "slot[2026:ghost].happeningId", id = "nobody"),
            exception.error,
        )
    }

    @Test
    fun toDomain_slotNamingAnUndeclaredDay_rejectsTheBundle() {
        val dto = editionDto(slots = listOf(slotDto(id = "2026:ghost", dayId = "2026:thu")))

        val exception = assertFailsWith<AppException> { dto.toDomain() }

        assertEquals(
            AppError.Content.UnresolvedReference(field = "slot[2026:ghost].dayId", id = "2026:thu"),
            exception.error,
        )
    }

    @Test
    fun toDomain_happeningNamingAnUndeclaredCategory_rejectsTheBundle() {
        val dto = editionDto(happenings = listOf(artistDto(category = "jazz")))

        val exception = assertFailsWith<AppException> { dto.toDomain() }

        assertEquals(
            AppError.Content.UnresolvedReference(field = "happening[dj-alf].category", id = "jazz"),
            exception.error,
        )
    }

    // endregion

    // region malformed fields

    @Test
    fun toDomain_unknownKind_rejectsTheBundle() {
        val dto = editionDto(happenings = listOf(artistDto().copy(kind = "workshop")))

        val exception = assertFailsWith<AppException> { dto.toDomain() }

        assertEquals(
            AppError.Content.MalformedField(field = "happening[dj-alf].kind", detail = "workshop"),
            exception.error,
        )
    }

    @Test
    fun toDomain_kindWithoutItsPayload_rejectsTheBundle() {
        val dto = editionDto(happenings = listOf(artistDto().copy(artist = null)))

        val exception = assertFailsWith<AppException> { dto.toDomain() }

        assertEquals(
            AppError.Content.MalformedField(field = "happening[dj-alf].artist", detail = "absent"),
            exception.error,
        )
    }

    @Test
    fun toDomain_unknownProvenance_rejectsTheBundle() {
        val dto = editionDto(happenings = listOf(artistDto().copy(provenance = "probably")))

        val exception = assertFailsWith<AppException> { dto.toDomain() }

        assertEquals(
            AppError.Content.MalformedField(field = "happening[dj-alf].provenance", detail = "probably"),
            exception.error,
        )
    }

    @Test
    fun toDomain_instantWithoutAnOffset_rejectsTheBundle() {
        // A bare local time is the mistake the schema forbids, and the one that would otherwise be
        // read in the device's zone rather than Europe/Zurich.
        val dto = editionDto(days = listOf(dayDto(start = "2026-07-10T16:00:00")))

        val exception = assertFailsWith<AppException> { dto.toDomain() }

        assertEquals(
            AppError.Content.MalformedField(field = "day[2026:fri].start", detail = "2026-07-10T16:00:00"),
            exception.error,
        )
    }

    @Test
    fun toDomain_instantKeepsItsAuthoredOffset() {
        val dto = editionDto()

        val result = dto.toDomain()

        assertEquals(Instant.parse("2026-07-10T14:00:00Z"), result.days.single().start)
    }

    // endregion

    // region payloads

    @Test
    fun toDomain_activityPrice_keepsTheDepositOutOfTheTiers() {
        val dto = editionDto(happenings = listOf(activityDto()), slots = emptyList())

        val activity = dto.toDomain().happenings.single() as Happening.Activity

        val price = requireNotNull(activity.price)
        assertEquals(listOf(Money(amount = 25.0, currency = "CHF")), price.tiers.map { it.amount })
        assertEquals(Money(amount = 50.0, currency = "CHF"), requireNotNull(price.deposit).amount)
    }

    @Test
    fun toDomain_freeActivityPrice_hasNoTiers() {
        val dto =
            editionDto(
                happenings =
                    listOf(
                        activityDto(price = PriceDto(free = true, tiers = emptyList(), deposit = null, provenance = "confirmed")),
                    ),
                slots = emptyList(),
            )

        val activity = dto.toDomain().happenings.single() as Happening.Activity

        val price = requireNotNull(activity.price)
        assertTrue(price.free)
        assertTrue(price.tiers.isEmpty())
        assertNull(price.deposit)
    }

    @Test
    fun toDomain_standMenu_keepsItemMarksApartFromStandMarks() {
        val dto = editionDto(happenings = listOf(standDto()), slots = emptyList())

        val stand = dto.toDomain().happenings.single() as Happening.Stand

        assertEquals(listOf("végan", "bio"), stand.marks)
        assertEquals(listOf("végé"), stand.menu.single().items.single().marks)
    }

    @Test
    fun toDomain_menuGroupKeepsItsDescription() {
        val dto = editionDto(happenings = listOf(standDto()), slots = emptyList())

        val stand = dto.toDomain().happenings.single() as Happening.Stand

        assertEquals("Cuisine mijotée", stand.menu.single().description)
    }

    @Test
    fun toDomain_image_renamesSrcToUrl() {
        val dto = editionDto(happenings = listOf(artistDto().copy(images = listOf(ImageDto(src = "a.jpg", credit = "Photo: X")))))

        val image = dto.toDomain().happenings.single().images.single()

        assertEquals("a.jpg", image.url)
        assertEquals("Photo: X", image.credit)
    }

    @Test
    fun toDomain_partnerWithoutAUrl_keepsItNull() {
        val dto =
            editionDto(
                partners =
                    listOf(
                        PartnerTierDto(
                            id = "soutien",
                            name = "Soutien",
                            order = 1,
                            provenance = "confirmed",
                            members = listOf(PartnerDto(id = "winatypic", name = "Winatypic", url = null, logo = null)),
                        ),
                    ),
            )

        val partner = dto.toDomain().partners.single().members.single()

        assertNull(partner.url)
        assertNull(partner.logo)
    }

    @Test
    fun toDomain_figureValueStaysAString() {
        val dto = editionDto(figures = listOf(FigureDto(id = "visiteurs", value = "6000", label = "visiteurs", provenance = "confirmed")))

        assertEquals("6000", dto.toDomain().figures.single().value)
    }

    // endregion

    // region wire format

    @Test
    fun published_json_parsesAndIgnoresFieldsThisBuildDoesNotKnow() {
        // What makes an additive schema change safe for an app already in the wild: a field added
        // to the content must not stop an older build from reading the rest of the bundle.
        val json =
            """
            {
              "schemaVersion": 1,
              "id": "2026",
              "year": 2026,
              "name": "Yadlo 2026",
              "imageBaseUrl": null,
              "venue": {
                "name": "Plage de Préverenges",
                "address": "Avenue de la Plage 1, 1028 Préverenges, Suisse",
                "latitude": 46.5122,
                "longitude": 6.5347,
                "provenance": "unverified"
              },
              "days": [
                {
                  "id": "2026:fri",
                  "name": "Vendredi",
                  "date": "2026-07-10",
                  "start": "2026-07-10T16:00:00+02:00",
                  "end": "2026-07-11T02:00:00+02:00",
                  "provenance": "confirmed"
                }
              ],
              "categories": [{ "id": "musique", "name": "Musique", "order": 1 }],
              "happenings": [
                {
                  "id": "dj-alf",
                  "kind": "artist",
                  "name": "DJ ALF",
                  "category": "musique",
                  "description": null,
                  "images": [],
                  "provenance": "confirmed",
                  "artist": {
                    "genres": ["House"],
                    "links": [{ "type": "website", "url": "https://djalf.ch/" }]
                  }
                }
              ],
              "slots": [
                {
                  "id": "2026:dj-alf-fri",
                  "happeningId": "dj-alf",
                  "dayId": "2026:fri",
                  "start": "2026-07-10T17:00:00+02:00",
                  "end": "2026-07-10T18:30:00+02:00",
                  "provenance": "confirmed"
                }
              ],
              "partners": [],
              "figures": []
            }
            """.trimIndent()

        val edition = contentJson.decodeFromString<EditionDto>(json).toDomain()

        assertEquals("Yadlo 2026", edition.name)
        assertEquals("DJ ALF", edition.slots.single().happening.name)
        assertEquals(Provenance.UNVERIFIED, edition.venue.provenance)
    }

    // endregion
}

// region fixtures

/** Configured like the app's own client, so the wire-format test exercises the real leniency. */
private val contentJson = Json { ignoreUnknownKeys = true }

private fun editionDto(
    venue: VenueDto = venueDto(),
    days: List<FestivalDayDto> = listOf(dayDto()),
    categories: List<CategoryDto> = listOf(CategoryDto(id = "musique", name = "Musique", order = 1)),
    happenings: List<HappeningDto> = listOf(artistDto()),
    slots: List<SlotDto> = listOf(slotDto()),
    partners: List<PartnerTierDto> = emptyList(),
    figures: List<FigureDto> = emptyList(),
): EditionDto =
    EditionDto(
        schemaVersion = 1,
        id = "2026",
        year = 2026,
        name = "Yadlo 2026",
        venue = venue,
        days = days,
        categories = categories,
        happenings = happenings,
        slots = slots,
        partners = partners,
        figures = figures,
    )

private fun venueDto(): VenueDto =
    VenueDto(
        name = "Plage de Préverenges",
        address = "Avenue de la Plage 1",
        latitude = 46.5122,
        longitude = 6.5347,
        provenance = "unverified",
    )

private fun dayDto(
    id: String = "2026:fri",
    date: String = "2026-07-10",
    start: String = "2026-07-10T16:00:00+02:00",
): FestivalDayDto =
    FestivalDayDto(
        id = id,
        name = "Vendredi",
        date = date,
        start = start,
        end = "2026-07-11T02:00:00+02:00",
        provenance = "confirmed",
    )

private fun slotDto(
    id: String = "2026:dj-alf-fri",
    happeningId: String = "dj-alf",
    dayId: String = "2026:fri",
    start: String = "2026-07-10T17:00:00+02:00",
    end: String = "2026-07-10T18:30:00+02:00",
): SlotDto =
    SlotDto(
        id = id,
        happeningId = happeningId,
        dayId = dayId,
        start = start,
        end = end,
        provenance = "confirmed",
    )

private fun artistDto(category: String = "musique"): HappeningDto =
    HappeningDto(
        id = "dj-alf",
        kind = "artist",
        name = "DJ ALF",
        category = category,
        provenance = "confirmed",
        artist = HappeningDto.Artist(genres = listOf("House"), links = listOf(LinkDto(type = "website", url = "https://djalf.ch/"))),
    )

private fun activityDto(
    price: PriceDto =
        PriceDto(
            free = false,
            tiers = listOf(PriceDto.Tier(label = "Adulte", amount = 25.0, currency = "CHF", per = null)),
            deposit = PriceDto.Deposit(amount = 50.0, currency = "CHF", note = "Caution casque"),
            provenance = "confirmed",
        ),
): HappeningDto =
    HappeningDto(
        id = "silent-party",
        kind = "activity",
        name = "Silent Party",
        category = "musique",
        provenance = "confirmed",
        activity = HappeningDto.Activity(price = price, bookingRequired = true),
    )

private fun standDto(): HappeningDto =
    HappeningDto(
        id = "vegan-fabrik",
        kind = "stand",
        name = "Vegan Fabrik",
        category = "musique",
        provenance = "confirmed",
        stand =
            HappeningDto.Stand(
                offering = "Cuisine végétale",
                marks = listOf("végan", "bio"),
                menu =
                    listOf(
                        MenuGroupDto(
                            id = "plats",
                            name = "Plats",
                            description = "Cuisine mijotée",
                            source = "Liste transmise",
                            items =
                                listOf(
                                    MenuGroupDto.Item(
                                        name = "Le Végé",
                                        price = MoneyDto(amount = 15.0, currency = "CHF"),
                                        marks = listOf("végé"),
                                        provenance = "unverified",
                                    ),
                                ),
                        ),
                    ),
            ),
    )

// endregion
