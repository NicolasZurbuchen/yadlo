# Decisions

Running record for the Yadlo companion app. Vocabulary lives in [CONTEXT.md](./CONTEXT.md);
this file is what we chose and what is still open.

Context: the 2026 edition ran 10–12 July 2026. Next edition ~July 2027, lineup expected
around May 2027. Roughly eleven months of runway, with no live event to test against.

## Settled

**Status.** Unofficial / portfolio build now, aiming to become the official app for 2027.
Consequence: no broadcast push and no volunteer features until the association is on board.

**Comms.** An in-app feed of annonces from `announcements.json`, **local notifications on both
platforms**, and **remote push still deferred**. The local half is built: a reminder before each
saved Slot, and three that mark the turns of the year. Story 17 was dropped and story 16's
dormant-user case is what push would buy — see § Notifications below.

Everything else that looks time-driven reads the injected clock and recomputes on the ticker, so
Phase, the live-state pills, the countdowns and Mon Yadlo are unaffected by any of it.

Push stays behind the same interface so FCM can drop in
without a rewrite. Volunteer group chat stays out of scope — it is a second product and requires
being official.

**Content substrate.** Versioned JSON in a repo, served as static HTTPS, fetched on launch and
cached to disk. No CMS and no Firebase for now. The website's content is largely trapped in images
and in a page-builder tree; the app is the first place it exists as structured data.

**No bundled snapshot in v1.** An earlier draft shipped a copy of the content inside the app as a
cold-start fallback. It is not worth it, and the reasoning is worth keeping so it does not get
relitigated:

- **An app cannot be installed without connectivity.** A bundle therefore only ever serves someone
  who installed the app, never opened it, travelled, and *then* lost signal. That is a real person,
  but a narrow one.
- **The failure screen has to exist either way.** A bundle can be absent, or stale, or from an
  edition that has ended. So bundling never removed the "no content yet" screen — it only optimised
  past it.
- **The cache is what makes the app work on the beach**, and it is warm for everyone who has opened
  the app once, which is everyone who is at the festival with it installed.

The cost of bundling is a second source of truth that goes quietly out of date, and the classic
failure it produces — an app confidently showing last year's programme — is worse than an honest
empty state. It returns behind the same interface if this proves wrong, and the stale-year problem
then has a cheap fix: expire the bundle past its edition's last day, against the injected clock.

**First launch, in order.** Splash draws from bundled images, so there is never a spinner on a blank
screen. Then `festival.json` — small, and it names `currentEditionId` — then that edition, then
`announcements.json`. Nothing blocks the UI on the network: the fetch publishes into a `StateFlow`
and the screen updates when it lands. With no signal and no cache, the app says one connection is
needed rather than spinning.

**The cache is a document, not a schema.** Stored as one blob and parsed once into memory, rather
than shredded into SQL tables. Every read wants the whole bundle, references only resolve after
parsing, and a relational schema would buy nothing while costing a migration on every content
change. Parsing once matters: a UseCase that re-reads and re-resolves per query re-parses 45 KB for
each screen.

**A refresh never deletes saved data.** A Slot disappearing from the file means *no longer in the
programme*, never a silent removal from someone's Plan.

**`schemaVersion` is the only "update the app" trigger, and it never hard-blocks.** An unofficial
festival app that bricks itself on the Saturday afternoon is worse than one showing week-old data.
Content changes additively — the client parses with `ignoreUnknownKeys`, so an older app ignores
fields it does not know, and no bump is needed. A major bump means the app keeps its cache, refuses
to parse the newer file, and shows a soft update row in Plus. `minSupportedAppVersion` on
`festival.json` is the escape hatch rather than a separate manifest, since that file is fetched
first anyway. Versioned paths (`/v1/`, `/v2/`) are the last resort for a break that cannot be made
additive.

**A new edition needs no app release.** `currentEditionId` moves, the app fetches the new edition.
The one piece of code it requires is clearing the Plan when that id changes — Edition-qualified Slot
ids stop stale saves colliding, but they would linger as orphans, so rows not carrying the current
edition prefix are dropped explicitly.

**Two bundles, split by frozen record vs live truth.**

```
content/
  festival.json          live:   histoire, contact, réseaux sociaux,
                                 transports, paiement, Hot'Staff, devenir partenaire
  editions/
    2026.json            frozen: programme, activités, stands, menus, prix,
                                 horaires, partenaires, chiffres de l'édition
    2027.json
    2026/images/
```

One file per edition — the whole thing is 60–150 KB, so a single fetch and a single ETag
buys atomic consistency: the app is never in a state where the programme updated but the
stands did not. Splitting would buy partial updates nobody needs at this size.

The test for which bundle something belongs in is not "does it change every year" — it is
**would a past-edition archive need its own copy?** Browsing the 2026 archive should show
2026's lineup, 2026's partners and 2026's statistics, but *today's* contact address. The
festival's closing statistics ("6000 visiteurs, 160 bénévoles, 3200 litres de bière") are a
fact about one specific edition and belong in the frozen file — which also lets the ENDED
phase report on the edition that just finished, and lets the numbers become a series as
archives accumulate.

**The domain model resolves references; the graph stays one-way.** The bundle is atomic and that
stands, but the mapper's job is to turn it into something usable rather than restate it, so a `Slot`
carries its `Happening` and its `FestivalDay` and a `Happening` carries its `Category`. The
direction is not stylistic: `Happening` carrying its Slots back would make the graph cyclic, and a
cyclic `data class` blows the stack on `equals`, `hashCode` and `toString`. A fiche needs both, and
gets a small aggregate assembled by a UseCase rather than a back-reference.

**Two things the content model deliberately does not know.** *Live state* — `dans 15 min`,
`en cours`, `se termine`, `terminé` — is derived from the clock and a Slot's window on the same
ticker that drives Phase, not stored. And *whether something is saved* lives in local storage, so
every Programme and Mon Yadlo row is a join across two repositories. Putting either in the bundle
would mean content that changes without anyone publishing it.

**Provenance on curated content.** Anything reconstructed rather than given carries how
reliable it is — confirmed, archived, or unverified. Prices above all. The field goes in
the data now; whether the UI ever surfaces it ("prix 2026, à confirmer") is deferred. When
the association confirms a price, that is a field flip rather than a re-authoring.

**Images.** Remote, loaded with Coil3 and disk-cached; only app chrome and category icons
are bundled.

**The splash screen is the exception, and it is absolute.** Its background photograph, the Yadlo
wordmark and the two `soutien-public` logos are all bundled in the app. A splash draws before any
fetch completes — that is its entire job — so a remote image there means either waiting on the
network or rendering incomplete on a first launch, which is the one launch that has no cache to fall
back on. Using the platform splash APIs later would make it not merely slow but impossible: those
drawables are resolved before the `Application` class runs.

This duplicates two logos that also exist in the content, and the duplication is the point. Changing
a public backer needs an app release, which is fine at a timescale of years; the Partners screen
keeps reading the live list. The real payload is ~10 artist photos and ~20 partner logos per edition —
activity and stand photos are decorative and can wait.

**Editions.** Content is scoped by Edition from day one; the v1 UI shows a single Edition
with no switcher. Past editions are worth keeping as *content* (who played in 2026);
the user's own Plan is not worth preserving across years.

**Phase.** One navigation structure throughout the year. Home is a stack of blocks whose
visibility is driven by the phase. No separate "off-season app".

**Phase is derived, never authored.** No field to remember to flip — the one weekend you
would forget is the weekend you are at the festival rather than at a laptop.

```
OFF_SEASON    default
ANNOUNCED     edition has published slots   →  J-7
APPROACHING   J-7                           →  00:00 on day one
LIVE          00:00 on day one              →  11:00 the morning after the last day
ENDED         then                          →  +6 weeks
```

Only two inputs: the clock, and the content last fetched. Every boundary but one moves on
the clock alone. The exception is ANNOUNCED, which moves when *you publish the programme* —
so the trigger is the work itself, not a step to remember.

ANNOUNCED keys off the programme existing rather than a countdown threshold, because the
hero it triggers claims *"la programmation est là"*. A time window can make that claim
before it is true; `slots.any()` cannot. An edition file with dates but no programme yet
stays in OFF_SEASON, which already shows a countdown — which is why the edition file gets
published early with dates and an empty programme.

**Phase boundaries are deliberately wider than the FestivalDay windows.** A FestivalDay says
when content happens; a phase says where the user's head is. At 08:00 on the Friday you are
mentally at the festival even though nothing opens until the afternoon — so LIVE starts at
midnight on day one, spans the 03:00–09:00 gaps between days, and runs to the morning after.
Otherwise the app would say "merci, à l'année prochaine" at 23:01 on the Sunday, while
people are still on the beach finishing a beer.

**APPROACHING exists for one reason: it is the only time anyone will build their Plan.**
On site, at 30% battery, in the sun, nobody curates a schedule — they do it on the sofa on
the Tuesday before. So the hero stops being "la programmation est là" (exciting on 2 June,
faintly ridiculous on 6 July) and becomes "J-3 · ton programme t'attend", pointing at
Mon Yadlo. Alongside it: préparer sa venue — transports, paiement, accessibilité,
réservations.

**The "not open right now" screen carries LIVE's quiet hours.** One component, three uses:
before opening on day one, in the gap between two days, and after the last day. When there
is a next FestivalDay it reads *demain dès 09:00*; when there is not, it says goodbye
properly. That last variant is what makes the handover to ENDED land over breakfast.

*Built as a fourth `YadloHero` on Accueil rather than as a screen of its own, and with a fourth
state — the site being open.* Counting the real 2026 windows settled it: Friday runs 16:00–02:00,
Saturday 12:00–03:00, Sunday 12:00–22:00, and LIVE spans midnight Friday to 11:00 Monday, so **the
site is shut for 48 of LIVE's 83 hours**. Sixteen before the gates, ten and nine overnight, thirteen
between Sunday's close and the handover. A block that only appears in the gaps would be the tab's
main content anyway; and in the remaining 35 hours Accueil would fall back to a social row plus
whatever was posted in the last day, which is a screen that reads as broken rather than as closed.

The open variant says how long is left — *ouvert jusqu'à 02:00* — rather than pointing at the
Programme and nothing else. That is the one useful fact this tab has during opening hours, and it
is nowhere else on it; a hero that only said "go and look at the other tab" would be a signpost to
the tab the app already opened on, which is the rule against a home tab that summarises other tabs.

The goodbye is deliberately *not* the thank-you. It is thirteen hours of *à l'année prochaine,
rentrez bien* and no chevron, because the programme it would open has nothing left on it; the real
*Merci.* with the photograph arrives with ENDED the next morning. Two steps rather than one is what
stops the weekend ending on a cliff at 22:00 on the Sunday.

**Closed is one state, not two, and the copy has to survive both readings of it.** The same variant
is what someone sees at 02:30 leaving the beach and at 10:00 the next morning over coffee. *C'est
fini pour ce soir* is right for the first and wrong for the second, so the wording is the neutral
*le site est fermé · réouverture à 12:00* — the reopening time being the part either reader wants.

**Recompute while running, not only at launch.** At 08:55 someone is in APPROACHING; at
09:00 they should be in LIVE without killing the app. The same ticker drives the `en cours`
pills and the "dans 15 min" countdowns. It is also why the clock must be settable from the
first commit — the LIVE phase is otherwise untestable until July 2027.

**Compare in the festival's timezone.** FestivalDay windows are instants in `Europe/Zurich`.
Compare absolute instants and format in Zurich time, never against the device's wall clock —
this is the kind of thing that works all year and breaks on the one weekend that matters.

**Accueil, block by block.**

**~~Global search appears only when there is a programme to search.~~ Reversed: it is there all
year.** The original reading was ANNOUNCED, APPROACHING and LIVE, on the grounds that between
editions there is nothing to find but last year’s archive. That undercounted the corpus. Half of
it is *live truth rather than an edition* — paiement, horaires, comment venir, devenir bénévole,
nous écrire, l’histoire — and none of it expires when the festival ends; off season it is the half
a reader is most likely to want, which this Phase’s own quick-access block already concedes by
promoting two of them. The other half is last July’s line-up, which is the edition the bundle
holds anyway and is exactly what *revivre l’édition* would show. "An empty search box" was the
thing being argued against, and the box is not empty. One search, one results screen, reached from
Accueil and from the toolbar — two doors, one implementation.

