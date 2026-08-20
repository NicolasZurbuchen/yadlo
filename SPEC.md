# Spec — Yadlo companion app, v1

Vocabulary: [CONTEXT.md](./CONTEXT.md). Reasoning and rejected alternatives:
[DECISIONS.md](./DECISIONS.md). This document is what to build.

**Design references** (interactive, published):

| | |
|---|---|
| Programme tab — 4 layouts, B2 retained | https://claude.ai/code/artifact/0c2b066e-8ab2-4199-8544-9a5000f63bf9 |
| Accueil — all five phases, clock control | https://claude.ai/code/artifact/a5755f3b-9b91-4235-997c-09a56e26bf71 |
| Mon Yadlo — 3 layouts, variante 3 retained | https://claude.ai/code/artifact/8398454a-cd2e-4819-ab70-7a1fa2b977c0 |
| Visual identity — 5 directions, direction 5 retained | https://claude.ai/code/artifact/4543e10d-31f0-4c9e-8a83-e695a3a48baf |
| Fiches — artist, activity, stand (v1→v3, v3 retained) | https://claude.ai/code/artifact/4f41f2e7-b93b-4320-b720-74480ce36572 |
| Plus — root, partners, contact, history, template, archives | https://claude.ai/code/artifact/aac2d18b-dc29-4631-93f2-6d72dfb63b4c |
| Plus · sur place — 7 screens | https://claude.ai/code/artifact/0dd3d937-4484-4ca9-990f-0c8c7ab4543a |

---

## Problem Statement

Yadlo is a three-day lakeside festival at Préverenges, running since 2015, drawing roughly
6000 visitors across a weekend each July. Its only information channel is a page-builder
website, and that website fails its visitors in ways that compound:

- **The information is not data.** Partner names, and much else, exist only inside images.
  Nothing is searchable, filterable, or usable offline.
- **Most of it is simply absent.** There are no stand names, no menus, no prices, no site
  map, and — most strikingly — **no statement anywhere of what time the festival opens or
  closes**. Activity hours exist for some activities and not others.
- **The site rots visibly.** Its sitemap carries roughly fourteen orphaned `copie-de-*`
  duplicates, published and indexed, because editing a page means duplicating it.
- **It cannot be personal.** A visitor who wants to catch three specific sets across three
  days has to hold that in their head or on paper.

On site the situation is worse than at home: sunlight, one cell tower shared by thousands of
phones, and a phone at 30% battery. The website is not usable in those conditions, and its
most consequential fact — **the festival is cashless, card and TWINT only** — is buried
where someone will find it after arriving with cash.

## Solution

A Compose Multiplatform app for Android and iOS that is the first place Yadlo's information
exists as structured data, and therefore the first place it can be personal, searchable and
available offline.

Four tabs — **Accueil · Programme · Mon Yadlo · Plus** — over a single content model. The
visitor browses the programme, taps a heart on any date to build a personal schedule, gets a
local reminder before each saved slot, and finds every practical fact the website has (plus
several it does not) in a grouped list that works with no network.

The app reshapes itself across the year from a countdown and a news feed, through the lineup
announcement, into a live companion during the festival weekend, and back out through a
thank-you — without changing its navigation, and without anyone remembering to flip a
switch.

Built unofficially, with the explicit aim of becoming the association's official app for the
2027 edition.

## User Stories

**Finding out what is on**

1. As a visitor, I want to see everything happening on a given day in one chronological
   list, so that I can plan my day without cross-referencing pages.
2. As a visitor, I want to see at a glance which things are running right now, so that I can
   decide what to walk towards.
3. As a visitor, I want to see how far through a running set I am, so that I know whether
   it is worth going.
4. As a visitor, I want to see what is about to start, so that I do not miss the beginning
   of something I care about.
5. As a visitor, I want things that have finished to fade but stay visible, so that I can
   still read the shape of the day I am in.
6. As a visitor, I want to filter the programme by kind — music, water, land, children,
   Silent Party — so that I can find what suits the people I am with.
7. As a visitor, I want to search across the whole programme at once, so that I do not have
   to guess which day something is on.
8. As a visitor, I want a search result to be a thing rather than an occurrence, so that an
   activity running all three days appears once and lists its dates.
9. As a visitor, I want search to also find practical information, so that typing "twint" or
   "parking" gets me an answer.
10. As a visitor, I want the programme to open scrolled to now during the festival, so that
    the app is useful in the two seconds I look at it.

**Building a personal festival**

11. As a visitor, I want to save a specific date of something to my own schedule, so that
    the app tracks my weekend rather than the whole programme.
12. As a visitor, I want saving to be one tap on the row I am already looking at, so that I
    never have to answer a dialog about which date I meant.
13. As a visitor, I want to unsave with the same tap, so that there is never a separate
    removal flow to find.
14. As a visitor, I want an activity running on several days to let me save each date
    independently, so that I can commit to Saturday without committing to Sunday.
15. As a visitor, I want my saved items on a timeline grouped by day, so that I can read my
    festival the way I will live it.
16. As a visitor, I want a reminder shortly before each saved item starts, so that I can
    stop watching the clock.
17. As a visitor, I want a warning as a saved item nears its end, so that I do not discover
    the paddle rental closed ten minutes ago.
18. As a visitor, I want to save a food stand separately from my schedule, so that "things I
    want to try" does not clutter my timetable.
19. As a visitor, I want my saved stands grouped by what they sell, so that I can find the
    one I wanted while queuing.
20. As a visitor, I want the app not to warn me that two saved things overlap, because at a
    site this small catching half of each is a normal evening.
21. As a first-time user, I want an empty personal tab to explain what it will do for me,
    so that I understand why I would fill it.
22. As a visitor, I want to build my schedule the week before from my sofa, because I will
    not do it standing in the sun.

**Deciding what to eat and drink**

23. As a visitor, I want a list of every food and drink stand, so that I can see my options
    without walking the site.
24. As a visitor, I want each stand's opening hours in the list, so that I know the bar runs
    later than the buvette.
25. As a visitor, I want to filter stands by dietary offer, so that I can find something I
    can actually eat.
