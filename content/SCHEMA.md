# Content schema

What every field in these files means, what may be null, and which values are legal.
`validate.js` enforces everything written here — if the two disagree, the validator is right and
this file is stale.

Vocabulary is [CONTEXT.md](../CONTEXT.md). Why the content is shaped this way at all is
[DECISIONS.md](../DECISIONS.md).

> **`schemaVersion` is 1 in every file and stays there until the app ships.** These shapes have
> already changed several times, but nothing reads them yet, so there is no older client to break
> and nothing to migrate. The number starts meaning something at the first release; bumping it
> before then would only record churn nobody experienced.

## The files

| File | Holds | Fetched |
|---|---|---|
| `festival.json` | Live truth — history, contact, transport, payment, FAQ, volunteering | At launch |
| `editions/<year>/edition.json` | One frozen Edition — programme, activities, stands, menus, partners, figures | At launch |
| `announcements.json` | Dated annonces from the organisers | At launch, and polled during LIVE |
| `editions.json` | The list of editions that exist | On demand, archives only |

The test for which of the first two a field belongs in is **not** "does it change every year" but
**would a past-edition archive need its own copy?** Browsing 2026 shows 2026's lineup and 2026's
statistics, but *today's* contact address.

## Conventions that hold everywhere

- **Every human-readable string is plain French.** No `{fr, en}` objects. The validator fails the
  build if a localized object reappears. UI strings are a separate concern and stay translatable.
- **Optional means present-and-null, never absent.** A field that can be empty is written `null` or
  `[]` rather than omitted. Absent and null meant the same thing and forced every reader to handle
  both to learn nothing — which is what the Kotlin DTO layer walked into. The validator now errors
  on an omitted key, and on an unknown one: a typo'd field name is otherwise indistinguishable from
  a field left out.
- **Ids are lowercase kebab-case** and unique within their collection.
- **Slot ids are Edition-qualified** — `2026:dubside-sat` — so a reused id cannot resurrect last
  year's saved plan.
- **Instants are ISO-8601 with a real offset** (`2026-07-10T17:00:00+02:00`), never a bare local
  time. Comparisons happen in `Europe/Zurich`.

## Enumerations

Every closed value set in the content. Widening one should be a decision, not a typo.

| Set | Values |
|---|---|
| `provenance` | `confirmed` · `archived` · `unverified` |
| `kind` | `artist` · `activity` · `stand` |
| `category.id` | `musique` · `silent` · `eau` · `terre` · `enfants` · `restauration` · `createurs` |
| `marks` | `vegan` · `vegetarien` · `sans-gluten` · `sans-lactose` · `halal` · `piquant` |
| `link.type` | `website` · `instagram` · `facebook` · `youtube` · `tiktok` · `spotify` · `appleMusic` · `soundcloud` · `bandcamp` · `beatport` |
| `currency` | `CHF` |
| `price.tiers[].per` | `personne` · `équipe` · `null` |

**`kind` and `category` are not independent.** A kind may only carry certain categories, because
nothing else stops a typo pairing `kind: "stand"` with `category: "musique"`:

| kind | allowed categories |
|---|---|
| `artist` | `musique` |
| `activity` | `silent` · `eau` · `terre` · `enfants` |
| `stand` | `restauration` · `createurs` |

The lists are deliberately narrow — exactly what exists today. The first realistic widening is a
`musique` **activity** (an initiation au mix), which is also why the two fields cannot be merged.

**Category colour is not in the content.** The content declares a category's label and order so the
Programme's filter chips get their French names from data; the colour lives in the app, keyed by id,
because colour is a design decision made once against a measured palette.

---

## `edition.json`

```
schemaVersion  number   1
id             string   "2026"
year           number
name           string
venue          Venue
days           FestivalDay[]
categories     Category[]
happenings     Happening[]
slots          Slot[]
partners       PartnerTier[]
figures        Figure[]
```

> `entry` and `openingNote` were **removed**. No screen renders them yet, and content nobody reads is
> content nobody notices going stale. The FAQ in `festival.json` still answers whether entry is free.
> Both come back as structured fields the day the Horaires and Sur place screens exist; the validator
> rejects them until then so they cannot drift back in unnoticed.

### Venue

```
name, address   string
latitude        number
longitude       number
provenance      Provenance
```

One per Edition, and part of the frozen record: if the festival moves, the 2026 archive must still
say Préverenges.

### FestivalDay

```
id          string      "2026:fri"
name        string      "Vendredi"
date        string      "2026-07-10" — display only
start, end  instant     THE OPENING HOURS for that day
provenance  Provenance
```