*OFF_SEASON* — recherche · countdown · annonces · revivre l'édition précédente · s'impliquer ·
newsletter · l'histoire de Yadlo · réseaux sociaux

*ANNOUNCED* — recherche · countdown · **hero: la programmation est là** · annonces ·
s'impliquer · réseaux sociaux. One job: announce the programme and send people to it.
No history block — the reader has already decided to come. Réservations moved to
APPROACHING, where they are actually actionable.

*APPROACHING* — recherche · countdown · **hero: ton programme t'attend** → Programme ·
préparer sa venue (paiement, comment venir, accessibilité) · réservations · annonces.
No plan preview: the hero is already the one route to building a Plan, and a second would
be the duplication rule again.

*The hero pointed at Mon Yadlo until the Accueil implementation, and that was wrong.* A Plan
is built by saving rows in Programme; Mon Yadlo only recalls what is already saved and has
no add-flow by design. So the hero that says "choose your weekend" was landing the reader on
an empty tab, one short of the thing it had just asked them to do.

*LIVE* — recherche · **hero: where the weekend is up to** · annonces du jour · lien Instagram.
The hero is present throughout rather than only in the gaps — see above. Deliberately thin, because during LIVE **the app opens on Programme,
not Accueil.** No plan du site and no stands here: both already live in Plus › Sur place,
and duplicating them is the thing we are avoiding.

**No live Instagram feed in v1.** The website embeds one and it is the obvious way to make
LIVE's Accueil feel alive, but it is not reachable unofficially: Instagram's Basic Display
API was shut down at the end of 2024, and the Graph API needs a Business account plus a Meta
app authorised *by the account owner*. Scraping breaks constantly and violates their terms,
and republishing photos of identifiable people from an app that is not theirs is its own
problem. So: a link-out card for now, and a real feed becomes another thing that going
official unlocks.

*ENDED* — recherche · merci · Yadlo en chiffres · annonces · archives · newsletter · réseaux sociaux

**The default tab follows the phase.** Accueil for 361 days; Programme, scrolled to now, for
the four days of the festival.

A home tab that only summarises other tabs is a tab that should not exist. Strip everything
from the LIVE Accueil that duplicates another tab and only announcements remain — which is
the correct answer, not a gap to fill. Accueil is substantial in OFF_SEASON, ANNOUNCED and
APPROACHING, and thin exactly during the days that matter. Opening on Programme resolves it:
no "Maintenant" block, because you are already looking at now; no "Ensuite" block with an
unanswerable "how many do we show", because Mon Yadlo is one tap away; and no reappearance
of the drop-in versus timed split that B2 exists to avoid.

**It is a start destination, not a redirect, and that distinction is the whole implementation.**
LIVE begins at midnight on the Friday, so a rule that simply keeps the tab in step with the Phase
would pull the screen out from under someone reading the annonces as the date turns — and would
do it again on every later recomposition, undoing whichever tab they had chosen since.
`TabNavigator.selectStart` therefore moves once and is idempotent afterwards, and an explicit tap
closes the question early: a decision the visitor has already made cannot be overruled by content
arriving a beat later.

Decided during composition rather than in an effect. An effect runs after the pass that draws, so
the shell would show one frame of Accueil on the Saturday morning before replacing it; written
before the selected tab is read, the navigator has already moved. The shell is not composed until
the content is Ready — the splash holds until then — so the Phase is known on the first pass and
there is no second chance to wait for.

**"Scrolled to now" was dropped from this.** The valuable half of it was already built and lives in
`ProgrammeStoreFactory`: the day chip opens on the current FestivalDay, which is the part that is
unambiguous and the part that matters. The scrolling half is not. "Now" has no single row when a
14:00–20:00 activity overlaps an 18:00 concert — anchoring on the first unfinished row lands at the
top of the day, and anchoring on the next to start hides what is already running. The trigger is no
better defined: doing it on every visit to the tab throws away a scroll position the visitor
expects to find again, and doing it only on the first visit makes the behaviour unrepeatable.
Against that, a day here is around sixteen rows — two or three screens — so the whole feature saves
a flick. It is a Paléo-sized answer to a problem this festival does not have.

**No last-bus block.** Transport was dropped; an earlier mock reintroduced it by mistake.

**The aftermovie is an annonce, not a block.** It lands in September; ENDED runs mid-July to
late August. A phase block for it would be empty for its entire life.

**No personal recap in ENDED.** The idea died in two steps: the app knows intent and never
attendance, so "tu as vu 5 concerts" is a claim it cannot make; and once the statistics go,
what remains is a list truncated to "et 2 autres", which tells nobody anything. ENDED is
merci + chiffres + annonces + archives, and that is a respectable goodbye for a screen
someone opens once. Emptiness is acceptable here: the website is full, and full is part of
why it is bad.

This also settles the Plan lifecycle — there is nothing to show before clearing, so a
previous edition's Plan is simply discarded when a new Edition publishes.

**The thank-you is the one hero with a photograph, and it is meant to stay the one.** Every other
hero sits above content it introduces — a programme to open, a payment page to read — where a
picture would make the introduction louder than the thing introduced. ENDED has nothing under it
but chiffres and archives; *Merci.* is the whole of what the screen has left to say, and a blue
card there is the app closing the year on a UI component. `img_see_you_soon` is the association's
own photograph, the surfboard sign reading *À tout bientôt*, which says it in the festival's voice
rather than the app's.

Bundled rather than fetched. ENDED runs mid-July to late August and is the phase the app spends
most offline; a thank-you that fails to load is worse than no photograph at all.

Over it the ink is `onScrim` under the fiche's own bottom-weighted scrim, not the hero's usual
`onPrimarySubtle`. That is the same measurement, not a matching exercise: `onPrimarySubtle` was
derived against the bandeau blue and guarantees nothing over a picture, and the scrim's alpha is
the lowest at which white clears 4.5:1 over a *white* photograph. The two treatments live in one
component so that no hero can end up with page ink over a picture.

**The pre-festival heroes stay flat.** *La programmation est là* has no image made for it, and the
candidates were both wrong: reusing the splash photograph would open the app and then sit in the
middle of it, and `editions/2026/images/` is empty — the affiche is the right occupant of that
slot and does not exist yet. A hero with a picture on one phase and without on another is a phase
difference, which is the whole idea of the block stack.

**No live Instagram feed, and no special-cased link either.** LIVE gets the same four social
links as every other phase.

**Design notes never ship inside the UI.** The one exception is the provenance line under
the statistics ("chiffres 2024 — les seuls publiés à ce jour"), which is not commentary but
the provenance field doing its job.

**Hero vs annonce are not duplicates.** An annonce is a record — dated, and it scrolls away
as newer ones arrive. A hero is a state — it stays true until the phase changes. "The
lineup was published" is news; "the lineup is available" is a standing fact. Publish both.

**An annonce carries a nullable URL, not a typed action.** *Reversed — the earlier decision is
recorded below because the reasoning still applies to anything that does deep-link.*

The original design was a typed action so an annonce could point inside the app:

```
action: none | programme(day?) | happening(id) | plus(entry) | url(external)
```

That is more machinery than the job needs. An annonce is a dated record, and the only thing it
has to do is open somewhere; `url: null` means the card simply is not tappable. The cost is real
and worth naming — an annonce can no longer send someone to a specific fiche, only to a web page —
but the failure modes are not symmetric. A dead deep link points at a screen that has been renamed
or an id that no longer exists, inside an app that has already shipped; a dead external URL is an
ordinary broken link, and the user knows what happened.

What survives from the original reasoning: **content outlives app versions.** That is exactly why
the internal targets were dropped rather than kept — an internal link expressed as a string is the
thing that rots.

**Annonces live in their own file**, not in `festival.json`. They are the only content that has to
arrive *during* the festival, when a correction is being pushed from a phone. Folded into
`festival.json`, every annonce would reupload history, contact and transport, and a visitor's
cached copy of all of it would go stale together. Alone, an annonce is a few hundred bytes with its
own ETag, which is what makes polling during LIVE affordable at all.

**An annonce is scoped to an edition**, or to none. One naming an edition the app has not fetched
is dropped rather than rendered half-resolved; `editionId: null` means it is true of the festival
itself and survives every edition.

**Archives.** `Éditions précédentes` under *Le festival* in Plus, fetched on demand from an
`editions/index.json`. This is the only feature that reads a third file, and the only one
that does not work offline unless previously opened — acceptable, since browsing archives
is a November sofa activity, not a July beach one.

**S'impliquer is a router, not a form.** One screen, a "concern" picker, and each choice
opens the destination that already exists: the Préverenges recruitment site for Hot'Staff,
`mailto:` with a prefilled subject for partners, programming and stands. No backend, no
message storage, no becoming a data processor — and critically, the association's existing
recruitment pipeline keeps receiving its applications instead of landing in a personal
inbox that has to forward them by hand during their busiest month.

**Préparer sa venue.** Home borrows the *Sur place* group from Plus — transports, paiement,
réservations — from J-7 and again on each festival morning. The payment rule
in particular is only actionable *before leaving the house*, which is when someone would
otherwise stop at an ATM.

**Tabs.** `Accueil · Programme · Mon Yadlo · Plus`. Food and the site map live in Plus —
the site is small enough to see in 360° from the middle, so the map is a static zoomable
plan, not an interactive tab.

**Programme layout: option B2.** One chronological list per day. No calendar column and no
"now" line. Each row carries its state as a pill (`dans N min` / `en cours` / `se termine`)
and, when live, a raised bar with a progress fill. Past rows dim.

Why B2 over the alternatives:
- State is text and colour, not position, so it survives large text sizes and screen readers.
- It costs zero horizontal width, unlike the right-hand time column (option C), which
  reserved 132px of a 354px screen and collapsed under accessibility text sizes.
- It keeps working under any sort order, unlike a time axis.

What B2 gives up: nothing shows that two later slots will collide — which turned out not to
be a loss, since clash warnings were dropped entirely.

**A row is a Happening on a day, not a Slot.** SUP Yoga runs 14:00, 16:00 and 18:00 on the
Saturday, and as three rows it read as three activities that happen to share a name, a Category, a
price and a photograph — scattered among everything programmed between them. One row now, with its
hours written on one line: *14:00 – 15:00 · 16:00 – 17:00 · 18:00 – 19:00*, and three marks on one
track.

The fiche was always right about this and the list was the odd one out. There is no screen for a
single Slot — tapping any of the three has always opened one page with a list of dates on it —
because an activity running three times has one description, one price and one photograph. The list
was making a distinction the rest of the app does not.

Only SUP Yoga qualifies in 2026. The rule is not written for it: an activity that runs in sessions is
the normal shape for an activity, and the previous behaviour was a bug waiting for the second one.

**Each hour keeps its own state, and the row takes the loudest of them.** *En cours* wins, because a
row with something happening on it right now has exactly one useful thing to say. **`terminé` appears
only when every hour is finished** — a row whose 14:00 is over and whose 18:00 has not started is not
over, and saying so hides the rest of the afternoon. That is the one thing about a merged row a
reader could call a lie, so it is the thing the tests pin down. Between them, the hour that has gone
dims on its own at the same alpha the whole row uses when it is finished: a row whose 14:00 has gone
is not a row that has gone.

**The Programme tab answers two questions, and a toggle is which one.** *Programme* is a list of
Slots and *Découvrir* is a list of Happenings — one entry per thing, no hour, no day. It is not a
deduplicated Programme; it is read off `Edition.happenings` rather than folded out of the Slots,
because an entry is the record the Slots point at and deriving it from them would make the
Catalogue depend on a timetable it is defined to ignore.

The gap it closes is real and was not the one it looked like. The Programme is *unconditionally
scoped to one day* — 14, 15 and 15 rows across the three — so no screen in the app has ever shown
what the festival offers in one place, and a visitor who does not yet know what is there cannot ask
for it. That is a discovery problem, and search is the wrong instrument for it: a search field is
retrieval for somebody who already has a word.