26. As a visitor with a dietary requirement, I want dietary marks on individual dishes, so
    that I know which items work rather than which trucks might.
27. As a visitor, I want a stand's full menu with prices, so that I know what a round costs
    before I am at the counter.
28. As a visitor, I want to know that prices are last year's and unconfirmed when they are,
    so that I am not misled.
29. As a visitor, I want to know where to return a deposit glass and what it is worth, so
    that I get my money back.

**Getting there and home**

30. As a visitor, I want to know that the site is cashless before I leave home, so that I do
    not arrive with only cash.
31. As a visitor, I want to know which cards and wallets are accepted, so that I can check I
    have one.
32. As a visitor without TWINT, I want pointing at the official source, so that I install
    the right app for my bank rather than the wrong one.
33. As a visitor, I want the bus lines and stop, so that I can get there without a car.
34. As a visitor, I want the night bus departures grouped by night, so that I can read them
    in one glance rather than scrolling a list of rows.
35. As a visitor, I want to know that the last Saturday departure has no onward connection,
    so that I do not get stranded at Morges.
36. As a driver, I want to know parking is limited and distances vary, so that I arrive
    early or come another way.
37. As a cyclist, I want to know there is bike parking at the entrance.
38. As a visitor, I want to know how long the walk from Morges is, so that I can decide to
    walk it.

**Being able to come at all**

39. As a visitor with reduced mobility, I want to know exactly what is confirmed accessible,
    so that I can decide whether to come.
40. As a visitor with reduced mobility, I want to see plainly what is *not yet* confirmed,
    so that I am not misled by a reassuring but vague page.
41. As a visitor with reduced mobility, I want a direct way to write ahead, so that the team
    can arrange what the page cannot promise.

> **39–41 are not built in v1.** They were, and the screen was withdrawn: with nothing published it
> could only say so and offer an address, which is a page that exists to apologise for itself in
> front of the one reader who most needs an answer. The questions to put to the association are in
> content/GAPS.md § 9, and the screen returns when there are facts for it — see DECISIONS.md.
42. As a parent, I want to know where the children's corner is and where lost children are
    reunited, so that I have a plan before I need one.
43. As a visitor, I want emergency numbers and the first aid post in one place, so that I
    find them under stress.
44. As a visitor, I want to know how to recognise a staff member, so that I know who to ask.
45. As a visitor, I want to know where lost property goes during and after the festival.

**Orientation on site**

46. As a visitor, I want a site map with the stages, bars, food, children's area, toilets
    and first aid, so that I can orient myself on arrival.
47. As a visitor, I want map markers numbered as well as coloured, so that they work in
    sunlight and for colour-blind readers.
48. As a visitor, I want to jump from a thing to its place on the map, so that "where is
    that" is one tap.
49. As a visitor, I want to know when the site opens and closes each day, because that is
    the single question this screen exists to answer.

**Depth on a single thing**

50. As a visitor, I want an artist's genre, description and links, so that I can decide
    whether to plan around them.
51. As a visitor, I want an activity's price, minimum age and level, so that I know whether
    it suits my group.
52. As a visitor, I want to know whether equipment is provided and whether booking is
    needed, so that I turn up correctly prepared.
53. As a visitor, I want to book the Silent Party from its own screen, so that I do not have
    to find the ticketing site myself.
54. As a visitor, I want to share a thing with a friend, so that we can agree to meet there.
55. As a visitor, I want facts that are not links to not look like buttons, so that I do not
    tap things that do nothing.

**Across the year**

56. As a follower, I want a countdown out of season, so that the app is worth keeping
    installed.
57. As a follower, I want announcements from the organisers in the app, so that I hear about
    the lineup, tickets and volunteering.
58. As a follower, I want the app to tell me the moment the programme is published, so that
    I can start planning.
59. As a visitor in the week before, I want the app to nudge me to build my schedule, so
    that I do it while I still realistically can.
60. As a visitor on the opening morning, I want the app already in festival mode even before
    the gates open, so that it matches where my head is.
61. As a visitor at 02:15, I want the app to tell me it is over for tonight and when
    tomorrow starts, rather than showing an empty "now".
62. As a visitor on the Monday, I want a thank-you rather than a countdown, so that the app
    finishes the weekend gracefully.
63. As a returning visitor, I want to browse past editions, so that I can remember who
    played.
64. As a visitor, I want the app never to say "the lineup is here" before it is.

**Working offline and on a bad network**

65. As a visitor, I want the entire programme available with no signal, so that the app
    works when the cell tower is saturated.
66. As a visitor, I want practical information available offline, so that "what time is the
    last bus" is answerable at 02:00.
67. As a visitor, I want previously viewed images to stay available offline.
68. As a first-time user opening the app with no signal, I want to be told plainly that one
    connection is needed to download the programme, rather than a spinner that never resolves.

**Language and accessibility**

69. As a French-speaking visitor, I want the app in French.
70. As a non-French-speaking visitor, I want the app in English.
71. As a user, I want French shown when an English translation is missing, rather than a
    blank or a key.
72. As a user with large text enabled, I want every screen to remain usable, so that nothing
    depends on a fixed-width column.
73. As a screen reader user, I want a slot's state read aloud, because it is written in
    words rather than expressed as position.
74. As a colour-blind user, I want every category named in text, so that colour is never the
    only carrier of meaning.

**Maintaining the thing**

75. As the maintainer, I want to publish content without shipping an app build, so that I
    can fix a wrong price during the festival.
76. As the maintainer, I want one file per edition, so that content can never be half-updated.
77. As the maintainer, I want to mark content as unconfirmed, so that the app can be honest
    and I can flip a flag when the association confirms.
78. As the maintainer, I want the app phase derived rather than set, so that I cannot forget
    to flip it on the one weekend I am not at a laptop.
79. As the maintainer, I want a settable clock in the app, so that I can develop and test the
    live phase eleven months before it happens.
80. As a committee member who downloads this, I want to see who made it and how to reach
    them, so that I can start a conversation.

**Arriving and orienting**