**`start`/`end` *are* the opening hours.** There is no separate `opening` object — the validator
errors if one appears. Friday runs 16:00 → 02:00 the next morning, which is why a 01:30 set belongs
to Friday.

Two consequences:

- **A Slot may legally fall outside the window.** The beach is public, so the morning yoga and the
  climbing wall run from 10:00 on days the site opens at 12:00. The validator *warns* rather than
  errors: the case is real, but next year one of them may be a mistake.
- **Never derive a Slot's day from its instant.** `dayId` is authored. `date` is for display only.

### Category

```
id     string   from the category enum
name   string   "Sur l'eau"
order  number   display order of the filter chips
```

### Happening

Shared by all three kinds:

```
id           string
kind         "artist" | "activity" | "stand"
name         string
category     string          must be declared in categories[] and legal for the kind
description  string | null
images       Image[]         [] when none
provenance   Provenance
<kind>       object          exactly one payload, named after the kind
```

`Image` is `{ src, credit }`. `credit` is usually null and exists because press photos carry a
photographer's condition. A `src` is an absolute `https://` URL, or a path relative to the **content
root** — `shared/images/artists/dj-alf.webp`, never `../../shared/…`. It is the published site's
root rather than the file's own directory, so a path reads the same whichever file it is written in.
There is no `imageBaseUrl`: the app already knows the address it fetched the bundle from, and
declaring it again in the content would be the same fact written twice, free to drift.

The picture bank lives at `shared/`, outside any edition, because a photo of an artist who plays two
years running is one file rather than one per edition. Inside it, `images/` is split by the
Happening's kind and **a file is named for the Happening it depicts** — `artists/dj-alf.webp`
against `dj-alf`, and `<id>-1.webp`, `<id>-2.webp` for a Happening with several. Nothing enforces
that, and nothing needs to: the convention exists so that a photo can be found without reading
`edition.json`, and a name that drifts costs one Happening its picture rather than misfiling it
under another.

#### `artist` payload

```
genres  string[]
links   Link[]     { type, url }
```

#### `activity` payload

```
genres             string[]
price              Price | null
bookingRequired    boolean
bookingUrl         string | null
equipmentProvided  boolean | null
suitability        string | null    "De 4 à 12 ans, deux heures maximum"
supervised         boolean | null   whether a child can be left there
```

**Price is one shape, free or not.** It used to be three mutually exclusive ones — a bare `free`
flag, a flat `{amount, currency, per}`, and `{tiers, deposit}` — so the app had to sniff which it
held before it could read a number. That cost landed on every screen showing a price. It now lands
in the validator, once.

```
price
  free        boolean
  tiers       Tier[]              empty exactly when free is true
  deposit     Deposit | null
  provenance  Provenance

Tier
  label     string | null   null when there is one price for everyone
  amount    number          as authored: 4.5 means CHF 4.50, not 450 centimes
  currency  string
  per       string | null   null when the price is per person

Deposit
  amount    number
  currency  string
  note      string | null
```

`free` and `tiers` are two views of one fact and are checked against each other: `free: true` with a
tier, or `free: false` with none, is a content bug that would render as "gratuit — CHF 10".

**A deposit is not part of the price and must never be summed into it.** The Silent Party is CHF 25
with a CHF 50 headset deposit; showing CHF 75 would be wrong in the direction that stops someone
coming.

A **missing poster price means free**, not unknown: the association's posters carry a price when
there is one and nothing when there is not.

#### `stand` payload

```
offering  string | null    "Cuisine végétale"
links     Link[]
menu      MenuGroup[]      [] when unpublished
```

```
MenuGroup
  id           string
  name         string          "Plats", "Boissons"
  description  string | null   what the group is when its name does not say
  source       string | null   where these prices came from, in the author's words
  items        Item[]          at least one

Item
  name         string
  price        { amount, currency } | null
  description  string | null
  marks        string[]        [] when none — describe THIS ITEM only
  provenance   Provenance
```

**Marks live on the dish and nowhere else.** A Stand used to carry its own list, meaning "all of it
is", while its items carried theirs, meaning "this one is" — two levels that could contradict each
other, and that a content edit adding one non-vegan dish would silently make false.

The app derives the stand's answer from its menu instead: a mark every dish carries reads as
*100 % végan*, one that only some carry reads as *options véganes*. That is the difference someone
scanning a row of trucks is actually asking about, and deriving it is the only way it cannot be
wrong.

Which means **every dish must be marked**, including the ones that carry nothing: an untagged dish
is read as "not vegan", and one forgotten drink is what turns *100 % végan* into *options véganes*.

**The vocabulary is closed and slugged.** Slugs rather than the French words they used to be,
because the app keys an icon and a colour off each one, and `végan` is two different byte sequences
depending on who typed it. The label a reader sees is the app's, exactly as a Category's colour is.