**One selector row rather than a fifth tab, a screen behind the search icon, or a control of its
own.** The scopes share the tab, the Category chips and the fiche every item opens, and a second
navigation entry onto the same thirty Happenings is what "one place to browse a thing, one place to
see what you kept" forbids. Behind a magnifying glass it would have been a browse surface nobody
finds, which is the failure the Catalogue exists to fix.

*Reversed twice on the way to one row.* It shipped first as a Material segmented control above the
day chips, spaced away from them so it would not read as a third filter. That was wrong on the
premise — the day chips are exclusive too, and nobody reads *Vendredi · Samedi · Dimanche* as three
independent switches — and it paid for the distinction in the wrong currency: 40dp against a 32dp
chip, fully rounded ends against an 8dp corner, at the top of the chrome. Redrawn as a chip row it
was still a row, which left three rows and an axis above the first of fifteen. Merging it into the
day chips cost nothing, because the split was never real: every chip there answers *what am I
looking at*, and three of the answers happen to be days.

**An icon pair in the top app bar was refused, and a FAB with it.** Icon-only removes the reason the
Catalogue exists — the visitor it is for does not know there is a second list, so there is nothing
for a glyph to remind them of, and two words are the whole discovery affordance. The bar is also the
shell's, read once for four tabs so the two strings on it cannot drift; a per-tab action means
hoisting one store into the shell and three tabs carrying an empty slot, which is the hero icon slot
argument again. A FAB does not save the space either, it covers it — over the bottom of a scrolling
list, beside the nav bar — and one control with one label can only ever show one of the states,
so the first press is a guess.

**Tous exists because a day was never the only unit.** The Programme was unconditionally scoped to
one day, so the app could show 14, 15 or 15 rows and never the weekend. It is the default in every
phase but LIVE: off season, the week before and the weeks after are all read from a sofa across all
three days, and APPROACHING is the one that matters, because it is the only time anyone builds a
Plan and nobody builds one a day at a time. LIVE is the exception — on site the question is "what is
on now", which is about the day you are standing in, taken off the FestivalDay window so 01:00 on
the Saturday is still Friday and 04:00 lands on the day about to open.

**Each day keeps its own axis, and that is what makes the sticky header load-bearing.** A span is a
fact about one day: Friday runs 16:00–02:00 and Sunday 12:00–22:00, so a single reading in the
chrome could only be right about one of the three, and a scale that is wrong about the bars under it
does not fail to answer the question — it answers it wrongly. Pinning the header keeps the reading
on screen belonging to the rows on screen. It wears the chrome's blue so it reads as the toolbar
briefly growing a line rather than as a card wedged into the list, and because [SlotScaleRow]'s ink
is the one that blue carries.

**The card is not the row minus the time.** A Programme row carries the name, the Category, the
price, the hours, a live-state pill and a bar; it has no room for the photograph, the genres or the
description, and those live nowhere but inside a fiche you have to have already chosen to open.
That is what earns the card its height — measured at +32% over a row, and spent on facts the row
cannot carry rather than on the same ones set larger. The genres band is absent rather than empty,
which is most of the Activities.

**No hours on the card, and no heart.** Both belong to a Slot, and a Catalogue entry is a
Happening. Writing *ven · sam · dim* on it was considered and dropped: it is a timetable fact on
the one view defined by not being a timetable, and the fiche one tap away lists every date with a
heart against each — which is where a decision about a date belongs. The cost is real and accepted:
during the phase the Catalogue opens in, the browse surface is the one you cannot save from.

**The opening view follows the Phase, and is decided once.** ANNOUNCED opens on the Catalogue —
the week the programme drops, nobody has read the bill and hours are noise on top of a list nobody
can parse yet. APPROACHING and LIVE open on the timetable, because by then the question is "what am
I doing on the Saturday" and then "what is on now". After the first bundle the choice is the
visitor's and nothing takes it back: ANNOUNCED becomes APPROACHING at midnight on J-7, and somebody
reading the Catalogue at 23:59 must not have the screen pulled out from under them. Same shape as
`TabNavigator.selectStart`, same reason.

**Mon Yadlo does not merge, and that asymmetry is deliberate.** The Programme is a list you choose
*from*, so three chances at one activity is one choice. A Plan is a list of what you are doing, in
order, and two hours of yoga you kept are two appointments — collapsing them would take the
chronology out of the one screen whose whole structure is chronology.

**The bar spans the day, and it is on every row — finished ones included.** Each row's segment
sits where its Slot falls on the day's span, so the list carries the shape of the day as well
as the state of each item: what overlaps what, how much of the afternoon a seven-hour activity
covers, whether the evening is empty. While a Slot is running the segment lifts off the track
and fills as it goes, which is where *"on voit qu'il reste vingt minutes de Thalassothérapie
sans lire un chiffre"* comes from.

This was briefly built the other way — a full-width bar showing only the Slot's own progress,
drawn only while running — on the reasoning that a per-row time axis is the horizontal width
B2 exists to give back to the text. That was wrong twice over. The bar costs no width, because
it is a full-width row of its own under the text rather than a column beside it; and dropping
it once a Slot ends takes the shape of the day with it, exactly when most of the list is past
and reading the day is the whole job. The day's span is written once above the list as three
readings, never per row — that repetition *is* the right-hand time column, and it is still out.

The span is the FestivalDay's opening hours widened to cover anything programmed outside them:
the beach at Préverenges is public, so the morning yoga runs from 10:00 on a day the site opens
at 12:00 and still has to sit on the axis. It is measured over every Slot of the day rather
than the filtered ones, so tapping a chip never rescales what two rows are being compared on.

**The live-state pills use two warm ramps, not the prototype's amber-then-coral and not the one
ramp that replaced it.** Green fills `en cours`, amber outlines `dans 45 min`, orange-red fills
`se termine`, and `terminé` is plain tertiary text on a dimmed row — four states over three
colour roles plus three treatments, so they stay apart for a reader who cannot separate one hue
from another.

The single-ramp version argued that the two warm pills were the same fact at two distances. They
are not. `dans 45 min` is a caution — nothing is being lost yet, and the pill is telling you how
long you have — and amber is the colour of a caution. `se termine` is the last chance to walk
over there, the only pill on the screen asking for something to be *done*, and in amber it read
as more of the first rather than as a different thing.

The coral was refused once on the grounds that it had never been part of the
perceptual-separation measurement the five Category hues were chosen by. That was a reason to
measure it, not to refuse it, and it has been: 112 and 119 from `live`, the pair that appears on
adjacent rows, and 54 to 59 from amber, the pair that now has to be told apart at a glance. 24
and 18 from `musique` is the closest it comes to anything, which is why the pill is still
written out in words.

`urgent` is the one role in the theme that does not swap ends between light and dark, and the one
that carries dark ink in both. A signal is not a ground — it is not read *against* the theme, it
interrupts it — and the vivid step is the only one on the ramp that both looks urgent and holds
text. Its own boundary against a light page is 2.5:1, under the non-text floor, accepted for the
same reason an `enfants` chip's amber fill is: the pill is a word on a colour rather than a
control, and the word inside it measures 6.4:1.

**Sorting is dropped from v1.** The prototype offers Heure / A–Z / Prix; § Open below already
called it a candidate for deletion, and building it would have meant a permanent row of
chrome plus a price-extraction rule, for two orders that both break the meaning of the bar.
Time is the only order the screen is for. It comes back if the list is used with real content
and someone reaches for it.

B2 was re-tested against a card version (option B3) carrying identical data, logic and
state chips. Cards cost **+32%** vertical space on the Saturday — 1502px against 1139px,
88px per card against 72px per row — and removing the thumbnail changed that by a single
point, because the cost is the card itself: padding, border, and the gap between cards. The
thumbnail was never paying for it, and two thirds of the entries are activities with no
photograph to put there anyway.

**Lists compare, cards separate.** That is the reason B2 wins here and the rule worth
carrying forward. A card draws a boundary so an item reads as its own object; the Programme
exists to weigh slots *against each other*, which wants the opposite — a shared left edge,
a common baseline, as little furniture between neighbours as possible.

**Past items are never collapsed.** They dim and stay. By 21:00 on the Saturday that is 7 of
10 rows in Mon Yadlo and most of the Programme, which is accepted: seeing what has already
happened is part of reading a festival day, and a collapsing band would hide it behind a tap
exactly when someone is working out where they are.

**Wishlist granularity is the Stand, not the menu item.** You save "the taco truck", never
"the vegan burger". Menus and prices live on the Stand's detail screen, where "what do they
serve and what does it cost" is the biggest single gap versus the website. Item-level
favouriting would multiply the authoring burden — already the project's binding constraint —
for a precision nobody needs at a site with seven stands you can see from the middle. It
stays additive if it is ever wanted.

**Booking renders as a single inline row.** One bookable item today (the Silent Party), very
likely one in 2027. Modelled generically as a `booking` field on any Happening; the block
hides at zero and grows into a list only if there is ever more than one.

**Mon Yadlo layout: the rail variant, with rows.** A date pinned to the left while its items
scroll past, the time written once as a range, and the same row vocabulary as Programme. It
came from testing three shapes: rows with sticky day headers (829px), rail with rows
(882px), rail with cards (964px). The rail costs about 6% more scrolling than plain rows and
buys a permanently visible date; the cards cost 16% and bought spacing. The Wishlist screen
uses rows for the same reason.

**Mon Yadlo's bars share one axis across the days, and the scale sits in the chrome.** *Reversed:
the rows carried no bar at all.* The argument against was that a bar places a Slot against the whole
day's span, which is a question about a day you are choosing from rather than one you have chosen.
Half right — you are not choosing any more, but on the Sunday afternoon "how much of this is left,
and what have I got after it" is exactly what a Plan is open for. It is the same argument the price
lost: the decision was made when the heart was tapped, and the coins in your pocket were not.

**The scale had to go in the chrome, and that is what forced the axis.** The rail takes the left of
every row here, so a scale drawn inside a day block would be inset past 96dp of date column and
would have to be repeated three times. In the chrome it is written once, on the app bar's own blue,
continuing it exactly as the Programme's filter block does. But a single scale above three days is
only telling the truth if every bar under it means the same thing — so every Slot is measured as a
*clock* reading counted from its own day's midnight, and the axis is the union of the three days'
windows: 12:00 to 03:00 for 2026.

Friday opens at 16:00, so its bars start a quarter of the way in. That is not wasted space, it is
the true statement that Friday starts later — and it is the thing this axis buys that a per-day one
cannot: three days you can compare at a glance, which is what someone reading a Plan on the Saturday
morning is actually doing.

**It is deliberately not the Programme's axis**, which is one day wide and starts at that day's own
first hour, so the same Slot sits at a different x on the two screens. That is the accepted cost. The
two answer different questions — the Programme compares Slots inside one day, a Plan compares the
days — and the alternative was three scales, or one scale that is wrong for two days out of three.

Each day's window is still measured over **every** Slot the edition programmes on it, never over the
ones that were saved, which is the Programme's rule unchanged. A Plan scaled to what you happened to
keep would move its own bars every time you saved something.

**The Wishlist tile is a hero, not a Plus row.** *Reversed.* On Plus that shape is right, because a
Plus row is one of eight and its leading icon is what tells them apart in a column. Here it is the
only thing above the timeline and it is the entire other half of the tab, so a row-shaped block made
the screen read as a list that happens to start with a link. The hero is what the app already uses
for "the answer, before the page that supports it" — and *À essayer · trois stands gardés* is that
shape, on the treatment Accueil gives the one block it wants you to tap.

The fork-and-knife goes with it, and that is the cost: it tied this to *Nourriture & boissons* in
Plus, which the words still do. A hero has no icon slot, and adding one for a single caller would
make every other hero in the app answer a question it does not have.

**The Wishlist tile sits above the timeline, not below it.** *À essayer* is the half you consult
standing on the site, hungry, and by the Sunday the half above it would be three days of finished
rows to scroll past first. Putting it first costs one tile of vertical space on the screen someone
opens to check what is next, which is the cheaper of the two.

