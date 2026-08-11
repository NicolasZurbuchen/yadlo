# Yadlo

A companion app for the Yadlo festival — a three-day lakeside music, sport and beach
festival held at Préverenges (Vaud, Switzerland) each July since 2015.

The app's reason to exist is that the festival's information has never existed as
structured data: it lives in a page-builder site, much of it inside images. The app is
the first place the programme, the activities, the stands and the practical information
are modelled as data — which is what makes a personal schedule, reminders and offline
access possible at all.

The festival's public language is French. This glossary is in English (the codebase is
Kotlin); the French rendering is noted where a user-facing term differs.

## Language

### The festival in time

**Edition**:
One year's festival, and the frozen record of it — its programme, activities, stands,
prices, partners and closing statistics. An Edition never changes once it is over.
Contrast with the festival's live information, which is always the current truth no
matter which Edition is being viewed.
_Avoid_: Year, instance

**Provenance**:
How trustworthy a piece of content is: confirmed by the organisers, taken from a past
Edition's record, or reconstructed from public sources and unverified. Carried by
anything curated rather than given — prices above all.
_Avoid_: Source, confidence, status

**FestivalDay**:
One named day of an Edition, whose start and end are **the hours the site is open** to the
public that day — Friday 16:00 to 02:00, not a calendar date. Friday's window runs past
midnight, which is why a 01:30 set still belongs to Friday.

Two things follow, and both matter:

- **A Slot may fall outside it.** The beach at Préverenges is public, so the yoga and the
  climbing wall run from 10:00 on days the site opens at 12:00. The window is what visitors are
  told, not a bounding box around the programme.
- **Which day a Slot belongs to is authored on the Slot**, never inferred by testing an instant
  against these times. That is what keeps a 01:30 set on Friday without the window having to be
  stretched to prove it.
_Avoid_: Day, date, opening hours as a separate concept

**Phase**:
Where the year is, from the app's point of view — off season, announced, approaching, live,
or ended. Derived from the clock and the published content, never set by hand. Deliberately
wider than the FestivalDays it surrounds: at 08:00 on the opening Friday the festival is
already live, whatever time the gates open.
_Avoid_: State, mode, season, status

**Slot**:
One Happening occurring at one time, in one Lane, at one Venue, on one FestivalDay. The
atomic unit of the programme: a Happening with three timeslots has three Slots. This is
what a user favourites and what a reminder fires for. Every Slot behaves the same way,
whether it is a two-hour concert or a seven-hour open activity — you can arrive late to
either.
_Avoid_: Event, session, occurrence, showing

### The things themselves

**Happening**:
A thing the festival offers, with its own identity, description, images and detail
screen. One Happening has one or more Slots. Sealed into three variants — Artist,
Activity and Stand — which share identity and differ in their detail payload.
_Avoid_: Event, item, entry, attraction

**Artist**:
A Happening variant: a musical act. Carries genre, biography, links, and the stage its
Slot plays on.
_Avoid_: Act, performer, band

**Activity**:
A Happening variant: something to do rather than watch — sport, games, wellness,
children's offerings. Carries price, booking requirement, equipment and suitability.
_Avoid_: Attraction, experience

**Stand**:
A Happening variant: somewhere present on the site that you visit rather than attend —
food trucks, the main bar, clothing and craft sellers, game stands. Carries what it
offers and at what price. Its Slots are opening windows.

The line between a Stand and an Activity is whether the festival programmed it. An
Activity has hours the organisers set and publish; a Stand is simply there while the
site is open.
_Avoid_: Food truck, vendor, booth, kiosk, shop

**Menu**:
What a Stand offers, arranged in named Groups — plats, menus, boissons — each holding Items.
An Item has a name and a price, and may have a description and dietary Marks; most have
neither. The most valuable content in the app, and the one thing that exists nowhere else.
_Avoid_: Carte, price list, offering

**Mark**:
A short dietary or preparation note — végé, végan, sans gluten, sans lactose, piquant, bio.
Always written out, never a pictogram.

A Mark sits at one of two levels, and the level is the meaning:

- **On the Stand**, it describes the whole stand. Vegan Fabrik is `végan` and `bio` because
  everything it sells is.
- **On an Item**, it describes only that item. De l'Or Bokit carries no Mark, but its `Le Végé`
  is `végé` — feta, so vegetarian and not vegan.

This is what separates "this stand is entirely vegan" from "this stand has a vegan option", which
is the actual question someone is asking when they scan a row of food stands. A stand-level Mark
must never be repeated on every one of its items.
_Avoid_: Tag, label, icon, allergen

### Where things are, and how they are laid out

**Venue**:
A physical place a visitor walks to, and the unit the site map pins. The lake, the
beach, the main stage, the children's corner.
_Avoid_: Location, place, area, spot

**Lane**:
One row of the Timetable — a presentation axis, deliberately distinct from Venue. Three
Lanes (GladiaSUP, SUP Yoga, floating trampoline) can share the Venue "the lake", and one
Lane ("Musique") can host Slots at two stages. Lanes are declared in content, ordered,
and grouped into Sections.
_Avoid_: Row, track, stage, channel

**Section**:
A named, collapsible group of Lanes in the Timetable — Musique, Sur l'eau, Sur terre,
Enfants, Restauration.
_Avoid_: Group, category

**Timetable**:
The day-by-day grid view: Lanes down the side, time across, one grid per FestivalDay.
Rendered to the user as _la grille_.
_Avoid_: Grid, schedule, programme

### The user's own festival

**Plan**:
The saved Artist and Activity Slots, ordered in time. Their personal festival, shown as
_mon programme_. The app never warns that two of them overlap: at a site you can cross in
two minutes, catching half of each is a normal evening, not a mistake.
_Avoid_: Schedule, calendar, agenda, favourites

**Wishlist**:
The saved things the festival did not programme — Stands of every sort, grouped by what
they offer. A checklist, not a schedule: nothing here has a time or reminds you of anything.
Reached from a single full-width tile in Mon Yadlo. Shown as _à essayer_.
_Avoid_: Bookmarks, saved, interested, later, favourites

**Live state**:
Where a Slot sits relative to now — upcoming, running, ending soon, or finished. The only
thing that distinguishes one saved Slot from another on screen, and it is written in words
rather than expressed as position or layout.
_Avoid_: Status, phase, progress