`bio` is gone with the stand level: it is a claim about sourcing rather than about whether someone
can eat the thing, and it was the one mark the filter could not answer a question with. `halal`
replaces it in the vocabulary and nothing carries it yet — see GAPS.md.

`source` exists because **no menu here is confirmed by the festival**, and they are not all
unconfirmed in the same way: two are the vendor's own published carte cut down to festival portions,
two are reconstructed from the dishes the vendor lists without prices, and two are plausible cartes
for a stand that publishes nothing at all beyond "crêpes, gaufres et glaces". A reader deciding
whether to trust a price deserves to know which of those they are looking at, and that belongs next
to the price rather than in a document nobody ships.

### Slot

```
id           string      Edition-qualified: "2026:dubside-sat"
happeningId  string      must exist in happenings[]
dayId        string      must exist in days[] — AUTHORED, never derived
start, end   instant     never null; end must be after start
provenance   Provenance
```

**Every Slot is timed.** There is no all-day Slot: a Happening running the whole festival carries its
day's opening hours, written out like any other Slot and marked `unverified` because those instants
were derived rather than published. The validator errors on a null `start` or `end`, so no screen has
to format an absent time.

### PartnerTier / Partner / Figure

```
PartnerTier   id, name, order, provenance, members: Partner[]
Partner       id, name, url: string|null, logo: Image|null
Figure        id, value: string, label, provenance
```

`Figure.value` is a string because some are ranges or carry a qualifier, and the screen only ever
prints it next to `label`.

`Partner.url` is null when no website could be verified. **The app shows a toast rather than a dead
link** when a logo with no url is tapped.

`logo` and `images` are deliberately different fields. A photo gets cropped into a collapsing toolbar
behind a scrim; a logo must never be cropped, tinted or bled to an edge. Sharing a field name is what
leads to a sponsor's logo being rendered like a press shot.

**A logo file is named for the partner id, exactly** — `shared/logos/volt-a.webp` against the partner
`volt-a` — the same rule the happening photographs follow, and for the same reason: the name *is* the
lookup, so there is no table to fall out of step and a drifted name shows up as one partner with no
logo rather than as a silent mismatch. Both `.svg` and `.webp` are in the bank and the app reads
either; the extension is not part of the lookup.

---

## `festival.json`

```
schemaVersion   number
name, tagline   string
website         string
currentEditionId string
minSupportedAppVersion string | null
histoire        { foundedYear, body, journee: {title, body, provenance}, provenance }
faq             { id, question, answer, provenance }[]
responsable     { charters: { id, name, body, url, provenance }[] }
contact         { address: {lines[], provenance}, phone, emails: {id, address, label, responsible}[], provenance }
social          { id, name, url }[]
links           { id, label, url }[]
transports      { modes: Mode[], provenance }
paiement        { headline, summary, methods: Method[], notes: {id, title, body, links: Link[]}[], provenance }
besoin          { emergencyNumbers: {id, label, number}[], recognition: {id, text}[],
                  lostPropertyEmailId, provenance }
simpliquer      { hotstaff: {...}, partenaire: {...} }
```

`website` is the festival's public address, and it exists for exactly one reason: it is what a
shared Happening carries so the person receiving it has somewhere to go. Everything else the app
shows is for someone who already has the app; this is the one field written for someone who does
not. It lives in the content rather than in the binary so that the day a shared link becomes an
Android App Link, the path changes here and no release is needed.

`currentEditionId` is how a new edition ships without an app release: move it, and the app fetches
the new one.

`minSupportedAppVersion` is the escape hatch for a break that cannot be made additive. Below it the
app shows a soft update row in Plus and **never anything harder** — an unofficial festival app that
bricks itself on the Saturday afternoon is worse than one showing week-old data. It lives here
rather than in a separate manifest because this file is fetched first anyway. `null` means no
minimum is set, which is the normal state.

**Every section here is now modelled and read by a screen.** The French key names stay on the wire
and meet their English model names in exactly one place, the `@SerialName` pairs on `FestivalDto`:
`histoire` → `story`, `responsable` → `charters`, `transports` → `transport`, `paiement` → `payment`,
`besoin` → `assistance`, `simpliquer` → `involvement`.

The app reads a missing section as "not published" rather than as a broken file: a publish that drops
the transport block costs the visitor the transport screen, not the festival. `accessibilite` is
currently exercising exactly that — it is not published, so there is no screen, and the validator
errors if it reappears before there is something to put in it. See GAPS.md section 9.

### transports