81. As a visitor opening the app for the first time, I want a splash screen that looks like the
    festival rather than a blank frame, so that I know I opened the right thing.
82. As the association, I want our public backers shown on the splash screen, because that
    visibility is part of what they are owed.
83. As a visitor on the partners screen, I want to tap a logo and reach that company's site, so
    that I can find out who they are.
84. As a visitor, I want a partner with no website to tell me so when I tap it, rather than
    doing nothing and leaving me wondering whether the tap registered.
85. As a follower, I want an announcement to open the page it is about when there is one, and to
    be plainly untappable when there is not.

## Implementation Decisions

### Platform and shape

- Compose Multiplatform (Android + iOS) on the developer's existing KMP template, forked and
  renamed to `io.nicolaszurbuchen.yadlo`. Clean Architecture with an MVI presentation layer
  (MVIKotlin), Koin, Navigation 3, Ktor, SQLDelight, Coil3, and Konsist enforcing all of it.
- **The template has now been read, and the three capabilities this spec assumed do not stand
  equally.** Correcting the earlier draft:
  - *Offline-first disk caching* — **there.** SQLDelight is fully wired with an `expect`/`actual`
    driver factory on both platforms, so persisting a fetched bundle is routine. The bundled
    snapshot this draft once assumed is no longer wanted, so nothing is missing here.
  - *Local notifications* — **absent entirely.** `infra/platform/` contains only `BackHandler`
    and `Platform`; there is no notification code, no `expect`/`actual` seam, and no
    `POST_NOTIFICATIONS` permission in the manifest. The `Notifier` interface is not a
    formality wrapping an existing capability — it is the whole feature, on two platforms.
    **Deferred past v1.** The blast radius is exactly one user story — 16, the reminder before a
    saved item — plus the end-of-slot warnings of story 17. Everything else that looks time-driven
    (Phase, live pills, countdowns, Mon Yadlo) reads the injected clock and needs no notification.
  - *Disk-cached remote images* — **now configured.** Coil3 was wired to the shared Ktor client
    but built with no `diskCache { }` block, and Coil3 defaults that to null: every image was
    re-fetched on each cold start. It is now built in `infra/image/`, with the cache root behind an
    `expect`/`actual` seam and the subdirectory chosen in common code so the platforms cannot
    drift. Verified on an Android device — the directory is created, written and survives a
    reinstall. **Not yet verified on iOS**, which needs a Mac; the target compiles.
- Four bottom-nav destinations: **Accueil · Programme · Mon Yadlo · Plus**.
- **The default destination follows the phase**: Programme (scrolled to now) during LIVE,
  Accueil for the rest of the year.

### Domain model

Terms are defined in CONTEXT.md and must be used in code.

- `Happening` — sealed: `Artist` | `Activity` | `Stand`. Identity, description, images,
  detail payload.
- `Slot` — one Happening at one time on one FestivalDay. **The atomic unit**: what is
  favourited, what a reminder fires for. Ids are **Edition-qualified** (`2026:dubside-sat`) so a
  reused id cannot resurrect last year's saves.
- **Every Slot is timed — `start` and `end` are non-null.** "All day" is not a state the app
  models. A Happening that runs for the whole festival, such as the treasure hunt whose poster
  deliberately gives no hours, has its day's opening hours written into the content instead.
  This keeps one shape on the Programme, one sort order, one reminder rule and one live-state
  rule, and it deletes an absent-time branch from every path that formats a time. The cost is
  that those instants are derived rather than published, which is recorded where the model
  already records exactly that: their `provenance` is `unverified`, not `confirmed`. The
  validator rejects a null `start` or `end`, so the invariant cannot be reintroduced by content.
- `FestivalDay` — an explicit window with real start/end instants, **not a calendar date**.
  Friday runs to roughly 03:00 Saturday, so a 01:30 set belongs to Friday.
- **A FestivalDay's start/end ARE the opening hours** — one pair of instants, not a window plus
  a nested `opening`. An earlier draft split them because beach yoga runs at 10:00 on days the
  site opens at 12:00; the split turned out to be unnecessary, because a Slot's day is authored
  on the Slot rather than derived from the times. A Slot outside opening hours is therefore
  legal and merely worth flagging. The Programme derives its own range from the Slots it shows.
- **`Category` is the only grouping axis** — musique, silent, eau, terre, enfants, restauration,
  créateurs — declared in the content with a label and an order so the Programme's filter chips
  come from data. `Lane`, `Section` and per-place `Venue` were removed: they described rows of
  the Paléo grid, which is out of scope, and nothing in the app ever grouped or displayed by
  them. The one-stage constraint is now asserted directly — no two `musique` Slots may overlap.
- `Menu` → `Group{name, source, items}` → `Item{name, price, description, marks, provenance}`.
  Only name and price carry information; `description` and `marks` are always present and may be
  null or empty. A mark's **level is its meaning** — on the Stand it describes everything sold, on
  an Item only that item, which is the difference between "this stand is entirely vegan" and "this
  stand has a vegan option".
- **`Price` is one shape for every Activity, free or not**: `{free, tiers[], deposit, provenance}`,
  where `tiers` is empty exactly when `free` is true. The content previously carried three mutually
  exclusive shapes — a bare `free` flag, a flat `{amount, currency, per}`, and `{tiers, deposit}` —
  which meant every screen showing a price had to determine which one it held before reading a
  number. **A deposit is never summed into the price**: the Silent Party is CHF 25 with a CHF 50
  headset deposit, and CHF 75 is wrong in the direction that stops someone coming.
- `Provenance` on curated content — confirmed | archived | unverified.
- **`Attendance` was removed from the model.** Every Slot behaves identically. Its last job
  was clash detection, and clash warnings were dropped.
- **Five activity kinds**, not six: `musique`, `eau`, `terre`, `enfants`, `silent`.
- **The content model lives in `common/content/`, not in a feature.** It knows about zero features
  and is domain vocabulary all five share. Konsist had already settled this: `PackageHierarchyTest`
  treats `common` and `feature` identically, allowing `data`/`domain`/`presentation`/`di` under a
  named slice, and `DiLayerTest` expects `common/**/di`.