**A day with nothing saved on it is absent, not empty.** Mon Yadlo is a recall of what someone
chose. Three day headers with one row under them says less about a festival than one header does,
and an empty Saturday reads as a screen that failed rather than as a Saturday left open.

**Mon Yadlo's rows carry no heart.** They open the fiche, which is where the heart already lives —
never two hearts for the same thing, and the row is the target on both screens. It costs one tap to
remove something instead of none, and it buys a screen with exactly one meaning: this is what you
kept. The two verbs stay symmetrical too, since a Stand has never had a heart anywhere but its own
fiche.

**The rail writes the date without its year.** `11.07`, under the day name. A Plan only ever holds
the edition on screen, so three rails repeating `2026` say nothing three times. The day name is
above it because it is what people think in — *le samedi* — and the numeric date below because that
is what a poster and a bus timetable are written in.

**The rail's date comes from the FestivalDay, never from the Slot on it.** A FestivalDay is a
window, so the Saturday's 01:00 set falls on the Sunday by the calendar. Deriving the rail from the
first Slot would be right on most days and would file that set under a day the festival never
programmed.

**Both empty states point outward.** No Plan yet points at the Programme; no Wishlist yet points at
Plus › Nourriture & boissons. Neither offers a `+`, which is § The `+` in the Wishlist resolved in
the only direction that keeps "one place to browse a thing, one place to see what you kept" true.

**Grid / timetable view.** Not in v1. Possibly worth it on tablet, out of scope for now.

**Plus.** An iOS-style grouped list. Everything lives here permanently; Home borrows
individual entries by phase, so the institutional and call-to-action material surfaces in
March rather than sitting unread in July.

*Sur place* — always available offline
- Plan du site
- Nourriture & boissons
- **Paiement — carte et TWINT uniquement, pas d'espèces.** The single most consequential
  practical fact on the whole site. Not to be buried.
- Accès & transports — bus 701 / 705, bus pyjama, parking, vélo, à pied
- Accessibilité — 2 places réservées près de l'entrée, parking vélo
- Horaires d'ouverture *(missing)*
- Objets trouvés · premiers secours · sécurité *(missing)*

*Le festival*
- L'histoire de Yadlo — depuis 2015, un groupe d'amis véliplanchistes
- Yadlo en chiffres — per-edition, from the frozen file
- Festival responsable — FestiPlus
- Partenaires — one screen: a short introduction, the list of clickable partners with
  logos, and "devenir partenaire" as a footer link rather than a sibling entry
- No ecology section

*S'impliquer* — also surfaced on Home by phase
- Devenir Hot'Staff · Soutenir · Newsletter · Instagram / Facebook / YouTube / TikTok · Contact

**A row exists because its section does.** Every entry on the tab is derived from what the content
currently carries — no stands, no *Nourriture & boissons*; no `paiement` block, no *Paiement*. That
is what let the whole tab ship while half the festival's practical information is still unwritten,
and it means a rolled-back publish costs a row rather than opening a screen with nothing on it. The
cost is that the tab's shape moves with the content, which is the right direction for it to move in.

**The four groups are a screen decision, not a domain one.** That they follow usage rather than the
website's menu — what serves you on site, what tells you about the festival, what asks something of
you, the app itself — is a design judgement made once, so it lives in the UiMapper. The domain only
answers what the content can support. This is why payment is the third row of the tab: nothing about
the data says so.

**Facts carry a mark and no colour.** The prototype drew ✓ in green and ✕ in magenta. The magenta is
`musique`, and a red-pink beside a kind dot is the collision § Open already flags for the accent;
the green would have had to borrow `live`, which means "this is happening while you read it". So
polarity is carried by the glyph and by the section header above it — *Accepté partout* against *Non
accepté* — which is also what story 74 asks for, since colour is then never the only thing saying it.

**Horaires is deduced, never authored.** A FestivalDay's `start`/`end` *are* the opening hours, and
the first and last Slot of a day give the programme window. So the screen needed no new content
field, shipped before the association published anything about times, and follows a set added
mid-festival on its own. It says out loud that some activities start before the site opens rather
than clamping them: the beach is public, the morning yoga is on it, and correcting the festival
about its own site would be worse than explaining it.

**~~Accessibilité assumes the absence.~~ Withdrawn.** The screen was built around saying that nothing
is published and handing over an address, on the reasoning that somebody to ask is the most useful
thing a page can offer when the data is missing. Shipped, it read as a page that existed to apologise
for itself — a visitor deciding whether a beach is navigable in a wheelchair opened it and was told
to write an email. The section, its DTO, its domain model, its use case and its screen are out; the
questions to ask the association stay in content/GAPS.md § 9, and the screen comes back when there
are facts to put on it rather than a shape waiting for them.

**One dietary filter, and a row that says how much of the truck it matched.** Marks used to sit at
two authored levels — a Stand's, meaning "all of it is", and its Items', meaning "this one is" —
and the filter matched on either while the row printed only the first. So a truck matched through
its single végé bokit showed nothing at all under its name, which is the moment the reader most
needed a word.

Coverage is derived from the menu instead: a mark every dish carries reads `100 % végan`, one only
some carry reads `options véganes`. Two authored levels could contradict each other and one content
edit could make the Stand's level false; reading the menu cannot be wrong about the menu. A dish
with no mark counts against every mark, because one forgotten drink is exactly the difference and
being wrong in that direction is the safe one.

The chip set is still derived from the listing rather than declared, so no chip is offered that
matches nothing. **One chip at a time**: two read as an intersection to whoever wrote them and a
union to whoever reads them, and on eight stands the difference is a scroll.

**One gabarit for the text pages.** *Festival responsable* and *Réseaux sociaux* are a title, some
prose and some links, and so is whatever the association publishes next — so they are one
parameterised screen rather than a folder of near-identical ones. It is deliberately not a layout
language: a section is a heading, a paragraph and some links, and an entry needing more has earned
its own screen. Horaires and Paiement both did.

**Two rows of the prototype are not built, and the reason is the same each time**: a row that
opens nothing is worse than no row. *Plan du site* has no content at all — only a parking PDF
exists. *Langue* would open a picker with one language in it. *Notifications* is now built. Two more are outside this pass rather than refused: *Éditions précédentes* needs the
on-demand third file, and *Effacer mes données* needs a repository capability that does not exist.

*L'application*
- Langue · Notifications · Effacer mes données
- À propos de cette app — unofficial, who built it, how to reach them. This is the screen a
  committee member will find, so it is the app's inbound channel, not a formality.
- Signaler une information incorrecte
- Confidentialité — required at store submission even for an app that sends nothing

**Language.** French and English, opening in the device locale and falling back to French.
A phone set to French opens in French; anything else opens in English. Two translation
directions, and they are separate concerns:

- **UI strings** are authored in English and translated to French. Yours to maintain.
- **Content** is authored in French and translated to English. The organisers' language,
  and the source of truth.

When an English content translation is missing, render the French — never a blank or a key.
Be realistic that bilingual content roughly doubles the authoring burden on an organisation
that struggles to keep one language current.

**The Wishlist saves Stands, never menu items.** You keep "Food truck tacos", not "tacos
végétariens CHF 14". The stand's own screen still lists its full menu with prices — that
content is the biggest single gap versus the website and gets authored either way — but the
saved unit is the place you walk to. Roughly seven stands to maintain instead of fifty
items, no stale prices mid-festival, and item-level remains purely additive if it is ever
wanted.

**No opening times on the Wishlist.** Whether stands close before the festival does is
unknown, so nothing is shown rather than something invented. "Ouvert maintenant" is the
best food query there is and it returns the day that data exists.

**One live state for every Slot.** `dans 2h` → `en cours` → `se termine · 30 min` →
`terminé`, written in words, never expressed as layout. A seven-hour open activity and a
two-hour DJ set read identically, and both warn the same way as they end.

**A debug-only time-travel clock, from the first screen that needs one.** The injected clock
makes the app testable; it does not make it *checkable* on a device, and almost everything on
screen is a function of an instant nobody can reach by waiting — the Phase and its whole
Accueil block stack, which day the Programme opens on, every live pill and progress bar, the
24-hour annonces window during LIVE. A panel that sets the clock turns eleven months of waiting
into a tap, and the day any of it is wrong is the one weekend nobody can fix it.

Two independent guards, not one: the panel does not draw unless the binary is a debug build,
and the clock refuses to move regardless. What survives into release is a null check per
reading, which is cheaper than keeping two clocks in step.

It also forced a small honest change. The tickers run at one minute, which is right for a
festival and useless for a control you are watching — so the clock carries a `jumps` signal
that a store collects alongside its ticker. A jump is not time passing. Nothing emits on it in
release, so the collectors are subscriptions that never fire.

**Countdowns only inside a one-hour window.** Beyond that the day header and the start time
already say everything; "dans 26h" is noise. It was four hours, which put `dans 4h` in a pill —
the loudest thing the row has — directly above the start time saying the same thing more
precisely, so most of a Saturday afternoon was shouting a fact nobody needed. Inside an hour
the number changes what you do next: it is the difference between finishing your drink and
leaving now. The published prototype counts down from thirty minutes, which is narrower still;
an hour is the point where "should I start walking" becomes a real question on a site you
cross in ten minutes. Written in whole minutes throughout, and never "dans 0 min" — under a
minute out it still has not started, and one is the smallest true thing to say.

**The `se termine` warning starts at twenty minutes**, from the prototype, and writes out how
long is left. Long enough to be worth walking for, short enough that it is not the state half
a one-hour set spends its life in.

**Two verbs: Plan and Wishlist.** Saving something with a moment you could miss puts it on
the timeline; saving a Stand puts it on a checklist. One heart in the UI — the app decides
the bucket from what was tapped. Mon Yadlo is a timeline plus a single full-width Wishlist
tile, which opens a screen grouped by category (nourriture, boissons, boutiques, jeux).

**Mon Yadlo never browses, it only recalls.** The Wishlist screen shows saved Stands and
nothing else — discovering a new stand happens in Plus › Nourriture & boissons, or through
search. Same rule as the LIVE Accueil: one place to browse a thing, one place to see what
you kept.

**`Attendance` is dropped from the model.** It tried four times to become a visible
distinction and lost every time, and its last remaining job — clash detection — went with
the decision below. Every Slot now behaves identically: one `en cours` label, reminders
before it starts, a warning before it ends. You can arrive an hour into a DJ set or two
hours into the Silent Party; "can you miss it" was never a real question here.

**No clash warnings.** Overlapping favourites raise nothing. Clash detection is a
big-festival feature — six stages, five hundred metres apart, a genuine either/or. At Yadlo
you can hear the Scène from the UNO table, and catching half of each is a normal evening
rather than a mistake the app should flag.

**What lands on the timeline.** Any Artist or Activity Slot with published hours. Stands
never appear on it, however long their opening hours — the bar being open from 12:00 to
02:00 is not a fourteen-hour appointment.

**Transport: dropped from v1.** Timetables stay in Plus as static content. Notes from the
discussion, for when it comes back:
- Direction is the only thing the app genuinely cannot infer — lines 701 and 705 both serve
  Préverenges, and Morges vs Lausanne changes everything. It must be user-supplied.
- Choosing an exact departure when saving does not work: at 22:00 nobody knows whether they
  are leaving at 01:00 or 03:00, and a reminder for the wrong bus is worse than none.
- The shape that survives both problems: pick a direction once, then the app lists tonight's
  remaining departures against the clock, last one flagged, one tap to arm a reminder,
  re-armable late.
- The only notification with real consequences is "last bus in 30 minutes".
- Do not call a live transport API. It would be a network request at 02:00 with 6000 phones
  on one cell tower. Static, cached times are worse data and a better product.
- Data cost is small: only evening and night departures matter, ~12 per direction per
  edition, transcribed by hand from the PDF timetables.

**Visual identity: the site's blue, with the discipline of a sail palette.**

```
Bandeau / marque   #74AEE0   the sky blue from yadlo.ch
Primaire           #14618F   emphasis, pills, active states
Encre              #12242F   on both white and the blue band

État d'un créneau — deux rôles, trois traitements
  en cours #00612D     bientôt / se termine #7F5900

Par nature d'activité — cinq, pas six
  musique  #DD3B7A     eau      #1B86C9     terre    #2FA35A
  enfants  #F5B000     silent   #8A4FD4
```