```
Mode
  id          "bus" | "bus-nuit" | "voiture" | "velo-pied" | "nage"
  name        string                  the section heading, as read: "Venir en bus"
  body        string | null           prose, for a mode that is genuinely a sentence
  facts       Fact[]                  [] when the mode is prose
  links       { id, label, sublabel, url }[]
  departures  Departure[] | null      null on every mode but bus-nuit

Fact
  id      string
  text    string           "Lignes 701 et 705, arrêt Préverenges, Village"
  caveat  boolean          true draws ⓘ instead of ✓

Departure
  id     string
  night  string           "Vendredi"
  times  { time, note }[] "01:30", note usually null
```

**Facts rather than a paragraph, wherever the mode is really a list.** *Lignes 701 et 705* and
*cinq minutes à pied* were one sentence, and a sentence is what someone has to read to the end of
before they know whether it was theirs. The modes that are genuinely prose — the night bus, arriving
by water — keep `body` and publish no facts. A mode with neither is a heading with nothing under it,
and the validator errors on it.

**`caveat` is a boolean for the same reason `accepted` is on paiement.** A fact is either stated or
it is a warning about one; *places limitées* is not a thing the site offers, it is the thing that
will go wrong. Anything in between renders as a shrug.

**The mode ids are the sections, not the vehicles.** *À vélo, à pied* is one heading because the two
answers are the same answer, and the app never enumerates them: a shuttle laid on for one edition is
a content edit and no release.

`departures` is grouped **by night, not by row**: seven departures fit in four lines instead of
filling the screen, and the one that matters — the last bus with no onward connection to Lausanne —
carries a note rather than being buried in a list.

### paiement

```
Method  { id, name, accepted: boolean }
Note    { id, title, body, links: Link[] }
```

**`accepted` is a boolean, never "unknown".** A method nobody has confirmed is left out entirely
rather than rendered as a shrug — "TWINT: ?" helps no one, and a note or the FAQ can say it is being
checked.

**`headline` and `summary` are the answer, written.** A reader opening this screen has one question,
and deriving three words of French from a list of method names is how a screen ends up saying
something nobody wrote.

**Links belong to a note, not to the block.** That is the only arrangement that puts twint.ch under
*Vous n'avez pas TWINT ?* rather than in a bin of links at the foot of the page.

**`title` is required here and optional on the wire.** The app reads a note without one rather than
refusing `festival.json` — a required field nested in a section fails the whole file, so one late
heading would cost the visitor every screen — but a paragraph with no heading is one the reader has
to finish to know whether it was theirs, so the validator errors on it.

### accessibilite

**Not published, and not modelled.** The shape was decided before the content existed, and what
shipped was an empty list under a heading — a screen whose whole content was the admission that it
had none. The section, its DTO, its domain model and its screen are all out. What it should say when
it comes back is in GAPS.md section 9; the validator errors if the key reappears before then.

---

## `announcements.json`

```
schemaVersion   number
announcements   Annonce[]

Annonce
  id           string
  publishedAt  instant
  title        string
  body         string | null    null when the title says it all
  editionId    string | null    null when the annonce is about the festival, not one year
  url          string | null    null when the annonce is not tappable
  provenance   Provenance
```

**Its own file, not a section of `festival.json`.** This is the only content that needs to arrive
*during* the festival, when a correction is being pushed from a phone. Folded into `festival.json`
it would reupload history, contact and transport on every annonce, and a visitor's cached copy of
all of it would go stale together. Alone it is a few hundred bytes with its own ETag, which is what
makes polling it during LIVE affordable.

**`url` is a plain nullable link, not a typed internal action.** An earlier design had
`action: none | programme(day) | happening(id) | plus(entry) | url(external)` so an annonce could
deep-link into the app. That is more machinery than the job needs: an annonce is a dated record, and
the only thing it has to do is open somewhere. `null` simply means the card is not tappable. The
cost of the simpler form is real and worth naming — an annonce cannot send someone to a specific
fiche — but a broken deep link into a screen that has been renamed is a worse failure than a link
that goes to a web page.

**`editionId` scopes an annonce to one year.** An annonce naming an edition the app has not fetched
is dropped rather than rendered half-resolved; `null` means it is true of the festival itself and
survives every edition.

## `editions.json`

The list behind the archive entry in Plus, so the app can show which years exist without fetching
them all. **This is the only file fetched on demand**, and the only feature that does not work
offline. Nothing from an archive is cached: opening a past edition fetches it every time.

Back-filling is additive and safe — drop an `editions/2019/` folder, add a line here, ship nothing.
A past edition does **not** need to be complete to be worth having; a lineup and a poster is already
more than exists anywhere today. That is what `provenance: "archived"` is for.