- **The bundle is atomic; the domain model is not JSON-shaped.** One file, one ETag and one fetch is
  a *content* decision and it stands. But the mapper's job is to turn that into something usable,
  not to restate it: **references are resolved as they cross the boundary.** A `Slot` carries its
  `Happening` and its `FestivalDay`, a `Happening` carries its `Category` — not the ids the JSON has
  to use because JSON has no other way to express a reference. A Programme row needs the Happening's
  name, its Category's label and its day before it can draw a single line, so leaving them as ids
  means every screen repeating the same join and each inventing its own answer for an id the
  edition does not declare. The join happens once, against the bundle the mapper already holds, and
  an unresolvable reference fails there — bad content, caught at the boundary.
- **A UseCase takes what it reads, never the whole Edition.** `DerivePhaseUseCase` takes the days
  and whether a programme exists, so its signature says what changes a phase and its tests do not
  have to build a festival to exercise one boundary.
- **`Happening` must never carry its Slots.** `Slot` holds a `Happening`, so the reverse reference
  would make the graph cyclic — and a cyclic `data class` blows the stack on `equals`, `hashCode`
  and `toString`. The one-way direction is load-bearing rather than stylistic. A fiche, which needs
  a Happening *and* its Slots, gets a small aggregate assembled by a UseCase instead.
- **Live state is derived, not stored.** `dans 15 min` / `en cours` / `se termine` / `terminé` comes
  from the clock and a Slot's window, recomputed on the ticker that also drives Phase. It is a
  UseCase because it touches a port, and it feeds both Programme and Mon Yadlo.
- **The heart is a join, not a field.** Saved Slot ids live in local storage, not in the content
  bundle, so every Programme and Mon Yadlo row combines two repositories. Nothing in the content
  model knows whether something is saved.
- **Only the UiModel reaches a Composable**, and that is enforced rather than trusted:
  `PresentationLayerTest` makes `State` illegal outside the Store/Executor/Reducer/UiMapper
  boundary. A Programme row's `Slot` carries a whole Artist with genres, links and a biography; the
  UiMapper drops all of it. That is the layer working, not waste.
- **`Phase` derivation is a UseCase** because it touches a port — the injected clock. Two
  derivations are easy to get wrong: the morning-after boundary is computed from the last day's
  **start**, never its end, since Friday ends at 02:00 on Saturday and an end-based derivation
  lands a day early; and every boundary is an instant in `Europe/Zurich`, so a phone in another
  timezone derives the same phase.
- **APPROACHING requires published slots, as ANNOUNCED does.** It exists to point at Mon Yadlo and
  say *ton programme t'attend*, which is as hollow as the ANNOUNCED hero with nothing to plan. An
  edition published early with dates and an empty programme stays OFF_SEASON, which already shows a
  countdown. LIVE and ENDED stay clock-only — the festival happens whether or not anyone published.

### Content architecture

Split by *frozen record* vs *live truth* — the test being "would a past-edition archive need its
own copy?" — plus one file that exists purely because of how often it changes.

```
content/
  festival.json        live:   histoire, contact, réseaux, transports,
                               paiement, FAQ, Hot'Staff
  announcements.json   live:   dated annonces, polled during LIVE
  editions.json        list of available editions (archives only)
  editions/
    2026/edition.json  frozen: programme, activités, stands, menus, prix,
                               horaires, partenaires, chiffres
    2026/images/       only what depicts this edition and no other — the affiche
  shared/              the picture bank, spanning every edition
    images/            artists/ · activities/ · stands/
    logos/             one per partner
  README.md            what this directory is, for whoever edits it next
  SCHEMA.md            every field, its nullability, and every closed value set
  GAPS.md              what is still missing, to put in front of the association
  validate.js          gates the deploy
```

**[`content/SCHEMA.md`](content/SCHEMA.md) is the contract** and the place to look before writing
any DTO. It documents each field's nullability and writes out every closed value set — provenance,
kind, category, marks, link types, currency, price units — including the fact that `kind` and
`category` are not independent (a `stand` may not be `musique`).

**`schemaVersion` is 1 everywhere and stays there until the app ships.** The shapes have already
changed several times; nothing reads them yet, so there is no older client to break. The number
starts meaning something at the first release.

- Static HTTPS from a versioned repo. No CMS, no Firebase. **Live at
  `https://nicolaszurbuchen.github.io/yadlo/`** — GitHub Pages, deployed from `content/` by CI on
  every push to `main`, with `validate.js` gating the deploy so content that does not validate is
  never published. That gate matters more than it sounds: the app fetches this at launch, and a
  malformed edition file is indistinguishable from being offline, so it would reach a user as a
  festival with no programme rather than as an error anyone could act on.
- Verified against the live site: `ETag` is served and `If-None-Match` returns **304 with zero
  bytes**, which is what makes re-checking one 44 KB edition file at every launch affordable.
  `Cache-Control` is `max-age=600` and Pages does not allow changing it — a correction takes up
  to ten minutes to propagate, which is fine except during the festival itself.
- **Instagram is the association's source of truth, not the website.** Established while
  authoring the 2026 bundle: `/artistes` was missing three of six Friday acts and had Diggin' on
  the wrong day, while every Instagram post was current and more detailed. The website is
  maintained in a way nobody can rely on. Practically this means the content files are
  **hand-transcribed from Instagram posts** — there is no feed to scrape and no API worth using,
  so authoring is manual and stays manual until the association adopts these files as their own
  source. That is also the strongest argument for the app becoming official: the JSON would stop
  being a copy of their communication and start being what their communication is generated from.
- **One file per edition** (60–150 KB): one fetch, one ETag, atomic consistency.
- **Fetch on launch → cache to disk. No bundled snapshot.** The cache is what makes the app work
  on the beach; a bundle would only ever serve someone who installed the app, never opened it,
  travelled, and then lost signal — and the "no content yet" screen has to exist regardless, since
  a bundle can be absent or stale. It optimises past a screen rather than removing it, at the cost
  of a second source of truth that goes quietly out of date. It returns behind the same interface
  if that proves wrong.