Wellness folds into `terre`: yoga and a UNO tournament are both things you do on land, and
the distinction was never load-bearing. It also made the palette measurably better — the
coral it removed was half of the closest pair.

Chosen against four alternatives on a measured basis rather than taste. The binding
constraint is six colours a person can tell apart at 8px, in both themes, in direct sun —
measured as the smallest perceptual gap (ΔE) between any two of the six:

| direction | ΔE clair | ΔE sombre |
|---|---|---|
| **Le bleu du site + voiles, 5 catégories** | **58.5** | **49.2** |
| Voiles de planche, 6 catégories | 53.6 | 49.4 |
| Le bleu du site + voiles, 6 catégories | 43.0 | 40.4 |
| Palette du site telle quelle | 23.2 | 13.0 |
| Plage délavée | 23.0 | 20.9 |
| Affiche sérigraphiée | 14.5 | 22.6 |

Taking the website's palette unchanged fails because the site is blue-heavy: water and the
Silent Party both end up blue, which collapses to ΔE 13 in dark mode. Giving water the blue
and sending everything else elsewhere recovers most of the separation of a full sail palette
while keeping the colour everyone already associates with the festival — and with the lake.
No vermillion: the only warm is a coral, used as an activity colour, never as a ground.

**Typography: a DIN-family grotesque.** The website already uses DIN Neuzeit Grotesk, so it
is the festival's existing voice on posters as well as on screen — the same "recognisably
Yadlo" argument the blue won on. That face is Wix-licensed and cannot be redistributed in an
app, so the app bundles an openly licensed DIN-adjacent family instead.

Chosen: **Barlow** (SIL OFL) — a grotesque with real width variants, which is what a festival
app wants: Barlow Semi Condensed for the wordmark, headings and screen titles, Barlow for
body and UI. Wide weight range, ships legally inside the app, and close enough to the poster
voice that the two read as one brand. `D-DIN` is the more literal DIN clone if the family
resemblance matters more than the weight range.

Consequence worth taking: **times move out of a monospace face and into the display face**
with tabular figures. DIN's numerals are a large part of why the style reads as it does, and
the mono in the mocks was making a festival app look like a dashboard.

**Dark text on light brand colour, everywhere.** The website sets white on `#74AEE0`, which
is 2.4:1 and under the minimum — part of why it reads washed out. Deep navy on the same blue
is 5.4:1. Same brand colour, legible, and a difference that can be explained rather than
apologised for.

**One fiche template for everything.** Artist, activity and stand share a single layout:
collapsing toolbar over a photo, category label, title, attribute tags, sections. Silent
Party is the same template plus the booking row; a ground activity is the water fiche with a
different colour; the children's corner is covered by the same shape.

- **Collapsing toolbar.** The hero photo carries the title; on scroll the title rises into
  the bar and **the category colour closes over the whole head**, bar and photograph
  together, from one number. Tinting only the bar left a colour strip sitting on an untinted
  picture, which is a bar over a photo rather than a head arriving at a colour.
  **Continuously, not at a threshold**, so a slow drag paints it on slowly and a fling lands
  on it. It starts a third of the way in — a photograph that dissolves the moment a thumb
  moves reads as fragile — and reaches full opacity with a fifth of the travel still to go,
  because the tail of a linear ramp is a picture faintly showing through something meant to
  be solid, which looks broken rather than gradual.
  **A rule in the category colour closes the hero at rest.** Sitting still at the top of a
  fiche, nothing said which category it was: the bar is transparent, the label is written in
  the scrim's ink, and the colour did not arrive until something was scrolled.
  The radial blob this originally specified as sitting *over* the photograph is now what
  stands in *for* it: over an image it was one more thing between the reader and the picture,
  and the fiches that have no picture are the ones that needed it.
- **The category is written, never only coloured** — `Musique`, `Sur l'eau`, `Sur terre`,
  `Enfants`, `Silent Party`, `Nourriture` — as the label above the title. Colour alone is
  not an accessible carrier of meaning.
- **Actions are round icon buttons in the bar**: share and map on every fiche, plus the
  heart on a stand.
- **Tags are attributes only.** Never the category (the label says it), never the venue
  (the date row and the map say it). Genres for an artist, several allowed. Price, age and
  level for an activity. Cuisine and offer for a stand — `Options végé`, never a bare
  `Végan`, which would read as "this whole truck is vegan".
- **Facts are not tappable and must not look it.** `✓` and `ⓘ` rows on the background. The
  card-with-chevron style is reserved for things that navigate; an outward arrow marks a
  link that leaves the app.

**The photograph arrived, and it brought two decisions with it.** Thirty-six of the 38 Happenings
carry an image, so the head of the fiche is the hero the template always described. There is no
`imageBaseUrl`: a src is a path relative to the content root, resolved once at the data boundary
against the address the bundle was fetched from.

**The fiche has one ground, and it is always a photograph.** *Supersedes the two-variant head.* For
one release the head was a hero image where the content had a picture and the Category's colour as a
radial blob where it did not, with the words at matching insets so the two would not read as
different screens. That was the right answer while sixteen fiches of 38 had a photograph. It stopped
being one at thirty-six: the variant became the exception a reader meets without warning, on the
fiche of the stand whose photo has not arrived — which is exactly the moment the app should look
finished rather than apologetic. Two heads that differ in height, in ground and in ink are two
screens however carefully the type is aligned.

**What fills the gap is a photograph of the site, bundled.** Not a grey rectangle, not a category
wash: `img_placeholder` is a picture of the festival, which is a true thing to say about anything
at it, and being bundled it is there on a first launch with no signal. It is the same 280dp under
the same scrim under the same veil, so nothing about the screen changes but which photograph is
behind the words. It serves a null url and a failed load alike — on a beach with one bar of signal
those are the same fact, and telling them apart would mean drawing a broken-image mark on a fiche
that is otherwise complete.

This makes the placeholder real rather than defensive. Two stands have no photograph today (GAPS
§ 7), and both were prepared and lost; the number is expected to reach zero, and the ground is not
built on the assumption that it does.

**Over a photograph the Category label is written in the scrim's ink, not in the Category's colour.**
This is the one place the rule "the Category is written *and* coloured" gives up the colour, and it
gives it up because the ground stopped being ours. The scrim's alpha was derived as the lowest at
which white clears 4.5:1 over a *white* photograph, which is the worst case an image can present; a
Category fill carries no such guarantee, because the five fills were measured against the app's own
grounds and never against a picture nobody has seen. `enfants` gold on a bright sky is the failure,
and it is not a hypothetical — the water and beach photographs are the bright ones. Nothing is lost
that mattered: the Category is still written out, and the toolbar takes its fill as the header goes.

**The Category colour is also the ground the image loads onto**, rather than a grey placeholder. A
fiche opened with no signal is then the same colour as the bar it collapses into, which is a page
that looks unfinished rather than one that looks broken.

**The round icon actions are not built yet either, and for different reasons.** Share needs a
platform share sheet and map needs a geo intent — two expect/actual pairs that belong with the
Partenaires screen's link-out work rather than smuggled into the first fiche. The heart needs the
Plan repository, which does not exist; the date row it will attach to does, and is already the row
the heart is specified to sit on. Only the back button ships in the bar today.

**The price is a section, not a tag** — a refinement of the tag rule above, which listed price
alongside age and level for an activity. A tag can hold `CHF 25`; it cannot hold `CHF 25 · CHF 15
moins de 16 ans · caution CHF 50`, which is what the Silent Party actually costs. Shortening it to
fit would either drop the concession — pricing a family out of something they can afford — or fold
in the deposit, which is the CHF 75 error § A deposit is never summed already rejects. Two places
to read a price is also how the two come to disagree. Age and level were never tags either: the
model made `suitability` prose, because the one activity that states a limit states two at once,
and prose belongs in *Bon à savoir*.

**A free activity says `Gratuit` in the Tarifs section rather than showing no section.** The
question "what does this cost" is asked of a free activity exactly as often as of a paid one, and a
missing section answers it with silence that reads as missing data.

**An Activity's booking page is not a link row.** Artists and stands get a *Liens* section; an
Activity's one outward address is its booking page, and that is an action attached to the price it
commits you to rather than a reference to browse. It renders inside *Tarifs*, directly under the
tiers. A booking that is required with no page still says so, as a fact with nothing to tap:
someone who turns up without a ticket has lost the evening, not a tap.

**A fiche's *Liens* are marks, not tiles — the footer's row, left-aligned.** *Reversed: they were a
column of full-width `YadloLinkTile`s.* Seventeen of the 38 Happenings publish links and DJ ALF
publishes five, which is five 64dp rows — more of the fiche than his description gets — to say five
things the glyphs say by themselves. Nobody needs the word "Instagram" written beside the Instagram
mark. Worse, the tile's whole job is its trailing mark saying *where the tap goes*, and five rows
of identical chevron is that mark promising a difference it is not making.

The row is the one Accueil and Plus already end on, with one parameter: **left-aligned here,
centred there.** Centred, the row reads as the end of a page, which is what it is under a tab. Inside
a fiche it is a section with a title over it, on a screen where every other line — the description,
the dates, the prices — sits on one left edge, and a centred row of icons halfway up that column
reads as a footer that has landed in the wrong place. It is nudged back 12dp so the *mark* lands on
that edge rather than the invisible 48dp target around it.

**`website` is one of the marks, drawn as a globe.** The content's own `link.type` set puts it
alongside `instagram` and `spotify`, and on a fiche they are the same offer: somewhere else this
artist exists. The globe is a weaker mark than a brand logo, which is right — it is the one
destination whose identity is the thing you tapped from. The ten ids are resolved by one function
shared with the footer, so the two rows cannot disagree about what Instagram looks like, and a
platform the app ships no mark for still renders, under its name.

**A partner's logo sits on white, in both themes.** These marks were drawn for print and for a white
website, and the app has no curated dark-theme set for them — it will not get one either, because
thirty-nine of them belong to thirty-nine companies who each own their own and none has supplied a
variant. A fixed white card is the single ground every logo in the bank is already correct against.

The consequence is that nothing on the card may take a theme role: a hairline in `borderSubtle` is
invisible on white in dark, and a name in `textPrimary` would be near-white text on a white card. The
card is a fixed ground, so its edge and its ink are fixed with it — `slate200` and `slate700`, which
is what the light theme puts on white anyway and clears 8.25:1. The edge exists for the *light*
theme, where the page behind is `Color.White` too and without it the cards would not read as objects.

A wall of white cards on a near-black page is the visible cost, and it is why the screen has a dark
preview. The alternative — tinting each logo to the theme — would be altering thirty-nine
trademarks, which is the one thing this screen may not do.

**A logo is normalised by area, not fitted to its cell.** `ContentScale.Fit` gives every logo the
largest size that stays inside the box, which sounds fair and is not: the bank runs from 0.83 (Volt-A,
all but square) to 6.38 (VSM, six times wider than tall), so Fit hands the square one the full height
of the cell and the wide one a sixth of it. In a three-column cell that is 3136 square points against
1214, and the square company reads as almost three times the sponsor — on the one screen in the app
drawn for someone other than the visitor.

So each logo is scaled to cover the same *area* instead. Normalising the cell to height 1 makes its
width and its area both its aspect ratio; a logo takes `box² / ratio` when it is wide enough to be
width-bound and `ratio` when it is height-bound; scaling by `s` scales area by `s²`, so the factor is
the square root of the ratio between the area wanted and the area Fit would give. The target is 0.35
of the cell, which is the one number tuned rather than derived — it is where the areas actually meet
across the bank without making every logo smaller than the card it sits in.

It only ever shrinks, and that is the honest limit: a logo wider than the cell cannot reach the
target area because there is nowhere to grow, so the factor is capped at 1 and VSM and Von Auw stay
lighter than the other thirty-seven. Growing them would mean a taller cell for every tier, which
spends a screen of scroll on two logos. The rule is applied as a draw-time `scale` rather than a
smaller layout box, so the size Coil is asked for never changes when the ratio becomes known.

