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
accessibilité, réservations — from J-7 and again on each festival morning. The payment rule
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

**The live-state pills use one amber ramp, not the prototype's amber-then-coral.** Green
fills `en cours`, amber outlines `dans 15 min` and fills `se termine`, and `terminé` is plain
tertiary text on a dimmed row — four states over two colour roles plus three treatments, so
they stay apart for a reader who cannot separate green from amber. The coral the prototype
reaches for was never part of the perceptual-separation measurement the five Category hues
were chosen by, and a red pill beside a `musique` magenta dot is the same collision § Open
already flags for the accent. The words carry the state either way.

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

**Countdowns only inside a four-hour window.** Beyond that the day header and the start time
already say everything; "dans 26h" is noise. The published prototype counts down from thirty
minutes; four hours is the later and wider decision, and the layout it was tested against is
unchanged by which one wins. Inside the window it is written in whole hours until the last
one, then in whole minutes, floored — at 3h50 the answer someone wants is "not for a while",
and rounding it up to four would cross back out of the window itself.

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
  the bar, the bar and the status bar take the category colour, and a veil closes over the
  image. The category colour arrives as a radial blob anchored bottom-right rather than a
  wash over the whole photograph.
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

**The fiche shipped without its photograph, on purpose.** The template above describes a
collapsing toolbar over a hero image, and it still does — but not one of the 38 Happenings in the
2026 content carries an image, and `imageBaseUrl` is `null` because hosting is not settled
(content/GAPS.md § Images). Building the image layer now would have meant an untested remote path
drawing nothing on every fiche in the app, against a Coil3 disk cache CLAUDE.md already flags as
unverified. What shipped is the half that has something to draw: the Category colour as a radial
blob anchored bottom-right, which was always specified as sitting *over* the photograph rather than
being derived from it. The photo slides in behind it the day the press images are hosted, and
nothing else about the head has to move.

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

**The live-state vocabulary moved to `common/content/presentation` when the fiche arrived.** It was
written inside the Programme's screen package with a note saying it would move up a layer for its
second caller and not before. The fiche is that caller: a visitor who taps `en cours` on the list
must not land on a screen that has gone quiet about it, so the fiche's date rows carry the same
pill, measured against the same tick. Mon Yadlo will be the third. The strings moved with it —
`slot_state_*` and `price_free` are named after what they describe rather than after the first
screen that happened to need them.

**The heart is attached to what you are saving, and the whole row is the target.** For a
Slot that is the date row; for a Stand, which has no dates, it is the button in the bar.
There is never more than one heart for the same thing on a screen, never a selection dialog,
and removing is the same heart tapped again. This replaces the earlier "hearts toggle, rows
navigate" rule: people expect to tap the row, and a fiche's date row has nowhere to navigate
to anyway.

**A Stand's menu has three levels.** `Menu → Group{name, items} → Item{name, price,
description?, marks?}`. Only name and price are required, and each item renders as up to
three independent rows — name with price, description, marks — so nothing shares a line with
the name and nothing can overflow into the price. An item with only a name and a price is a
complete item, which matters because that is the data most trucks will actually give.
Groups make drinks and combo menus expressible without a special case.

**Dietary information is text, not pictograms** — `végé`, `végan`, `sans gluten`,
`sans lactose`, `piquant`. No legend to learn, it translates for the English build, and it
avoids symbols that mean "contains" in one country and "free from" in another.

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