- **Cold start is two fetches, in order**: `festival.json` — small, and it names `currentEditionId`
  — then that edition. `announcements.json` follows and is polled more often during LIVE.
- **The cache is stored as a document, not shredded into tables.** Every read wants the whole
  bundle, and references resolve only after parsing, so a relational schema would buy nothing and
  cost a migration per schema change. It is parsed and resolved **once** into memory; every UseCase
  reads that, or each screen re-parses 45 KB for nothing.
- **A refresh never deletes saved data.** A Slot disappearing from the file means *no longer in the
  programme*, never a silent removal from someone's Plan.
- Archives are the **only** feature reading a third file, fetched on demand, and the only one
  that does not work offline unless previously opened.

### Schema changes and forcing an update

The content is served to every installed version at once, so a schema change reaches apps that
cannot read it. **The app must never hard-block on this.** An unofficial festival app that bricks
itself on the Saturday afternoon is worse in every way than one showing week-old data. Three
layers, in order of preference:

1. **Additive-only discipline.** Never remove or retype a field; only add optional ones. The client
   parses with `ignoreUnknownKeys`, so an older app ignores what it does not know. This covers
   almost every change and costs nothing.
2. **`schemaVersion` is the only "update the app" trigger.** If the fetched major is higher than the
   app supports, it keeps the cached content, does not parse the new file, and surfaces a soft
   update row in Plus. Additive changes need no bump, which is what keeps this rare.
   `minSupportedAppVersion` on `festival.json` is the escape hatch — no separate manifest file,
   because `festival.json` is already fetched first.
3. **Version the path** (`/v1/…`, `/v2/…`) only when a break is genuinely unavoidable, publishing
   both through a transition.

**A new edition needs no app update at all.** `currentEditionId` changes, the app fetches the new
edition, done. The only code it requires is clearing the Plan when that id changes: Slot ids are
Edition-qualified so stale saves cannot collide, but they would linger as orphans, so plan rows
whose id does not carry the current edition prefix are dropped explicitly.
- Images remote and disk-cached; only app chrome, category icons and **every image on the splash
  screen** are bundled. The splash is the one absolute exception — see § Screens.
- **Image references are relative by default.** Every Happening carries `images: [{src, credit}]`
  and every partner a `logo`; a `src` is either an absolute `https://` URL or a path relative to
  the **content root** — `shared/images/artists/alf.webp`. Relative is the intended form, and there
  is no `imageBaseUrl` to resolve it against: the app already knows the address it fetched the
  bundle from, so the base is written once in the client rather than in every edition. The join
  happens at the data boundary, beside every other reference the bundle resolves. `credit` is there
  because press photos usually carry a photographer's condition.
- **The picture bank is shared, not per-edition.** It lives at `shared/`, outside `editions/`,
  because an artist who plays two years running is one photograph rather than one per edition.
- **A photo and a logo are separate fields, not one image field.** A photo is cropped into a
  collapsing toolbar behind a scrim; a logo must never be cropped, tinted or bled to an edge.
  Sharing a field name is what leads to a sponsor's logo being rendered like a press shot.

### Phase

Derived from **(clock, last-fetched content)** only. Never authored.

```
OFF_SEASON    default
ANNOUNCED     edition has published slots      →  J-7
APPROACHING   J-7                              →  00:00 on day one
LIVE          00:00 on day one                 →  11:00 the morning after the last day
ENDED         then                             →  +6 weeks
```

- ANNOUNCED keys off `slots.any()`, not a countdown threshold — the hero it triggers claims
  the programme exists, and a date cannot make that claim honestly.
- Phase boundaries are **deliberately wider** than FestivalDay windows.
- **Recompute on a ticker while running**, not only at launch — the same ticker drives live
  state pills and countdowns.
- All comparisons in **`Europe/Zurich`** instants, never the device wall clock.
- A **settable clock** must exist from the first commit.

### Screens

**Accueil** — a block stack per phase (blocks enumerated in DECISIONS.md). Global search
appears only in ANNOUNCED / APPROACHING / LIVE. LIVE is deliberately thin because the app
opens on Programme then. ENDED's thank-you carries the association's *À tout bientôt*
photograph, bundled — the only hero in the app with a picture behind it (DECISIONS.md § The
thank-you is the one hero with a photograph).

**Programme** — layout B2: one chronological list per day, no calendar column, no "now"
line. **A row is a Happening on a day**, carrying every hour it runs — an activity in sessions is
one row with its times on one line and a mark per session on the bar, not one row per session
(DECISIONS.md § A row is a Happening on a day). Each row carries live state as a text pill
(`dans 15 min` / `en cours` / `se termine` / `terminé`), a raised progress bar while running, and
dims when past. `terminé` and the dimming wait for every hour on the row to be finished; a single
past hour dims on its own. Past rows are never collapsed. Countdowns appear only within a one-hour
window.