**The top three tiers are drawn two across, the rest three.** The tier order is the hierarchy the
sponsors paid into, and this is that hierarchy said in the one other language a layout has: a
*Sponsor général* gets a card half again as wide and a third taller than a *Partenaire*. Counted from
the top rather than matched by tier id — the statement is "the top of the hierarchy gets more room",
not "these three slugs are special", so a tier that gets renamed or a fourth that gets added does not
silently lose or gain a column. The cell height follows the column count rather than being passed
beside it: a wider card in the same 72dp band is a letterbox, and every logo in the bank would end up
width-bound inside it.

**SVG is decoded, because seven of the thirty-nine logos are SVG.** Coil ships no decoder for it, so
without `coil-svg` those seven load as nothing at all — and which format a sponsor's logo arrives in
is the sponsor's decision, not one the app gets to make. A vector is also the right thing to be
given: these are drawn at two sizes across the grid and a logo is exactly the kind of mark that shows
its pixels when scaled.

**The live-state vocabulary moved to `common/content/presentation` when the fiche arrived.** It was
written inside the Programme's screen package with a note saying it would move up a layer for its
second caller and not before. The fiche is that caller: a visitor who taps `en cours` on the list
must not land on a screen that has gone quiet about it, so the fiche's date rows carry the same
pill, measured against the same tick. Mon Yadlo will be the third. The strings moved with it —
`slot_state_*` and `price_free` are named after what they describe rather than after the first
screen that happened to need them.

**The heart is attached to what you are saving. The heart is also the only target.** For a
Slot that is the mark on the date row; for a Stand, which has no dates, it is the button in the
bar. There is never more than one heart for the same thing on a screen, never a selection dialog,
and removing is the same heart tapped again.

*The whole row was the target, and that is reversed.* The argument for it was sound — people expect
to tap a row, and a fiche's date row has nowhere to navigate to — but a row that is tappable has to
look tappable, and what made it look so was a card that stayed lit under a kept Slot. That was the
part that read wrong. A card under a row is the language of *selected*, so the fiche looked as
though it had a current date row rather than a kept one, and the meaning the ground was carrying
was already carried by the mark at the end of the line. Removing it takes the ambiguity and the
inset with it: the date now starts on the same left edge as every other line on the screen.

The mark keeps its disc, filled with the accent when kept and a ring when not. It is now a control
rather than a badge, which is also why it names its own action — the row used to say what a tap
would do, and the row no longer takes one.

**One table for both buckets, with the bucket written into the row.** `SavedEntry(id, kind,
edition_id)` holds a Slot id under `SLOT` and a Happening id under `STAND`. The two id spaces could
have been told apart on read — a Slot id carries `2026:` and a Happening id does not — and that was
rejected. The bucket is decided at the moment of the tap, by which control was tapped; re-deriving
it afterwards by matching strings against whichever content list happens to answer is the clever way
of losing a fact the app already had. It also means a row whose Happening has disappeared from the
content still knows what it was.

**`edition_id` is stored, and nothing sweeps on it yet.** Storing it is not optional: a Slot id
carries its year and a Happening id does not, so a Stand saved without it can never be attributed
afterwards. Sweeping is a different question, and § Plan lifecycle below is still open on it —
clearing the previous edition's rows on the first launch after a new one publishes would foreclose
the `ENDED` recap that entry is leaning towards. Orphans cost nothing while they sit there: every
screen joins saved ids against the current edition, so a row that matches nothing is invisible.

**Nothing optimistic, anywhere.** A heart tap writes to the Plan and dispatches no Message. The
screens read a join over the content and the Plan, so the write comes back through the collector
they are already on, and a filled heart is always the repository answering rather than the UI
assuming it was obeyed. It costs one flow emission and removes the entire class of bug where the
screen and the storage disagree about what was kept.

**The heart is filled or outlined, and its colour belongs to whatever it sits on.** On a date row it
takes the accent when filled; in a Stand's toolbar it takes the bar's own ink, because by the time
that bar is collapsed it has taken the Category's fill and an accent rose would land on the
`musique` magenta — the collision § Open already flags for the accent. The two never appear
together, so this is one control with one meaning rather than two treatments of it.

**A Stand's menu has three levels.** `Menu → Group{name, items} → Item{name, price,
description?, marks?}`. Only name and price are required, and each item renders as up to
three independent rows — name with price, description, marks — so nothing shares a line with
the name and nothing can overflow into the price. An item with only a name and a price is a
complete item, which matters because that is the data most trucks will actually give.
Groups make drinks and combo menus expressible without a special case.

**A menu group is a section of the fiche, and there is no *Au menu* above them.** *Plats*,
*Boissons* and *Tartelettes* are headed exactly the way *Quand*, *Tarifs* and *Liens* are, because
that is what they are — peers, not sub-headings. Two levels of heading over a carte of fourteen
dishes was one more than it needs, and the outer one only ever said what the dishes underneath
already do.

**Tabs over the carte were built and then taken out, and the reason is worth keeping.** A row of
Uber-Eats-style tabs pinned under the toolbar — one per group, one for *Liens*, tap to scroll,
selection following the scroll — was the obvious answer to a fiche that is five groups long, and it
worked. It was removed the moment the glyphs landed, because the two changes were solving the same
problem and the cheaper one won: with the marks reduced to glyphs, a dish is two short lines instead
of four, and the longest carte in the 2026 content is no longer long enough for anyone to get lost
in. Tabs over content that fits in two or three screens are a control that exists to be noticed
rather than used, and they cost a pinned band of chrome on every food fiche to say so.

Reopen it only against a measurement, not a hunch: the trigger is a carte long enough that a reader
scrolls past a group heading and cannot find their way back — realistically a stand publishing
thirty-plus dishes, which none does today. The `HappeningMenuTabs` component and its scroll-sync
are in the history of the branch that removed them if that day comes.

Two things the tab work settled that are still true without it. **A second screen for food fiches**
is not wanted — the template's whole promise is that an Artist, an Activity and a Stand degrade the
same quiet way. And **collapsible groups** answer "show me less" when the question was "take me
there", while hiding the very thing a fiche is for, which is what the stand sells.

**A dish name is `titleMedium`, not `bodyLarge`.** At 15sp Normal over a 13sp Normal ingredient
line it was two points and a shade of grey away from its own description, in the same weight, which
left a carte with no edge to scan down. The title style puts a step of weight between them as well
as three points of size.

**The menu's source line is off the screen and still in the content.** Every group carried a
sentence saying the carte was reconstructed from the vendor's own and not confirmed by the festival.
True, and doing the opposite of its job: five groups of a menu ended in five copies of the same
forty words, which is how a reader learns to skip small grey text rather than how they learn a price
might be wrong. `source` and `provenance` stay on every group and every item in `edition.json`,
where the record belongs and where content/GAPS.md can point at it. If a single unmissable statement
is wanted later, it belongs once per fiche and not once per group.

**Dietary information is a glyph and the word beside it, never the glyph alone.** It was text only,
for good reasons that turned out to be half the picture: no legend to learn, it translates for the
English build, and it avoids symbols that mean "contains" in one country and "free from" in another.
All still true — which is why the word never leaves. But a carte of fourteen dishes is scanned
before it is read, and a line of small grey words is not scannable. The glyph is what is found; the
word is what makes it safe to act on.

The vocabulary is closed at six and lives in the app keyed by a content slug, the same arrangement
Category colour uses: which marks exist is a content decision, what they look like is a design one
made once against a measured palette.

*Refined on the carte, and only there.* Beside a dish the mark is the glyph alone; the words are
written once at the top of the fiche, over the whole stand — *100 % végan*, *options sans gluten*.
That legend is what makes the glyph below readable rather than a symbol to guess at, and it is
always complete, because a stand's own marks are derived from its dishes. What forced it was
layout: a dish carrying four marks spent two lines spelling them out under a name and a price that
took one, so on a carte of fourteen dishes the marks outweighed the food. Every other place a mark
appears — a stand card, the filter chips, the top of a fiche — still writes the word. And the word
has not actually left the dish: each glyph carries its own label as its content description, which
is the reverse of the tag row, where a description would make a screen reader say everything twice.

**A Stand is a photograph.** Both browse lists and *À essayer* drew a stand as three lines of text
with a chevron — the shape of a settings entry, asked to sell dinner. On *Créateurs*, which
publishes no menu and therefore no dietary line, it was two lines on an otherwise empty tile.
Every one of the eight Stands the edition declares has a picture and none of the three screens was
showing it.

One card now, in `common/content/presentation`, drawn by all three: the photograph, then the name
and what the stand sells, then — behind a rule, so it is skippable by anyone who does not need it —
what can be eaten there. The rule is the answer to "everything is smushed together": the wording did
not change, the single indent everything was stacked at did. The chevron went with the change,
because a picture already says the card is a place rather than a row.

The frame is three by two rather than the sixteen by nine a card like this usually takes. Every
photograph in the bank is four by three, so a 16:9 frame is a centre crop that throws away a quarter
of the height — which on the one portrait among the eight takes the top of the subject's head off.

*This reverses "rows, because a list you compare across wants rows" on the Wishlist.* That was
answering the wrong question. You compare across a Programme to choose what to do at four o'clock;
you open *À essayer* standing on the site, to find the stall you kept among forty you did not, which
is matching a picture to a thing in front of you. It also means a Stand looks the same on the screen
it was saved from and the screen it was saved to.

**Two columns, and they are staggered.** A full-width card is mostly photograph, and eight of them
is a scroll of roughly three screens to see a list that would nearly fit on one and a half. Two
columns halve that and turn the lists into something scanned at a glance rather than paged through,
which is what a browse list is for.

The stagger is not decoration, it is what makes the arrangement work. These cards are exactly as
tall as what they have to say — a stand with no dietary band is a band shorter than one with two,
and at half a phone's width an offering line wraps on some names and not others. A plain
`LazyVerticalGrid` measures a line's items together and aligns them to its top, so every short card
sat over a hole that lasted until the taller card beside it finished, and the holes were the most
visible thing on the screen. `LazyVerticalStaggeredGrid` gives each column its own cursor: the next
card starts where the one above it ends and the columns drift out of step, which is the absence of
the gap rather than a second problem.

The alternative was equalising — reserving a fixed number of lines for the name and the marks so
every card is the same height. That buys a ruled grid by putting blank space under every short card,
which is the same dead space moved inside the card where it cannot be blamed on the layout.

*À essayer* uses the same two columns rather than a shape of its own, with the Category headers
spanning the full width of one grid instead of each group holding a grid of its own. Separate grids
would each level their columns at the boundary and re-stagger from scratch; one grid with full-line
headers levels the columns exactly where a Category actually changes, which is the only place a
straight edge means anything.

**Sourcing and allergens are facts, not tags.** "Viande et légumes de producteurs vaudois"
and "Allergènes sur demande auprès du stand" belong in *Bon à savoir*. A festival truck will
not publish an allergen table but will answer if asked.

**All-day Slots do not exist. Every Slot is timed.** Chasse au trésor was the only case: its
poster names three days and deliberately gives no hours, because the clues sit around the site
for the whole festival. The alternative — `start: null`, a "toute la journée" group pinned
above the timeline, the convention every calendar app already uses — was rejected. It is the
more honest encoding, but honesty in the data was not free: an untimed Slot still needs a
deliberate answer for sorting, for what "15 minutes before" means, and for whether the live
pill says *en cours*, so the special case survives the modelling and lands in three places
instead of one.

Writing the day's opening hours into the content answers all three at once, keeps a single row
shape on the Programme, and removes an absent-time branch from every screen that formats a
time. What it gives up is real and worth naming: the treasure hunt now sorts to the top of
Friday as though it began at 16:00, and reads *en cours* at 01:30. Both are overstatements
rather than errors — the clues genuinely are out there — and they are confined to one Happening
out of thirty-eight.

The derivation is recorded rather than hidden. Those three Slots carry
`provenance: "unverified"`, which is the distinction the field exists to make: the days are
confirmed, the hours are inferred. Leaving them `confirmed` would have asserted that the
organisers published times they did not. `validate.js` rejects a null `start` or `end`, so the
invariant lives in the content pipeline and the app never has to defend against it.

**Accueil promotes a few Plus screens, and it is not a shortcut.** The objection to raise first is
that it saves one tap, since everything it points at already lives in Plus — and if that were all
it did, it would not be worth the room. It is not. Plus is a table of contents where sixteen rows
carry identical weight and nothing in it knows what month it is: *Devenir bénévole*, the thing the
association spends the spring recruiting for, sits at the same volume as *Politique de
confidentialité* all year round, and a visitor in November has no reason to open the tab at all. Accueil is the
only surface in the app that knows the Phase. What the block buys is that a thing is raised at the
moment it becomes actionable and drops away afterwards, which no arrangement of Plus can do.

**The count follows the phase rather than a fixed width — one row in ANNOUNCED, three in OFF_SEASON.**
This is the part that decides whether the block is worth having. Filling every phase to the same
width means promoting whatever is left over once the genuinely urgent items run out, and a
promotion surface full of padding *is* just a smaller Plus, which really would be worth less than
the tap. ANNOUNCED gets exactly one because the hero is the screen's whole job in that phase.
APPROACHING gets paiement first, since the payment rule is the only fact in the app that is
actionable exclusively before leaving the house. ENDED gets the newsletter alone: the Monday after
is the one moment someone has just decided they are coming back.

**LIVE promotes nothing, and this is the earlier decision standing rather than an omission.** The
plan du site and the stands were turned down here by name — both already live in Plus › Sur place —
and the app is meant to open on Programme during the festival, so a block promoting Plus screens
onto Accueil that weekend is aimed at a tab nobody is looking at. *Urgences* was considered and is
the one item never covered by that reasoning; it was left out anyway, on the same grounds. If
emergency access matters at 01:00 on the Saturday — it does — then Accueil is the wrong home for it
precisely because nobody is on Accueil then. It wants a surface that is reachable from anywhere,
which is a separate piece of work.

**Three of the phases' natural tiles have nowhere to go, and stayed out.** *Revivre l'édition
précédente* needs an archive screen; nothing reads `editions.json` yet. *Réservations* has no
booking anywhere in the published content — `silentparty@yadlo.ch` is a row in the contact router
and nothing more, so a tile would open a mail composer under a label promising a reservation. The
FAQ was floated for APPROACHING and left out on the evidence: it holds four questions, three of
which are about being on site already (free drinking water, the heat, alcohol-free drinks), so it
is not what someone consults at J-3.

**A promoted tile is gated on its section exactly as the matching Plus row is.** `HomeContent`
carries five availability fields it never draws, asked of the same `festival` the Plus overview
asks, so the two tabs cannot disagree about whether a screen is worth opening. The tiles reuse the
Plus tab's own label strings for the same reason: a tile and a row that open one screen have to
call it the same thing.

**It is drawn as the Plus tab's own card, sharing the component rather than matching it by eye.**
The first attempt made these compact tiles laid out across the width, reasoning that a block reading
like a shorter Plus invites being scanned past like one. Wrong trade: these rows *open Plus screens*,
so a reader who taps *Paiement* here and *Paiement* there has tapped one thing and must not be able
to tell the two apart. The row and the card moved to `app/design/component/` — a titled card of
full-width rows, generic over the caller's own row type so nothing is matched up by a string
afterwards — and both screens now draw the same object. Matching a style by eye is exactly how the
four hand-written app bars ended up agreeing on nothing.

What makes it Accueil's rather than Plus's is the heading, which belongs to the Phase — *Préparer sa
venue* over payment and transport says why those two and why now, where a neutral "accès rapide"
would say nothing — and the length, one to three rather than sixteen. It carries no subtitles: Plus
writes the little it knows under a row because that tab is a table of contents being scanned, while
here the heading has already said why the row is on the screen, and a second line under each would
turn three promoted rows back into a list to read through.

**Recruiting is promoted only once there is an edition to staff.** OFF_SEASON offers *Nous écrire*
and ANNOUNCED offers *Devenir bénévole*, which is the difference between the two long phases rather
than an inconsistency. Between editions there is nothing to volunteer *for* yet and the useful offer
is a way to reach the association during the months it can actually answer; once the programme
exists it is an edition that has to be staffed, and that is when the association is recruiting.

**A cold start opens every tab at its root, and the saved back stacks are for rotation only.**

Navigation 3 restores each tab's stack from saved state, which cannot tell a rotation from a process
Android killed while the app was in the background — and the two want opposite things. The selected
tab is *not* saved and goes back to the Phase's answer, so the half-restored result was the worst of
both: the visitor came back to Accueil, and the Plus tab underneath was still four screens deep on a
page they had left days earlier.

Android's own guidance is the other resolution — save the tab too, and make process death invisible.
It was rejected for a festival app whose screens are all one or two taps from a tab root: coming back
to a clean shell costs nothing to undo, and coming back somewhere you did not put yourself is
disorienting in a way no amount of correctness fixes. The signal is [TabNavigator.selectStart]'s
return value, since that object lives exactly as long as the process does.

**They push onto Accueil's stack, not into the Plus tab.** A tab switch would leave the reader on
Plus's root when they back out of *Paiement*, somewhere they never chose to be. Pushed, back returns
to Accueil — the same rule the fiche already follows when it is opened from two different tabs.

**A share is plain text, and the link in it is the festival's, not the app's.** The obvious thing to
want is a link that opens the app on the exact fiche that was shared. It is not reachable, and the
reason is the same one that killed the live Instagram feed rather than a technical one: Android App
Links need `assetlinks.json` and iOS Universal Links need `apple-app-site-association`, both served
from **the domain in the link**, and nobody here owns `yadlo.ch`.

The trap to avoid is a custom scheme — `yadlo://happening/dubside`. It resolves only on a phone that
already has the app, most messaging clients will not even make it tappable, and sharing is precisely
the case where the recipient does not have it. A custom scheme is for a notification tap, never for
something leaving the phone.

