# Decisions

Running record for the Yadlo companion app. Vocabulary lives in [CONTEXT.md](./CONTEXT.md);
this file is what we chose and what is still open.

Context: the 2026 edition ran 10–12 July 2026. Next edition ~July 2027, lineup expected
around May 2027. Roughly eleven months of runway, with no live event to test against.

## Settled

**Status.** Unofficial / portfolio build now, aiming to become the official app for 2027.
Consequence: no broadcast push and no volunteer features until the association is on board.

**Comms.** An in-app feed of annonces from `announcements.json`, and **notifications deferred past
v1 — local ones included.** There is no notification code on either platform, no `expect`/`actual`
seam and no `POST_NOTIFICATIONS` permission, so the `Notifier` interface is the whole feature
rather than a wrapper over something that exists.

Deferring it costs exactly two user stories, 16 and 17 — the reminder before a saved Slot and the
warning as one ends. Everything else that looks time-driven reads the injected clock and recomputes
on the ticker, so Phase, the live-state pills, the countdowns and Mon Yadlo are all unaffected. That
is a small enough blast radius to be worth taking, given the feature is two platform
implementations and a permission prompt.

When it lands it is local only, with remote push behind the same interface so FCM can drop in
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

**Recompute while running, not only at launch.** At 08:55 someone is in APPROACHING; at
09:00 they should be in LIVE without killing the app. The same ticker drives the `en cours`
pills and the "dans 15 min" countdowns. It is also why the clock must be settable from the
first commit — the LIVE phase is otherwise untestable until July 2027.

**Compare in the festival's timezone.** FestivalDay windows are instants in `Europe/Zurich`.
Compare absolute instants and format in Zurich time, never against the device's wall clock —
this is the kind of thing that works all year and breaks on the one weekend that matters.

**Accueil, block by block.**

**Global search appears only when there is a programme to search** — ANNOUNCED, APPROACHING
and LIVE. Between editions there is nothing to find but last year's archive, and the
countdown deserves the top of the screen more than an empty search box does. One search,
one results screen, reached from both Accueil and Programme — two doors, one
implementation. Over roughly sixty items it is an in-memory filter, and it answers
"SUP yoga", "twint" and "parking" equally well.

*OFF_SEASON* — countdown · annonces · revivre l'édition précédente · s'impliquer ·
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

*LIVE* — recherche · annonces du jour · lien Instagram, plus the quiet-hours block when
nothing is running. Deliberately thin, because during LIVE **the app opens on Programme,
not Accueil.** No plan du site and no stands here: both already live in Plus › Sur place,
and duplicating them is the thing we are avoiding.

**No live Instagram feed in v1.** The website embeds one and it is the obvious way to make
LIVE's Accueil feel alive, but it is not reachable unofficially: Instagram's Basic Display
API was shut down at the end of 2024, and the Graph API needs a Business account plus a Meta
app authorised *by the account owner*. Scraping breaks constantly and violates their terms,
and republishing photos of identifiable people from an app that is not theirs is its own
problem. So: a link-out card for now, and a real feed becomes another thing that going
official unlocks.

*ENDED* — merci · Yadlo en chiffres · annonces · archives · newsletter · réseaux sociaux

**The default tab follows the phase.** Accueil for 361 days; Programme, scrolled to now, for
the four days of the festival.

A home tab that only summarises other tabs is a tab that should not exist. Strip everything
from the LIVE Accueil that duplicates another tab and only announcements remain — which is
the correct answer, not a gap to fill. Accueil is substantial in OFF_SEASON, ANNOUNCED and
APPROACHING, and thin exactly during the days that matter. Opening on Programme resolves it:
no "Maintenant" block, because you are already looking at now; no "Ensuite" block with an
unanswerable "how many do we show", because Mon Yadlo is one tap away; and no reappearance
of the drop-in versus timed split that B2 exists to avoid.

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

**Three rows of the prototype are not built, and the reason is the same each time**: a row that
opens nothing is worse than no row. *Plan du site* has no content at all — only a parking PDF
exists. *Langue* would open a picker with one language in it. *Notifications* are a settled deferral
past v1. Two more are outside this pass rather than refused: *Éditions précédentes* needs the
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

**Search granularity.** Search runs across the whole programme, not one day, and returns
Happenings rather than Slots — so an activity running all three days is one result listing
its three slots, not three near-identical rows. The Wishlist `+` is the same search with
Activities filtered out. Still to settle: whether it also searches practical information
("bus", "twint", "parking"), which is nearly free given the corpus is a few dozen items.

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