**Mon Yadlo** — the rail variant: date pinned left, items scrolling past, time written once
as a range, same row vocabulary as Programme — **span bar included**, on one axis shared by every
day on screen, with its scale written once in the chrome and inset to where the bars begin
(DECISIONS.md § Mon Yadlo's bars share one axis across the days). Timeline plus one full-width
*À essayer* hero. **Recall-only** — no browsing, no add-flow.

> **What shipped.** Both halves. The tile sits above the timeline and carries a count; it opens
> *à essayer*, the saved Stands grouped by Category — **as the same photograph-led cards the browse
> lists draw**, not rows of their own (DECISIONS.md § A Stand is a photograph). The timeline shows
> only the days something is saved on, each with its date on the rail and its rows carrying the
> Programme's live-state pills. Rows navigate to the fiche and carry no heart of their own —
> DECISIONS.md § The heart is attached to what you are saving. Nothing here can add: both empty
> states point outward, at the Programme and at Plus.

**Fiches** — one template for Artist, Activity and Stand: collapsing toolbar over a photo (title
rising into the bar on scroll as the bar takes the category colour, continuously rather than at a
threshold; the bundled placeholder photograph where the content has no picture; status bar taking
the colour), written category label, attribute-only tags, sections. *Liens* renders as the footer's
row of marks, left-aligned. Round icon actions in the bar. Silent Party = same template + booking
row.

A Stand's carte is **one section per menu group**, headed like every other section of the fiche;
there is no *Au menu* above them. Beside a dish a dietary mark is **the glyph alone**, its words
written once over the whole stand at the top of the fiche, and the dish name is a title rather than
body text so it carries an edge over its own ingredient line. No per-group source line: the content
keeps the record, the screen stopped repeating it five times. Tabs over the carte were built and
removed — DECISIONS.md § Tabs over the carte were built and then taken out.

> **What shipped, and what the fiche is still missing.** The template, the head, the hero
> photograph and its continuous collapse, the date rows with their live-state pills and their
> hearts, the tiered price with its deposit, the menu, the facts and the links are built, as is the
> Stand's single heart in the bar. **The head is one ground now** — every fiche opens on a
> photograph, the bundled one for the two stands whose picture has not arrived (content/GAPS.md
> § 7); the Category blob that stood in for it is gone, DECISIONS.md § The fiche has one ground.
> **Each group of a carte is a section of its own**, so the template still does nothing for a Stand
> that it does not do for an Artist. The share and map actions are not built, nor is the status-bar
> tint. Each is deferred for its own reason, recorded in DECISIONS.md § One fiche template for
> everything.

**Plus** — iOS-style grouped list, four cards: *Sur place* · *Le festival* · *S'impliquer* ·
*L'application*. Payment is the third row of the tab. Lost property, first aid, children and
emergency numbers merge into a single **"En cas de besoin"** screen — justified by a shared
user situation, not by tidiness. Most remaining entries share one text-page template.

> **What shipped, and the rows that did not.** All four cards, fifteen rows, and thirteen
> screens behind them. **Every row is derived from the section behind it**, so the tab can never
> open a screen with nothing on it — which is what let the whole thing ship while half the
> festival's practical information is unpublished, and what makes a rolled-back publish cost a row
> rather than a dead end. The four groups and their order live in the screen, not in the content or
> the domain: that payment is third is a design decision about how the tab is read.
>
> One entry is built around content that does not exist and is better for it: **Horaires** deduces
> everything from the programme — a FestivalDay's window *is* the opening hours — so it needed no new
> content field and follows a set added mid-festival on its own.
>
> **Accessibilité tried the same trick and failed at it.** A screen assuming the absence works when
> the absence is still an answer; here it left a visitor deciding whether a beach is navigable in a
> wheelchair with a page saying nobody knows, write in. Withdrawn entirely — section, DTO, model and
> screen — until the association confirms something. content/GAPS.md § 9 holds the questions.
>
> *Festival responsable* and *Réseaux sociaux* share **one parameterised screen** — the gabarit the
> prototype concluded most of this tab is. *À propos* and *Confidentialité* are static: every word
> on them is an app string rather than content, which is exactly what makes them the two rows that
> survive a publish going missing.
>
> **Three rows of the prototype are deliberately absent.** *Plan du site* has no content at all —
> only a parking PDF exists and the booth map has to be drawn. *Langue* would open a picker with one
> language in it. *Notifications* are a settled deferral past v1. A row that opens nothing is worse
> than no row. **Two more are outside this pass**: *Éditions précédentes* needs the on-demand third
> file, the only non-offline feature in the app; *Effacer mes données* needs a repository capability
> that does not exist yet and is the one row that writes.

**Partenaires** — logos grouped by tier. **Tapping a logo opens that partner's site in the
browser; a partner with no URL shows a toast saying it has none.** Five of the 39 have no website —
two genuinely have none, one is an activity of the festival rather than a company, and one has an
address that 404s — so silence on tap would be the common case, not the edge one, and a tap that
does nothing reads as a bug.

> **What shipped.** The tiers, sorted by their declared order, as grids of tappable cards — and the
> cards draw **names rather than logos**, because no partner in the 2026 content has a logo file
> supplied. The name is what the logo was standing for, and a grid of grey rectangles would read as
> a broken screen. The "no website" message is keyed on a **counter in the state** rather than
> published as a one-shot Label, so that a second tap says it a second time and the behaviour is
> assertable rather than a fire-and-forget.

**Splash** — one background photograph of the beach under a tint, the Yadlo wordmark, the logo and
the motto, and beneath them the two **soutien public** logos, Morges and Préverenges.

**Every image on this screen is bundled in the app, not fetched** — the one place in the app where
that rule is absolute. The splash draws before any fetch has completed; that is what it is for.
Reading the backer logos from the content bundle would mean either a splash that waits on the
network, or one that draws incomplete on a first launch — which is exactly the case story 68 is
about, someone opening the app on-site with no signal. If the platform splash APIs are ever used
(Android 12+ `SplashScreen`, iOS launch screen), remote images stop being merely slow and become
impossible: the system resolves those drawables before the `Application` class runs.

The cost is real and worth stating: **changing a backer needs an app release.** That is acceptable
because the `soutien-public` tier moves on a timescale of years — a commune and a regional tourism
board — not per edition. The Partners screen still reads the full list from the content, so the
authoritative list stays live; only these two images are duplicated into the app, on purpose.

**A FAQ belongs in *Sur place*, and it was missed in the prototypes.** It surfaced from the
plainest possible question — *is entry free?* — which no screen answered and no mock had a place
for. That is the shape of the whole problem: the association's information is scattered across a
stale site and a live Instagram, so the questions a first-time visitor actually asks have no
single home. `festival.json` carries a `faq` list of question/answer pairs, and that is currently
the only place the answer lives.

> **`entry` and `openingNote` were removed from the Edition.** An earlier draft carried
> `entry.free` as a structured field so a past-edition archive could state what it cost even if a
> later edition started charging. That reasoning still holds, but no screen renders either field,
> and content nobody reads is content nobody notices going stale. Both come back the day the
> Horaires and Sur place screens exist; `validate.js` **errors** if either reappears before then,
> so the decision cannot quietly reverse itself.

### Interaction rules

- **The heart is attached to what you are saving, and the heart is the only target.** Slot →
  the mark on the date row. Stand → button in the bar. Never two hearts for one thing on a
  screen. Never a selection dialog. *The whole row was the target until the fiche was built
  against real content: making a row tappable meant lighting a kept one, and a lit row reads
  as selected rather than as kept.*
- **Lists compare, cards separate.** Programme and Mon Yadlo's timeline use rows; the three
  places a Stand is listed — *Nourriture & boissons*, *Créateurs*, *À essayer* — and grouped Plus
  entries use cards. Measured: cards cost +32% vertical space in Programme. A stand card is one
  component, shared, and it leads with the Happening's photograph (DECISIONS.md § A Stand is a
  photograph).
