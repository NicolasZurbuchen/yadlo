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
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Accessibility
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Assistance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Charter
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Contact
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FaqEntry
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.common.content.domain.model.InfoLink
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Involvement
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Payment
import io.nicolaszurbuchen.yadlo.common.content.domain.model.SocialLink
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Story
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Transport
import io.nicolaszurbuchen.yadlo.common.content.domain.model.TransportMode

/**
 * No references to resolve and no instants to parse — `festival.json` is flat by the time it
 * reaches here, and the night-bus times stay the strings they were published as. The only
 * conversion is [String.toProvenanceEnum], which rejects the whole file on an unknown value.
 *
 * [FestivalDto.schemaVersion] is dropped rather than mapped: whether this build may read the file
 * at all is decided before mapping, not carried into the domain.
 */
fun FestivalDto.toDomain(): Festival =
    Festival(
        name = name,
        tagline = tagline,
        currentEditionId = currentEditionId,
        minSupportedAppVersion = minSupportedAppVersion,
        social = social.map { SocialLink(id = it.id, name = it.name, url = it.url) },
        links = links.map { it.toDomain() },
        story = story?.toDomain(),
        faq = faq.map { it.toDomain() },
        // The wrapper is dropped here: `responsable` holds one list and nothing else, so a domain
        // type around it would only be an extra hop between the screen and the charters.
        charters = responsible?.charters.orEmpty().map { it.toDomain() },
        contact = contact?.toDomain(),
        transport = transport?.toDomain(),
        payment = payment?.toDomain(),
        accessibility = accessibility?.toDomain(),
        assistance = assistance?.toDomain(),
        involvement = involvement?.toDomain(),
    )

private fun StoryDto.toDomain(): Story =
    Story(
        foundedYear = foundedYear,
        body = body,
        passage =
            passage?.let {
                Story.Passage(
                    title = it.title,
                    body = it.body,
                    provenance = it.provenance.toProvenanceEnum("histoire.journee.provenance"),
                )
            },
        provenance = provenance.toProvenanceEnum("histoire.provenance"),
    )

private fun FaqEntryDto.toDomain(): FaqEntry =
    FaqEntry(
        id = id,
        question = question,
        answer = answer,
        provenance = provenance.toProvenanceEnum("faq[$id].provenance"),
    )

private fun CharterDto.toDomain(): Charter =
    Charter(
        id = id,
        name = name,
        body = body,
        url = url,
        provenance = provenance.toProvenanceEnum("responsable.charters[$id].provenance"),
    )

private fun ContactDto.toDomain(): Contact =
    Contact(
        addressLines = address.lines,
        phone = phone,
        emails =
            emails.map {
                Contact.Email(id = it.id, address = it.address, label = it.label, responsible = it.responsible)
            },
        provenance = provenance.toProvenanceEnum("contact.provenance"),
    )

private fun TransportDto.toDomain(): Transport =
    Transport(
        modes =
            modes.map { mode ->
                TransportMode(
                    id = mode.id,
                    name = mode.name,
                    body = mode.body,
                    links = mode.links.map { it.toDomain() },
                    // Null and empty both mean "this mode has no timetable", and every mode but the
                    // night bus publishes null. Flattened so no screen has to tell them apart.
                    departures =
                        mode.departures.orEmpty().map { departure ->
                            TransportMode.Departure(
                                id = departure.id,
                                night = departure.night,
                                times =
                                    departure.times.map {
                                        TransportMode.Departure.Time(time = it.time, note = it.note)
                                    },
                            )
                        },
                )
            },
        provenance = provenance.toProvenanceEnum("transports.provenance"),
    )

private fun PaymentDto.toDomain(): Payment =
    Payment(
        methods = methods.map { Payment.Method(id = it.id, name = it.name, accepted = it.accepted) },
        notes = notes.map { Payment.Note(id = it.id, body = it.body) },
        links = links.map { it.toDomain() },
        provenance = provenance.toProvenanceEnum("paiement.provenance"),
    )

private fun AccessibilityDto.toDomain(): Accessibility =
    Accessibility(
        items =
            items.map {
                Accessibility.Item(id = it.id, name = it.name, available = it.available, note = it.note)
            },
        contactEmailId = contactEmailId,
        provenance = provenance.toProvenanceEnum("accessibilite.provenance"),
    )

private fun AssistanceDto.toDomain(): Assistance =
    Assistance(
        emergencyNumbers =
            emergencyNumbers.map {
                Assistance.EmergencyNumber(id = it.id, label = it.label, number = it.number)
            },
        lostPropertyEmailId = lostPropertyEmailId,
        provenance = provenance.toProvenanceEnum("besoin.provenance"),
    )

private fun InvolvementDto.toDomain(): Involvement =
    Involvement(
        volunteering =
            volunteering?.let {
                Involvement.Volunteering(
                    name = it.name,
                    body = it.body,
                    perks = it.perks,
                    signupUrl = it.signupUrl,
                    contactEmailId = it.contactEmailId,
                    provenance = it.provenance.toProvenanceEnum("simpliquer.hotstaff.provenance"),
                )
            },
        partnership =
            partnership?.let {
                Involvement.Partnership(
                    name = it.name,
                    body = it.body,
                    contactEmailId = it.contactEmailId,
                    provenance = it.provenance.toProvenanceEnum("simpliquer.partenaire.provenance"),
                )
            },
    )

private fun InfoLinkDto.toDomain(): InfoLink =
    InfoLink(
        id = id,
        label = label,
        sublabel = sublabel,
        url = url,
    )
