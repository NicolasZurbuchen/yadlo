package io.nicolaszurbuchen.yadlo.common.reminder.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Slot
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.common.reminder.domain.model.Reminder
import io.nicolaszurbuchen.yadlo.common.reminder.domain.model.ReminderMilestone
import io.nicolaszurbuchen.yadlo.common.reminder.domain.model.ReminderSubject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class PlanRemindersUseCaseTest {
    // The 2026 edition ran 10-12 July. Instants are written absolutely rather than computed from the
    // same expressions the production code uses, so a lead time that drifts still fails.

    @Test
    fun savedSlot_isRemindedThirtyMinutesBeforeItStarts() {
        val reminders = plan(saved = listOf(savedSlot("2026:dubside-sat")))

        assertEquals(
            Instant.parse("2026-07-11T21:30:00+02:00"),
            reminders.slotReminders().single().at,
        )
    }

    @Test
    fun savedSlot_carriesTheHappeningRatherThanTheSlot() {
        // A Slot has no screen of its own, so the tap has to open the fiche behind it. The name rides
        // along too: the sentence is assembled outside the domain and nothing there can look it up.
        val subject = plan(saved = listOf(savedSlot("2026:dubside-sat"))).slotReminders().single().subject

        assertEquals(
            ReminderSubject.SlotStarting(
                happeningId = "dubside",
                happeningName = "Dubside",
                startsAt = Instant.parse("2026-07-11T22:00:00+02:00"),
            ),
            subject,
        )
    }

    @Test
    fun savedSlot_goesStaleWhenTheSlotEnds_notWhenTheReminderFires() {
        // The whole point of carrying an end: a notification delivered two minutes ago about a set
        // starting in twenty-eight is the opposite of stale, and a sweep keyed off the reminder's
        // own instant would clear exactly the one still worth reading.
        assertEquals(
            Instant.parse("2026-07-11T23:00:00+02:00"),
            plan(saved = listOf(savedSlot("2026:dubside-sat"))).slotReminders().single().staleAfter,
        )
    }

    @Test
    fun unsavedSlot_isNotReminded() {
        assertTrue(plan(saved = emptyList()).slotReminders().isEmpty())
    }

    @Test
    fun savedStand_isNotReminded() {
        // A Wishlist entry is a checklist item, not an appointment — CONTEXT.md § Wishlist. The bar
        // being open from 12:00 to 02:00 is not a fourteen-hour thing to be warned about.
        val saved = listOf(SavedItem(id = "food-truck", kind = SavedKind.STAND, editionId = "2026"))

        assertTrue(plan(saved = saved).slotReminders().isEmpty())
    }

    @Test
    fun savedIdFromAnotherEdition_matchesNothing() {
        // Slot ids are Edition-qualified precisely so last year's plan cannot resurrect itself. The
        // saved row survives, it simply stops matching.
        val saved = listOf(SavedItem(id = "2025:dubside-sat", kind = SavedKind.SLOT, editionId = "2025"))

        assertTrue(plan(saved = saved).slotReminders().isEmpty())
    }

    @Test
    fun reminderInstantAlreadyPassed_isNotScheduled() {
        // Twenty-nine minutes out: the moment to warn somebody has gone and the set has not started.
        // Firing now would be a notification about something the visitor is already late for.
        val reminders =
            plan(
                saved = listOf(savedSlot("2026:dubside-sat")),
                now = "2026-07-11T21:31:00+02:00",
            )

        assertTrue(reminders.slotReminders().isEmpty())
    }

    @Test
    fun everythingSaved_staysUnderThePlatformCap() {
        // iOS drops local notification requests past 64, silently, with no error and no log. The real
        // Edition has 48 Slots so this is headroom rather than a limit anyone reaches — but the count
        // is content-driven, and nothing else in the app would notice an Edition that doubled.
        val slots = (1..80).map { index -> slot(id = "2026:s$index", happeningId = "h$index", start = "2026-07-11T22:00:00+02:00") }

        val reminders = plan(saved = slots.map { savedSlot(it.id) }, slots = slots)

        assertTrue(reminders.size <= PLATFORM_CAP, "planned ${reminders.size}")
    }

    @Test
    fun theCapKeepsTheNearestSlots() {
        // Which ones survive matters as much as how many: dropping tonight's set to keep Sunday's
        // would meet the cap and defeat the feature. Every Slot here is still ahead of `now`, so the
        // only thing that can remove one is the cap itself.
        val slots =
            (1..80).map { index ->
                slot(id = "2026:s$index", happeningId = "h$index", start = Instant.parse("2026-07-10T18:00:00+02:00").plus(index.hours))
            }

        val planned = plan(saved = slots.map { savedSlot(it.id) }, slots = slots, now = "2026-07-10T12:00:00+02:00").slotReminders()
        val plannedIds = planned.map { it.id }.toSet()
        val dropped = slots.filterNot { slot -> "$SLOT_ID_PREFIX${slot.id}" in plannedIds }

        assertTrue(planned.isNotEmpty() && dropped.isNotEmpty())
        assertTrue(planned.maxOf { it.at } < dropped.minOf { it.start }, "the cap dropped a nearer Slot")
    }

    @Test
    fun milestonesAreScheduled_evenWithAnEmptyPlan() {
        // Somebody who has saved nothing is exactly who these are for.
        val milestones = plan(saved = emptyList(), now = "2026-01-01T12:00:00+01:00").milestones()

        assertEquals(
            listOf(ReminderMilestone.APPROACHING, ReminderMilestone.LIVE, ReminderMilestone.ENDED),
            milestones.map { it.milestone },
        )
    }

    @Test
    fun milestonesLandOnTheirOwnInstants() {
        val milestones = plan(saved = emptyList(), now = "2026-01-01T12:00:00+01:00").milestones()

        assertEquals(
            listOf(
                // J-7, the same boundary DerivePhaseUseCase turns APPROACHING on.
                Instant.parse("2026-07-03T00:00:00+02:00"),
                // Ten in the morning on the Friday, NOT midnight. The Phase turns over at 00:00, and
                // a notification there tells somebody the festival is today — the night before.
                Instant.parse("2026-07-10T10:00:00+02:00"),
                // The morning after the last day, over breakfast.
                Instant.parse("2026-07-13T11:00:00+02:00"),
            ),
            milestones.map { it.at },
        )
    }

    @Test
    fun milestonesAlreadyPassed_areNotScheduled() {
        // Mid-festival: the week-out warning and the day-one announcement have both gone.
        val milestones = plan(saved = emptyList(), now = "2026-07-11T14:00:00+02:00").milestones()

        assertEquals(listOf(ReminderMilestone.ENDED), milestones.map { it.milestone })
    }

    @Test
    fun noPublishedProgramme_dropsTheWeekOutMilestoneAndKeepsTheRest() {
        // The same gate DerivePhaseUseCase applies to APPROACHING, for the same reason: a week-out
        // nudge points at a Plan there is nothing to fill in. The festival happens either way, so
        // the other two stand.
        val milestones =
            plan(saved = emptyList(), hasPublishedProgramme = false, now = "2026-01-01T12:00:00+01:00")
                .milestones()

        assertEquals(listOf(ReminderMilestone.LIVE, ReminderMilestone.ENDED), milestones.map { it.milestone })
    }

    @Test
    fun noDays_dropsEveryMilestoneAndLeavesTheSlotsAlone() {
        // Off season, or an edition published before the dates were known: there is no boundary to
        // announce. A saved Slot is deliberately unaffected — it carries its own hours, and coupling
        // it to the day list would mean content missing a field silently cancelling a reminder.
        val reminders = plan(saved = listOf(savedSlot("2026:dubside-sat")), days = emptyList())

        assertTrue(reminders.milestones().isEmpty())
        assertEquals(1, reminders.slotReminders().size)
    }

    @Test
    fun everythingIsOrderedByWhenItFires() {
        // The order the scheduler hands to the platform, and the order the cap reads.
        val reminders = plan(saved = listOf(savedSlot("2026:dubside-sat")), now = "2026-01-01T12:00:00+01:00")

        assertEquals(reminders.map { it.at }.sorted(), reminders.map { it.at })
    }

    @Test
    fun idsAreUniqueWithinAPass() {
        // The one guarantee an id has to make, because a pass replaces everything rather than diffing
        // against it. Two reminders sharing an id would silently become one on both platforms.
        val reminders = plan(saved = listOf(savedSlot("2026:dubside-sat")), now = "2026-01-01T12:00:00+01:00")

        assertEquals(reminders.size, reminders.map { it.id }.toSet().size)
    }

    private fun plan(
        saved: List<SavedItem>,
        slots: List<Slot> = defaultSlots(),
        days: List<FestivalDay> = defaultDays(),
        hasPublishedProgramme: Boolean = true,
        now: String = "2026-07-11T14:00:00+02:00",
    ): List<Reminder> =
        PlanRemindersUseCase().invoke(
            saved = saved,
            slots = slots,
            days = days,
            hasPublishedProgramme = hasPublishedProgramme,
            now = Instant.parse(now),
        )

    private fun List<Reminder>.slotReminders() = filter { it.subject is ReminderSubject.SlotStarting }

    private fun List<Reminder>.milestones() =
        mapNotNull { reminder ->
            (reminder.subject as? ReminderSubject.MilestoneReached)
                ?.let { MilestoneReminder(at = reminder.at, milestone = it.milestone) }
        }

    private data class MilestoneReminder(
        val at: Instant,
        val milestone: ReminderMilestone,
    )

    private fun savedSlot(id: String) = SavedItem(id = id, kind = SavedKind.SLOT, editionId = "2026")

    private fun defaultSlots() =
        listOf(
            slot(id = "2026:dubside-sat", happeningId = "dubside", start = "2026-07-11T22:00:00+02:00"),
        )

    private fun slot(
        id: String,
        happeningId: String,
        start: String,
    ) = slot(id = id, happeningId = happeningId, start = Instant.parse(start))

    private fun slot(
        id: String,
        happeningId: String,
        start: Instant,
    ) = Slot(
        id = id,
        happening =
            Happening.Artist(
                id = happeningId,
                name = happeningId.replaceFirstChar { it.uppercase() },
                category = Category(id = "musique", name = "Musique", order = 0),
                description = null,
                images = emptyList(),
                provenance = Provenance.CONFIRMED,
                genres = emptyList(),
                links = emptyList(),
            ),
        day = defaultDays()[1],
        start = start,
        end = start.plus(1.hours),
        provenance = Provenance.CONFIRMED,
    )

    private fun defaultDays() =
        listOf(
            day(id = "2026:fri", start = "2026-07-10T16:00:00+02:00", end = "2026-07-11T02:00:00+02:00"),
            day(id = "2026:sat", start = "2026-07-11T12:00:00+02:00", end = "2026-07-12T03:00:00+02:00"),
            day(id = "2026:sun", start = "2026-07-12T12:00:00+02:00", end = "2026-07-12T22:00:00+02:00"),
        )

    private fun day(
        id: String,
        start: String,
        end: String,
    ) = FestivalDay(
        id = id,
        name = id,
        date = start.substringBefore("T"),
        start = Instant.parse(start),
        end = Instant.parse(end),
        provenance = Provenance.CONFIRMED,
    )

    private companion object {
        /** Mirrors PlanRemindersUseCase.MAX_SCHEDULED, which is private and should stay that way. */
        const val PLATFORM_CAP = 60

        /** Likewise SLOT_PREFIX. Spelled out here so the id scheme cannot change unnoticed. */
        const val SLOT_ID_PREFIX = "slot:"
    }
}