- **Facts must not look tappable.** Card-with-chevron is reserved for navigation; `↗` marks
  an external link, `›` internal, `✉` opens mail.
- **An annonce carries a nullable URL, not a typed action.** Reversed from the earlier
  `none | programme(day?) | happening(id) | plus(entry) | url(external)` design, which was more
  machinery than the job needs. An annonce is a dated record whose only job is to open somewhere;
  `null` means the card is not tappable. The cost is real — it can no longer point at a specific
  fiche — but a dead deep link into a renamed screen is a worse failure than an ordinary broken
  web link, and "content outlives app versions" is precisely why the internal targets went.
- **A partner logo opens its site; a partner without one says so.** Never a silent tap.
- **S'impliquer is a router, not a form** — no backend, no stored messages, and the
  association's existing recruitment pipeline keeps receiving its applications.

### Identity

```
Bandeau / marque   #74AEE0      Primaire  #14618F      Encre  #12242F
musique #DD3B7A   eau #1B86C9   terre #2FA35A   enfants #F5B000   silent #8A4FD4
```

Chosen on measured perceptual separation (ΔE 58.5 light / 49.2 dark, best of five
directions). **Dark ink on light brand colour everywhere** — the website's white-on-`#74AEE0`
is 2.4:1; navy on the same blue is 5.4:1. Typography: **Barlow** (SIL OFL), Semi Condensed
for display, regular for body; **no monospace** — times use the display face with tabular
figures. Accent `#E27BA6` is still open (see DECISIONS.md).

### Notifications and i18n

- **Notifications are deferred past v1**, local ones included. There is no notification code on
  either platform, no `expect`/`actual` seam and no `POST_NOTIFICATIONS` permission, so the
  `Notifier` interface is the whole feature rather than a wrapper over something that exists.
  When they land they are **local only** — countdown, per-slot reminders, end-of-slot warnings —
  with remote push behind the same interface so FCM can arrive without a rewrite.
- **What deferring them actually costs is two user stories**, 16 and 17. Everything else that looks
  time-driven — Phase, the live-state pills, the countdowns, Mon Yadlo — reads the injected clock
  and recomputes on the ticker, so it needs no notification and is unaffected.
- **Content is French-only.** Revised after authoring the real 2026 bundle: every human-readable
  field in the content files is a plain string, not a `{fr, en}` object. The festival is French,
  its programme is French, and carrying an `en` key that is empty on all 29 happenings bought
  nothing but noise. `validate.js` fails the build if a localized object reappears.
- **UI strings stay translatable** — they are Compose Multiplatform resources and independent of
  the content bundle, so an English UI over French content remains possible without a schema
  change. If content translation is ever wanted, it returns as a parallel field or a parallel
  file, and that is a deliberate migration rather than something to carry speculatively now.

## Testing Decisions

**What makes a good test here.** Tests assert on what a user would observe — the state a
screen presents — never on how it was computed. No test should name a private function, a
cache key, or a JSON field it does not need. A test that breaks when the layout changes but
the behaviour does not is a bad test.

**The seams are not open for proposal — they are already fixed and enforced.** An earlier
draft of this spec proposed a single new seam at "the screen state producer". That was written
before the template was read, and it is wrong for this codebase. `konsistTest/TestingTest.kt`
*mandates* a test file for every one of these, and the build fails without them:

| Production file | Required test |
|---|---|
| `*Mapper` (in a `mapper/` package) | `*MapperTest` |
| `*RepositoryImpl` | `*RepositoryImplTest` |
| `*DataSourceImpl` | `*DataSourceImplTest` |
| `*UseCase` | `*UseCaseTest` |
| `*UiMapper` | `*UiMapperTest` |
| `*StoreFactory` | **both** `*ReducerTest` and `*ExecutorTest` |

So the correct move is the skill's own rule — prefer existing seams — and the existing seams
are the layer boundaries the architecture already draws. Two of them carry most of the weight
for Yadlo:

- **`*UiMapper` is the "what the user observes" seam.** `State` never leaves the
  Store/Executor/Reducer/UiMapper boundary (enforced by `PresentationLayerTest.kt`), so the
  `UiModel` a `UiMapper` produces *is* what the screen renders. Asserting on it is asserting
  on what a user would see. This is where live-state pills, countdown text, dimming and
  written category labels get tested.
- **`*UseCase` is the seam for derived time.** Phase derivation and FestivalDay assignment
  touch a port (the clock), which is exactly what the convention reserves a UseCase for, and
  that earns them an enforced test file.

**No mocking library** — a rule of the repo, not a preference. A seam is either a hand-written
fake or an injected lambda:

- A **file-local `private class`** for a one-off data-source or API double.
- A **single shared `Fake<Feature>Repository`** in `domain/fake/` once several test files for
  the same feature need it. Konsist enforces that anything named `Fake*` implements an
  interface and lives in `..domain.fake`.

I was also wrong to write "no mocking of HTTP". HTTP is faked at Ktor's `MockEngine`, and
`ContentApiImplTest` is the prior art: build the client with a mock engine, capture the outgoing
`HttpRequestData` to assert the URL, and hand back a canned JSON body to assert deserialisation —
including a malformed-body case, which is otherwise unreachable.