Serving the two files from the GitHub Pages host would work and is still wrong: it means texting
somebody `nicolaszurbuchen.github.io/yadlo/...`, which looks like nothing to do with the festival
and 404s for everyone without the app unless a web page per Happening is built to catch them. That
is building a website to support a share button.

So the message carries the name, the dates and `festival.website`. It is never broken, it is useful
to somebody who has never heard of this app — which is every recipient — and it points at the
festival rather than at an unofficial app. **Going official turns this on with no redesign**: the
share text does not change shape, that one URL gains a path, and two files land on the domain.

**`website` lives in the content, and it is the only field written for someone who does not have the
app.** In the binary it would be a festival-owned fact frozen into a release, which is the thing the
content pipeline exists to prevent — and it is exactly the field that has to change on the day the
link becomes an App Link.

**Shared is the Happening, not the Slot on screen.** A fiche lists every date a thing runs, and the
share carries up to three of them rather than whichever row was tapped — the same call story 8 makes
for a search result, where an activity running all weekend is one result with its dates rather than
three. A Stand has no dates and simply loses the line.

**Two surfaces, and the two that were cut are the interesting part.** The fiche, and *Devenir
bénévole* — which shares the association's own `ehro.app` recruitment address, so the thing being
forwarded is theirs and works for anyone. Sharing *the app itself* from Accueil was dropped: there is
no store listing, so the button would send yadlo.ch, which the recipient can already find. It becomes
real the day there is a Play Store URL. Sharing *l'histoire de Yadlo* was dropped too — there is no
public page for it, so it would forward a paragraph of prose. Both are buttons that exist because
they can rather than because anyone wants them.

### Notifications

**Local only, and hand-rolled rather than taken from a library.** The two real KMP candidates are
Alarmee and KMPNotifier. Neither solves the two parts that are actually hard — rescheduling after an
Android reboot, and the iOS cap on pending requests — so the reconciliation layer gets written
either way, and what a library would save is the two `actual` bodies. Alarmee's documentation also
contradicts itself about whether iOS *local* notifications need Firebase; pulling the Firebase SDK
into an app whose *Confidentialité* screen says it sends nothing is a documentation problem before
it is a technical one. The seam is five methods and the codebase already had three like it.

**The scheduler reads `WallClock`, and it is the only thing in the app that does not read
`AppClock`.** This is a real exception to the injected-clock rule, not a lapse. `AlarmManager` and
`UNUserNotificationCenter` take an absolute instant and compare it to wall time, so a reminder
planned from a simulated Saturday evening would be handed a July instant and fire in eleven months,
or never. `Clock.System` is still constructed exactly once, in `timeModule`; what changed is that
it now has two names, and the naming is what stops a call site getting the wrong one by accident.

The consequence is that **reminders cannot be checked by time travel**, which is how everything else
in this app is checked. Three layers replace it. The planner is a pure function taking `now` as a
parameter, so its whole contract — the lead time, the cap, unhearting, milestone instants — is host
tests at any date. The debug panel fires one in sixty seconds, which proves the pipe. And the real
end-to-end test is moving the *device* clock to 21:25 on the Friday before opening the app, which
makes both clocks agree and leaves the alarm genuinely five minutes out.

**Inexact alarms on Android, and the lead time is what pays for it.** `setExactAndAllowWhileIdle`
needs `SCHEDULE_EXACT_ALARM`, denied by default since Android 14 and returnable only through a
system settings screen; `USE_EXACT_ALARM` is restricted by Play policy to alarm clocks and
calendars and reviewed at submission. A festival companion arguing it is a calendar app is a fight
to lose at the worst possible moment. `setAndAllowWhileIdle` needs no permission, fires in Doze, and
drifts a few minutes — invisible inside a thirty-minute warning. Shortening the lead would quietly
break that, which is why the constant carries the reasoning.

For the same reason the notification says *ça commence à 22:00* rather than *dans 30 minutes*: a
clock time is true whenever it arrives, and a countdown baked in at schedule time is a promise the
scheduler is not making.

**Thirty minutes, not sixty, and no picker.** The site is one beach you can cross in four minutes.
The reminder is not travel time, it is not losing track of something while you are at the bar. A
minutes picker is the kind of knob that reads as configurability and never gets moved.

**Replace, never reconcile.** Every pass cancels everything and schedules the desired set. Asking
each platform what it holds is awkward — iOS answers asynchronously, Android does not answer at all
— and every caller would then need diffing logic that has to be right about cancellation. At tens
of items on app start the cost is not measurable, and it collapses four questions into one: an
unhearted Slot, a Slot the content dropped, a set whose hours moved and a reminder whose moment has
passed are all just absent from the next list.

Android cannot be asked which alarms it holds, so cancelling requires remembering; that is the only
reason a preferences file exists in `Notifier.android.kt`, and it holds ids and nothing else.

**A cap of sixty, protecting against iOS.** iOS drops local notification requests past 64 silently —
no error, no log. The 2026 Edition has 48 Slots, so hearting every single one already fits and the
cap never bites; it is there because the count is content-driven and nothing else in the app would
notice an Edition that doubled.

**Permission is asked at the first heart tap, never at launch.** Both platforms treat a refusal as
final, so there is one attempt to spend. Asked at startup it is an app the visitor has not used yet
asking to interrupt them; asked at the first heart it is a prompt about the thing they just did. The
cost is that somebody who saves nothing never gets the milestone notifications, which is the right
trade — they are the ones who wanted them least.

**Three milestones, and the LIVE one deliberately does not fire at its own Phase boundary.** LIVE
begins at midnight on the Friday. A phone buzzing at 00:00 to say the festival is today wakes
somebody the night before it starts, so that one fires at 10:00 instead. APPROACHING and ENDED sit
on their boundaries, which are already J-7 and 11:00 the morning after — both chosen to be humane
hours for other reasons.

ANNOUNCED is missing from the list and is the one worth wanting. It is the moment a dormant app
would most like to speak and precisely the one it cannot reach: the phone has to learn the dates
exist while the app is closed. A daily background poll was considered and works on Android; on iOS,
background refresh is granted on predicted engagement, so an app unopened for eight months is
deprioritised to approximately never — it fails exactly for the user it targets. That leaves push,
which is refused above on content grounds rather than technical ones.

**Story 17 — the warning as a Slot ends — is dropped, not deferred.** § The Plan already argues that
at a site you can cross in two minutes, catching half of two overlapping things is a normal evening
rather than a mistake the app should flag. A notification telling somebody that what they are
currently enjoying is nearly over is the same idea wearing a worse costume.