**The injected clock is a supported pattern here, not an imposition.** The architecture
convention already documents it, along with the Koin trap it sets: `singleOf(::Impl)` resolves
*every* constructor parameter by reflection, including ones with Kotlin defaults, so a class
holding a defaulted clock lambda needs an explicit `single<Interface> { Impl(get(), get()) }`
binding instead. Get this wrong and the "injected clock" silently comes from the DI graph in
tests too. No `Clock.System.now()` outside the composition root.

**Mechanics, all established by the template:** `runTest` wraps every suspend test
unconditionally, even with no async work. Turbine is for `Label` flows only; `StateFlow` state
is read synchronously after `testDispatcher.scheduler.runCurrent()` / `advanceTimeBy(...)`.
Tests are named `subject_condition_expectedOutcome`
(`generationStarted_setsLoadingTrueAndClearsError`).

**When Yadlo establishes a new category of production file, add it to `TestingTest.kt`'s
coverage list** — that file is the spec for what must be tested, and leaving it stale is how
coverage rules quietly stop meaning anything.

**What gets tested, in priority order:**

1. **Phase derivation** — the highest-value pure function in the app. Table-driven across
   every boundary: OFF_SEASON with an edition file but no slots; the flip to ANNOUNCED on
   publish; J-7; midnight on day one; 11:00 the morning after; six weeks later. Plus timezone
   cases: a device in another timezone must derive the same phase.
2. **Live state of a Slot** — upcoming / running / ending / finished, the one-hour countdown
   window, and the midnight-crossing case (a 23:30–01:30 set belongs to Friday).
3. **FestivalDay assignment** — the case that will break with any naive date formatter.
4. **Plan and Wishlist** — a saved Slot lands on the timeline; a saved Stand lands on the
   checklist; a Stand never reaches the timeline however long its hours; edition-qualified
   ids do not collide across years; saving and unsaving are the same operation.
5. **Content fallback** — no network with a warm cache serves the cache; no network and a
   cold cache serves the bundle; a malformed edition file does not crash the app.
6. **Annonce actions** — an action pointing at an unknown Happening renders the annonce
   without its button.
7. **Search** — returns Happenings not Slots; matches practical information; is
   diacritic-insensitive ("preverenges" finds "Préverenges").

**UI tests are deliberately few.** A handful of Compose tests for things state assertions
cannot reach: the collapsing toolbar reaching its collapsed state on scroll, and large-text
rendering on Programme and the fiches. Screenshot tests are optional and, if added, should
cover light and dark for one screen per tab rather than everything.

**Prior art exists and should be copied, not reinvented.** The template's example feature carried
a worked example of every test category above and was kept in the tree for exactly that reason
until the real slices replaced it; it is now deleted. Read `PlusReducerTest` before writing a
reducer test, `ContentApiImplTest` before an API test, `FakeContentRepository` before a fake.

## Out of Scope

- **Notifications of every kind, local included** — deferred past v1. Neither the interface nor the
  implementation exists today. For remote push specifically there is a second reason on top of the
  work: an unofficial app broadcasting operational claims is a content problem, not a technical one.
- **Volunteer group chat** — a second product: identity, roles, moderation, retention, and
  someone on call at 02:00. Requires being official.
- **Personal transport reminders** — dropped from v1. Timetables stay as static content. The
  design that survived scrutiny is recorded in DECISIONS.md for when it returns.
- **Live transport API calls** — a network request at 02:00 with 6000 phones on one tower.
- **A live Instagram feed** — not reachable unofficially since the Basic Display API shut
  down; needs a Business account and a Meta app authorised by the account owner.
- **The timetable grid (Paléo-style)** — possibly worth it on tablet, not in v1.
- **Multi-edition browsing UI beyond a simple archive list.**
- **Preserving a user's Plan across editions** — it is discarded when a new Edition
  publishes.
- **Menu-item-level favouriting** — the Wishlist saves Stands. Additive later if wanted.
- **Clash warnings** — a big-festival feature.
- **Accounts, sync, backup** — everything is local. Uninstalling loses the Plan, accepted.
- **A photo gallery** — the aftermovie is one link; photos live on Instagram.
- **An interactive map with live positioning** — the site is visible in 360° from the middle.
  A static, zoomable, numbered plan is the answer.

## Further Notes

### The real dependency is content, not code

Every code decision here is reversible in an afternoon. The list below is not, and it is the
project's binding constraint:

- **The festival's own opening hours.** Stated nowhere. All `FestivalDay` windows are
  currently guesses, and the entire timeline rests on them. **Twelve numbers** would resolve
  it — three days × (site, bar, restauration, activités).
- **Stand data of every kind** — names, menus, prices, hours. Zero exists. Every food feature
  depends on it.
- **Artist detail** — the site gives a name, a time, a stage and sometimes a genre.
- **A site plan** — only a parking PDF exists; the booth map must be drawn with the
  association.
- **Activity hours** for the items that publish none.

**There is a direct line to a founder.** Asking is strictly cheaper than reconstructing:
this data is not lost, it is in someone's spreadsheet. *"Can you send me last year's stand
list, I'm building something"* is an easier first message than a pitch, and it is also the
first step toward the app becoming official.

### Timing

The 2026 edition ended 12 July 2026. The 2027 lineup is expected around May 2027. That is
roughly eleven months of runway with **no live event to test against** — which is why the
settable clock is a first-commit requirement rather than a debug convenience.

The strongest argument to the committee is not a pitch deck but a working app on a phone
showing *their* 2026 festival. Autumn, while the debrief is fresh, is the window.

### Open questions carried forward

Listed in full in DECISIONS.md § Open. The ones that will bite soonest:

1. **The accent colour** `#E27BA6` versus `musique` `#DD3B7A` — a pink accent and a magenta
   kind-dot may read as one signal.
2. **Whether search covers practical information** — nearly free at this corpus size.
3. **Whether dietary marks stay coloured** in the stand list, where category colour was
   deliberately removed.
4. Facts to verify with the association: is there an ATM nearby; is there a glass deposit and
   how much; do they issue wristbands for children.

Sorting in Programme (Heure / A–Z / Prix) was on this list and is resolved: dropped for v1,
see DECISIONS.md § Programme layout.