**Staleness is the Slot's end, never the reminder's own instant.** A notification delivered two
minutes ago about a set starting in twenty-eight is the opposite of stale. Android is told a timeout
when the alarm is scheduled and dismisses it itself; iOS has no equivalent and sweeps on next
launch, which is the only moment it can. Neither is worth more engineering than that — a stale
reminder is one swipe.

**The switch on *Plus › Notifications* is one switch, and it shows the conjunction of two answers.**

Categories were considered and refused. The app sends two sorts of notification — the reminder
before a saved Slot, and the three that mark the festival year — and somebody who wants to be told a
set is starting is not a different person from somebody who wants to be told the festival is
tomorrow. A settings screen earns a second row when the two rows would be answered differently, and
these would not.

The harder question is that **two switches govern a notification and the app owns only one of them**.
The other is the OS permission, which Yadlo can ask for and never set, which the visitor can revoke
from outside the app, and which both platforms treat as final once refused. A switch showing only
the stored preference would sit at *on* while the system dropped everything the app posted, which is
a control that lies. So it reads on only when both halves agree, and when they disagree in the one
direction the visitor cannot fix from here — wanted in the app, refused by the phone — the screen
says so and offers the system settings. That button is why a fourth file exists in
`infra/notification/`: without it the primary control on a screen called *Notifications* can be dead
with no recourse, which is a bug rather than a simplification.

Turning the switch on **writes the preference before asking for the permission, and keeps it written
whether or not the prompt succeeds.** What is recorded is that the visitor asked for reminders, which
stays true when the operating system refuses — and it is what makes the switch come on by itself if
they later allow notifications in system settings and come back.

**Granting the permission publishes an event, because the permission is the one input to scheduling
that cannot be observed.** The Plan is a database, the content is a StateFlow, the visitor's own
switch is a table — all three say when they change. The operating system says nothing; it can only be
asked, at a moment somebody chose. So every request goes through one wrapper that emits the answer,
and the shell reschedules off that.

It is a wrapper rather than a line at each call site because the version with three call sites was
already wrong once: the switch here granted the permission and told nothing, and the reminders were
scheduled only because the Android system dialog happens to pause the activity underneath it. On iOS
the alert is drawn in-app, nothing pauses, and nothing would have been scheduled until the next
foregrounding. A rule a fourth call site cannot fail to follow is worth more than three that
remembered.

**An unwritten store means on.** Reminders were already being scheduled for everybody who had granted
the permission before this screen existed, so the default cannot be off without turning the feature
off for its existing users on an upgrade. Only an explicit tap writes.

**One table for one fact, not a settings store.** `ReminderSetting` is a single row under
`common/reminder/`, keyed by a constant. A generic key/value store is the shape to reach for when
there is a second preference rather than before. *Langue* is the one row that would be a second, and
it is waiting on a second language rather than on somewhere to put the answer. *Effacer mes données*
is not a preference at all — it is an action wanting a repository that can delete, and the screen for
it stores nothing: it counts what is saved and offers to remove it.

**A custom scheme is right here and wrong for a share, which is the same rule read twice.** §
*A share is plain text* refused `yadlo://` because a share is precisely the case where the recipient
does not have the app. A notification payload never leaves the device, so the objection does not
apply.

### Search

**One corpus, and the entry points are what make that credible.** A field on Accueil; a magnifier in
the shell toolbar on the other three tabs. Both in every Phase, and both opening the same screen
over the same index. The alternative — a search bar per screen, scoped to that screen — was
turned down on the shape of its failure: it fails *silently*. Type `twint` into a Programme-scoped
search, get nothing, and the conclusion is that the app does not know what TWINT is. A global search
reached from the Programme fails in the other direction, by returning a *Sur place* heading the
reader did not expect, which teaches the model in under a second and loses nothing. Those are not
the same size of mistake.

**No icon on Accueil, and no field anywhere else.** They are two doors to the same room, and on one
screen side by side they are the duplication rule. The block wins on Accueil because it is the one
that *teaches* the app has a search; the icon covers the three tabs with no room for a field. The
sequencing works out: the first encounter is overwhelmingly the block, so the magnifier is met as
*that thing again* rather than cold.

**Accueil-only was rejected on the LIVE case.** During LIVE the app opens on Programme and Accueil is
deliberately thin — so a search reachable only from Accueil is a tab switch plus a scroll plus a tap
away at exactly the moment it is worth the most: on the site, one hand, sun on the screen.

**The block leads every stack, including ENDED.** It is a control rather than content, and the
magnifier it teaches never moves either; a field that slid down the page in two Phases out of five
would be one the reader has to hunt for. The cost is one 48dp row above the thank-you on the Monday
after — noted, and judged smaller than a search affordance that is only sometimes where it was.

**The bar can carry it because the bar is not a tab’s.** `YadloTopAppBar` at a tab root shows the
festival’s name and the edition dates, identically on all four tabs — never a tab title. A magnifier
beside *Yadlo · 10–12 juillet* inherits that. The same icon in the Programme’s own chip row would
read as scoped, and there it should.

**The scope is stated by the placeholder and demonstrated by the groups.** *Rechercher dans tout le
festival*, not *Rechercher* — five words in the one place the reader is certainly looking. Then the
results come back under *Programme*, *Sur place* and *Infos pratiques*, so a query typed from one tab
that answers with another tab’s heading has shown its own reach without a word of explanation.

**Search never inherits the screen’s filters.** A search opened from the Programme with Samedi and
*musique* selected returns the whole festival. Inheriting them would make the scope genuinely the
current screen with nothing on the results page saying so — the one version of this that a reader
cannot recover from by reading the answer.

**Every result is something with a screen.** Story 8 settles it for the timetable — a result is a
thing, not an occurrence — and a dish is the same case one level down. `tofu` returns *Végémania*
with *Ragoût de tofu* written underneath as the reason it matched. A FAQ question is titled in the
association’s own words and opens the FAQ page. Nothing in the results dead-ends.

**Practical information is indexed as rows plus aliases, not as prose.** Fifteen screens, each with a
hand-written keyword list — `twint` → Paiement, `parking` → Comment venir. Indexing the paragraphs
inside those screens would produce a result labelled *Paiement* for a word buried three notes down,
with no way to show why and nowhere to scroll the reader to. The aliases live on the domain enum and
are never displayed; the titles are the Plus tab’s own strings, so a screen keeps one name.

**Dietary marks are a filter, not a query.** `vegan`, `sans-gluten` and the other four are a closed
set the content already models per dish, and someone looking for vegan food wants *every* vegan dish
rather than a ranked guess. That belongs on the stands list as chips. Not built.

**No LLM, and it is not close.** There is no backend — the content is static JSON on a CDN — so a
model means a server, a key and a per-query bill on an unofficial app with no revenue. It also means
a network round-trip at the moment it is least available, which is the argument that already refuses
live transport API calls. The corpus is about 120 strings, which is far smaller than the thing a
model is good at. And the deciding one is standing: a generative answer invents sentences about
somebody else’s festival, which is exactly what killed remote push. The errors are asymmetric — a
substring search that misses is annoying, a model that confidently says the bar closes at 01:00 sends
someone home an hour early.

**No index, no FTS, no debounce.** 38 Happenings, 62 dishes, four questions and fifteen topics — a
substring scan of it is work a phone does between two frames, and the whole edition file is 95 KB.
SQLDelight FTS would be slower to build than the search it replaces and would put a second copy of
the content somewhere it can go stale. A debounce would be latency the app chose to add.

**The real work is the accents, and it is a table rather than `expect`/`actual`.** `preverenges` has
to find *Préverenges*, and commonMain has no `Normalizer`. Android’s `java.text.Normalizer` and
iOS’s `CFStringTransform` would be two implementations to keep in agreement, neither testable on the
host JVM. French has about thirty accented letters, so `infra/text/` folds them with a positional
table and one commonMain function covers both platforms.

**The current Edition only.** Archives load on demand, so covering them would mean either fetching
every edition at launch — which wrecks the cold start the offline story rests on — or a search that
silently does not cover what it appears to. That is the scoped-corpus mistake again, wearing a date.
If an archive list ever needs narrowing, that is a filter on the screen you are standing on.

**A topic is offered only when its section is published**, the same rule the Plus row it opens
follows. A result that opens an empty page is worse than no result: the reader now believes the app
has nothing to say rather than that they asked the wrong question. *Notifications*, *Confidentialité*
and *À propos* are the app rather than the festival, so they are always there.

## Open

**The accent colour.** `#14618F` is the primary, not an accent — the terminology in earlier
notes was wrong. The site uses `#E3ADC8` as a true accent on page titles, and a punchier
version of it, around **`#E27BA6`**, is the candidate for a floating action button and for
titles. It carries the same rule as the header: white on it is 2.4:1 and unusable, navy
`#12242F` is 6.4:1. Open question is the collision with `musique` `#DD3B7A` — a pink accent
and a magenta kind-dot may read as the same signal. Either confine the accent to chrome that
never neighbours a kind dot, or move `musique` to the coral and `bien-être` to a teal, which
would also resolve the magenta/coral crowding that is currently the palette's closest pair.

**~~Sorting may be dropped.~~ Resolved: dropped.** See § Programme layout above. Revisit once
the list has been used with real content.

**~~Search granularity.~~ Resolved: built.** See § Search above. It runs across the whole edition and
returns Happenings rather than Slots, and it does cover practical information — the half still open
here, and as cheap as this entry guessed. The Wishlist `+` never arrived: Mon Yadlo is recall-only,
so its empty state points at the stands list rather than at a search of its own.

**Plan lifecycle.** SlotIds must be Edition-qualified (`2026:dubside-sat`) so a reused id
cannot resurrect last year's favourites into this year's Plan. Local storage survives app
updates indefinitely, so clearing is code that has to be written — it does not happen on
its own. Leaning towards: when a new Edition publishes, show the previous Plan once as an
`ENDED`-phase recap ("ton Yadlo 2026 — 7 concerts, 4 activités"), then clear it. Undecided
whether the recap is worth building at all.


**The `+` in the Wishlist.** Resolved by making Mon Yadlo recall-only: there is no add-flow,
so the empty state points at Plus › Nourriture & boissons or at search. Left here in case
the empty state proves too passive in practice.

**Recruiting is treated as always open.** *Devenir Hot'Staff* was built with two states — a
campaign, and a campaign that has closed — on the reasoning that recruiting is seasonal and
a row silently disappearing in August is worse than a page saying so. Decided for now that
the app does not model the closed case: the row is still derived from the `simpliquer`
block, so removing the block removes the row, and the screen itself assumes there is an
offer to show. What is unresolved is what should happen in the weeks between one edition
closing its applications and the next opening them — whether the row stays with a "revenez
en janvier" page, disappears, or the content gains a window with dates on it. The third is
the only version that survives an association that forgets to edit anything, which is an
argument for it and a reason it has not been built.

## Content that does not exist yet

Everything here is missing from the website and from any structured source. It is the
real dependency of this project, and most of it needs to be asked for.

**There is a direct line to a founder.** Before reconstructing any of this from public
sources, ask. The stand list, the menus and the opening hours are not lost — someone typed
them into a spreadsheet or a group chat already. "Can you send me last year's stand list,
I'm building something" is a far easier first message than a pitch, and it makes the
reconstruction work below largely unnecessary.

**The festival's own opening hours.** The site never states what time the festival opens or
closes on any day. `FestivalDay` windows are currently guesses. This is the most basic
missing fact and it underpins the whole timeline.

**Stand data of every kind.** No names, no menus, no prices, for food, drink, clothing or
game stands. Every Wishlist feature is blocked on someone authoring this.

**Activity hours.** "Chasse au trésor" publishes none, and now carries its day's opening hours
as `unverified` under the no-all-day rule above. If the association ever states real hours, the
fix is a content edit and nothing in the app changes.

**Artist detail.** The programme gives a name, a time, a stage and sometimes a genre. No
biographies, no links, no photos beyond page decoration.

**A site plan.** Only a parking map exists. The booth map has to be drawn.
