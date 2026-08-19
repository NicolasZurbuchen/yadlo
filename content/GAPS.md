# Content gaps — 2026 edition

Everything below is missing from `festival.json` or `editions/2026.json`. This file is the list
to put in front of the association; it is not read by the app.

Anything marked **`provenance: "unverified"`** in the JSON is derived or guessed and should be
replaced by a real answer. Run `node content/validate.js` after any edit.

---

## ✅ Resolved — opening hours

Confirmed from the festival's Instagram and now in `days[].opening`:

| Day | Site open |
|---|---|
| Vendredi 10 | 16:00 – 02:00 |
| Samedi 11 | 12:00 – 03:00 |
| Dimanche 12 | 12:00 – 22:00 |

These are **not** the same as the `FestivalDay` window, and the difference is real rather than
pedantic — see gap 1 below.

## ✅ Resolved — the music programme

The Instagram schedule is fuller and partly contradicts the website. **Instagram was taken as
authoritative.** See gap 6 for what that implies about the site.

---

## ✅ Resolved — yoga before opening, and Léman Records

`acro-yoga` (samedi 10:00) and `yoga-plage` (dimanche 10:00) genuinely run before the site opens
at 12:00, on the public beach.

> **Corrected.** An earlier draft of this section described a `beforeOpening` flag on the slot and a
> separate `FestivalDay.opening` object. **Neither shipped, and neither exists in the JSON.** A
> FestivalDay's `start`/`end` *are* the opening hours — one pair of instants — and the validator
> errors if an `opening` key appears. A Slot outside the window is simply legal and merely worth
> flagging, so the four early slots produce a *warning* rather than needing a flag to silence them.
> That is deliberate: the case is real, but next year one of them may be a mistake.

`Léman Records` is confirmed as the correct spelling.

---

## ✅ Resolved — artist links, spellings and descriptions

**All 13 artists** now carry links and a description. Link types in use: `spotify`, `instagram`,
`website`, `soundcloud`, `bandcamp`, `facebook`, `youtube`, `tiktok`, `beatport`, `appleMusic`.

The links settled all three open spellings, because an artist's own handle is the authority —
and each source turned out to be right about one and wrong about another:

| | Website | Instagram | Settled by | Correct |
|---|---|---|---|---|
| Quenis | GAUTHIER | GAUTIER | `@gauthier_quenis` + his booking address | **Gauthier** |
| Willengton | WILLENGHTON | WILLENGTON | `@dj_carlos_willengton`, TikTok, YouTube | **Willengton** |
| Tree House | TREEHOUSE | TREE HOUSE | bio reads "Tree House Music Collective" | **Tree House** |

Descriptions were written from the artists' own pages where there was anything to read. Two of
them are only lightly sourced and worth a glance before they ship:

- **JAYJAY** — his Instagram bio is nothing but four tags (`@collectif52`, `@jdl.morges`,
  `@gare.z_lausanne`, `@swissvinoclub`), so "lié au Collectif 52 et aux scènes de Morges et de
  Lausanne" is inference from those tags, not a statement he has made.
- **Tree House** — "en club comme en festival" is drawn from story highlights named after
  venues. Reasonable, but not a claim they wrote down.

Two artists have no readable bio anywhere and their descriptions come only from confirmed facts:
**AMC** (SoundCloud blocks fetching; the text is the website's) and **Thalassothérapie** (same).

## 2b. The bar does not exist as a stand

The drinks suppliers — Vivi Kola, Kosmos, SuperNaturalClub, Grano Maté, BroCello, Swan,
Wineatypic — are recorded as **partners**, which is how the association presented them and
presumably reflects that they supplied stock for free.

But the post describing them reads like a drinks list, not a sponsor list: each entry says what
it tastes like and when to drink it, split into *côté softs* and *côté apéros & cocktails*. As
partner logos that content dies. As a **bar menu** it answers "what can I drink", which is the
second question anyone asks after "what can I eat".

**There is no `bar` stand yet**, and the festival plainly has one — 3 200 litres of beer in the
closing figures, `fnb@yadlo.ch` in the contacts, and free bar drinks among the Hot'Staff perks.
These suppliers should end up in both places. Needed: the bar's name, where it stands, its hours,
and what it pours at what price.

## 2. Restauration — six stands, six menus, none of them confirmed

The festival does have a restauration page; it is simply not findable, which is why it was missed
on the first pass. All six food stands now carry a menu with ingredients and dietary marks, so the
filter on *Nourriture & boissons* has something to filter on. **Not one of those menus comes from
the festival**, and they are unreliable in three different ways, which each group's `source` says:

| Stand | Cuisine | Links | Menu | How reliable |
|---|---|---|---|---|
| Vegan Fabrik | végétale | ✅ | 11 items | Vendor's own list and a photo of their board |
| De l'Or Bokit | guadeloupéenne | ✅ | 4 bokits + 3 sauces | Vendor's own published carte |
| Man'ouchy | libanaise | ✅ | 14 items | **Reconstructed** from their restaurant carte |
| Guliko | géorgienne | ❌ | 13 items | **Reconstructed** from the dishes they list, no prices published |
| Gyros Greek & La Focacceria | méditerranéenne | ❌ | 9 items | **Plausible only** — nothing is published |
| Surf Shop | crêpes, gaufres, glaces | ❌ | 8 items | **Plausible only** — nothing is published |

The last four are the ones to put to the association first: they are readable, they are priced,
and they are invented. Better a real carte with four dishes than a convincing one with fourteen.

**Still needed:**

1. **The real menus**, for all six. Man'ouchy's exists as a PDF on their own site — a link or the
   plain text would do. Guliko lists its dishes but no prices. The other two publish nothing.
2. **The dietary marks confirmed.** Every dish is now tagged `vegan`, `vegetarien`,
   `sans-gluten`, `sans-lactose` or `piquant` from its ingredients, and those tags are what the
   filter answers with. They are inferences, and a wrong one on a menu is worse than a wrong price.
3. **Is Man'ouchy halal, and is anyone else?** Every dish on their carte now carries `halal` — a
   Lebanese kitchen sourcing from a halal butcher is the ordinary case rather than a special
   claim, so it is the plausible one to write, and writing it on the whole carte is what makes
   the stand answer *tout halal* rather than *options halal*.

   It is still **an invention**. It is also the one mark on this list that cannot be inferred
   from a list of ingredients, and the one a reader relies on for reasons a wrong answer does not
   repair. **Confirm it with the vendor or take it out before anyone acts on it.**
4. **Opening hours for all six.** A Stand's Slots are its opening windows, and not one is
   published, so every stand currently has none. Are they simply open whenever the site is?
5. **Prices confirmed for Yadlo.** Everything priced is marked `unverified` — see below.
6. **Links for Guliko, Gyros Greek & La Focacceria, and Surf Shop.**

### Why every price is `unverified`

Two different weaknesses, both short of "confirmed by the organisers", and each item records
which one applies in its group's `source`:

- **De l'Or Bokit** — from the carte published on `delorbokit.ch`. Reliable as *their* pricing,
  but it is their Lausanne carte, not necessarily what they charge at a festival.
- **Vegan Fabrik** — the three plats came from an unattributed list; the eight other items were
  **read off a photograph of a handwritten chalkboard of unknown date**.

One reading in that photo is genuinely uncertain: a `Fr. 4.50` sits between `Mini pizza Fr. 5.00`
and `Lassi`, and it is recorded as the **Lassi's** price on the basis that the prices are
right-aligned. If it belongs to the pizza instead, then the pizza is 4.50 and the lassi has no
price. The four tartelette prices are solid — a second photo shows the same figures on the
individual labels.

### The restauration colour

Stands use `category: "restauration"`, a sixth category beside the five that drive the palette.
Stands never appear on the Programme, so this does not need a sixth measured palette colour —
the brand primary `#14618F` is the natural choice for a stand fiche. **Worth confirming** before
the fiche template is built.

## ✅ Resolved — the two children's spaces

Le Totem and the Coin enfants are **separate activities**, both free, and both now have hours.
Le Totem is the name of a bouldering gym, Totem Escalade, which is also a festival partner.

| | Vendredi | Samedi | Dimanche | |
|---|---|---|---|---|
| Le mur de grimpe | 16:00–20:00 | 10:00–18:00 | 10:00–18:00 | gratuit jusqu'à 16 ans |
| Le coin enfants | 16:00–18:30 | 12:00–18:00 | 12:00–18:00 | 4 à 12 ans, 2 h maximum |

The Coin enfants is supervised by volunteers and children can be left there, which is why it
carries an age range and a maximum duration where nothing else does. Both are `enfants`.

## ✅ Resolved — partners

All 39 partners are in, across six tiers. **34 have a verified website**; the logos on
`/partenaires` carry no hyperlinks at all, so each was found and confirmed by loading the site
and reading its title.

**Closed — no website exists. Do not search for these again:**

| Partner | Tier | |
|---|---|---|
| Edifice | cygnes d'or | No findable site and the logo matches nothing. `EDIFEA SA` exists in Vaud but is a different company and was not assumed. |
| GladiaSUP | partenaires | Not a company with a web presence — it is the SUP obstacle course, an activity of the festival that also appears in the partner list. |

**Three name corrections:** the list read *Garno Maté* → **Grano Maté**, a Vevey maté brewer;
*Clash Solutions* → **Cash Solutions** (`cash-solutions.ch`), which is why it could not be found;
and *SuperNaturalClub* → **Super Natural Club**, which is how their own site writes it.

**The last three URLs were supplied and are now in.** Two were verified by loading the site:
**Super Natural Club** (`supernaturalclub.ch`, a Lausanne kombucha and soft-drink maker) and
**SwanWine** (`swanwine.ch`, a Swiss winemaker — the entry read *Swan* and is now the full name).

**Winatypic** is the third, and its name was the thing that was wrong: the list read *Wineatypic*,
and the correct spelling is **Winatypic** — id and name both corrected. Its supplied URL returns
**404** on the apex and on `www`, so **`url` is null**: a logo that opens a 404 is worse than one
that opens nothing, now that a missing url shows a toast instead. If a working address turns up it
is a one-line edit.

**All 39 partners are now settled** — 34 with a verified website, 5 deliberately without one.

> **Cham Properties was withheld in error.** It really is Cham Swiss Properties AG, the listed
> Zug firm — the reasoning that a CHF 1.7 bn company would not sponsor a Préverenges beach
> festival at bronze tier was wrong, and it is now linked. Its URL is stored as the site root
> rather than the `/en` path it was supplied as, on the same rule that strips Spotify's
> `/intl-ja/`: a locale in a stored URL is the collector's browser setting, not the address.

> **The "800" on the cygne tiers is not a price and not an error.** It refers to Préverenges'
> 800th anniversary. Nothing to reproduce, nothing to confirm — the earlier note read it as a
> copy-pasted sponsorship amount and was simply wrong.

**Partners are not cross-linked to Happenings, by decision.** Several partners are also festival
content — Surfshop Préverenges is both a stand and a partner, Totem Escalade runs the mur de grimpe,
GladiaSUP is an activity. An earlier note proposed a `happeningId` on the partner so a fiche could
credit its supplier. That is not wanted: it adds a relationship to maintain for a line of text
nobody asked for. **This section is closed.**

## ✅ Resolved — activity prices

**Every activity now has a price.** The association's activity posters carry a price when there
is one and nothing when there is not, so a poster with no price is a statement that the activity
is free, not an omission. That converted the last five unknowns into confirmed free: Jeux de
société, Chasse au trésor, Tournoi de UNO, Salsa et bachata, Slackline.

| | |
|---|---|
| Free | Coin enfants · Mur de grimpe · Jeux de société · Chasse au trésor · Tournoi de UNO · Salsa et bachata · Slackline · Air Track · Yoga · Acro-yoga |
| CHF 5 / personne | GladiaSUP · Trampoline flottant |
| CHF 10 / personne | SUP Yoga |
| CHF 10 / équipe | Mini Escape Game · Trésor de Black Sam |
| CHF 25 / 15 | Silent Party, plus a CHF 50 headset deposit |

**Closed.** `Diffusion de match` had no poster of its own, so the missing-price rule could not be
applied to it and it sat at `unverified`. It is confirmed free directly, and now reads `confirmed`
like the rest. **Every activity price in the edition is now confirmed.**

> The shape of every price also changed — one structure for all seventeen, free or not. See
> [SCHEMA.md](SCHEMA.md) § activity payload.

## ✅ Resolved — entry is free, and it exposed a missing screen

**Entry to Yadlo is free, all three days.** Recorded as `entry.free` on the Edition rather than
in `festival.json`, so that a later edition charging admission cannot retroactively rewrite what
2026 cost.

The interesting part is that **no mock had anywhere to put this**. The plainest question a
first-time visitor asks — *is it free?* — had no home in Accueil, Programme, Mon Yadlo or Plus.
That is the whole problem in miniature: the association's information is split across a stale
website and a live Instagram, so the ordinary questions have no single place to live.

Hence a **FAQ in Plus › Sur place**, now in `festival.json` as a `faq` list and recorded in
SPEC.md. It has one entry so far. Candidates that need an answer before they can be added:

- Peut-on payer en carte, ou faut-il des espèces ? Y a-t-il un bancomat à proximité ?
- Les chiens sont-ils admis ?
- Peut-on se baigner pendant le festival, et la plage est-elle surveillée ?
- Y a-t-il des toilettes, des douches, des vestiaires ? De l'eau potable gratuite ?
- Peut-on apporter sa nourriture ou ses boissons ?
- Que se passe-t-il en cas de pluie ?
- Y a-t-il des consignes ou un vestiaire pour les sacs ?

> The entry answer exists twice — as prose in `faq` and as `entry.free` on the Edition. Keep them
> in step, and prefer the structured field wherever a screen can use it.

## ✅ Resolved — Instagram is the source of truth

**The website is maintained in a way nobody can rely on; Instagram is current and complete.**
Everything needed has been gathered from posts, and that is where it should be gathered from in
future. `/artistes` should be treated as unreliable, not merely lagging.

The consequence is worth stating plainly: **there is no feed to scrape**, so these files are
hand-transcribed from Instagram posts and will stay that way until the association adopts them as
their own source. That is also the best argument for the app becoming official — the JSON would
stop being a copy of their communication and start being what their communication comes from.

The original evidence, kept because it is the reason:

| | `/artistes` | Instagram |
|---|---|---|
| DJ ALF, CÆSURE, JAYJAY | absent | three real Friday sets |
| Diggin' | Saturday 20:00 | **Friday** 20:00 |
| Gautier Quenis | 12:00–14:30 | 12:00–15:00 |
| Refaire le monde | 14:30–15:30 | 15:30–16:30 |
| Dubside | "Pop déviante" | "Techno-house" |
| Albert Chinet | "Néo-soul" | "Chanson française et pop moderne" |

Three whole artists missing and one on the wrong day is not drift, it is a page nobody updated.

**The ask:** should `/artistes` be treated as unreliable in general? If the association's real
working copy of the programme lives somewhere else — a spreadsheet, a Notion page, the Instagram
drafts — that is the thing the app should be fed from.

## 7. Images — the artists are done, the stands are not

**16/38 happenings now carry a photo: all thirteen artists, and three activities** — GladiaSUP, la
Silent Party and le yoga. They were taken from the association's own channels, cleaned, cropped to
one size and converted to WebP. **22 remain**: every stand, and every activity but those three.

**All 39 partner logos are in the picture bank and none of them is referenced yet.** The files are
there under `shared/logos/`; `logo` is still `null` on every partner, so the validator reports each
file as unreferenced. That is a wiring job rather than a content gap — eleven of the filenames do
not match their partner id (`arenaz-automobile` against `arenaz`, `morges` against `morges-region`,
`volta` against `volt-a`), so the mapping wants an eye rather than a loop.

| Field | On | Shape |
|---|---|---|
| `images` | every happening — artist, activity, stand | `[{ "src": …, "credit": … }]`, empty when unknown |
| `logo` | every partner | a single `src`, or `null` |

**A `src` is either an absolute `https://` URL, or a path relative to the content root** —
`shared/images/artists/alf.webp`. There is no `imageBaseUrl`: the app already knows the address it
fetched the bundle from, and declaring it again in the content would be the same fact written twice.
The picture bank lives under `shared/` rather than under an edition because a photo of an artist who
plays two years running is one file, not two.

**Photos and logos are deliberately different fields.** A photo gets cropped into a collapsing
toolbar behind a scrim; a logo must never be cropped, tinted or bled to an edge. Same data type,
opposite handling, so they do not share a name.

`credit` exists because press photos usually carry a photographer's condition. It is `null` on all
nineteen files currently in the bank, none of which arrived with one.

**What is still needed:**

- **One photo per stand.** The stand list is the screen where a photo does most work: people choose
  food by looking at it, and this is now the largest remaining hole.
- **One photo per activity**, for the ten that have none.
- **The partner logos wired up**, which is a mapping rather than an ask.

## ✅ Resolved — every Slot now has a start and an end

`Diffusion de match` runs **two hours** per screening: Friday 21:00–23:00, Saturday 23:00–01:00.
The Saturday one crosses midnight. Its price is confirmed free on the same rule as the other
activities, and it is no longer `unverified`.

**Chasse au trésor now carries its day's opening hours.** Its poster states the days —
vendredi, samedi, dimanche — and deliberately gives no hours, because the clues sit around the
festival for its whole duration. All-day Slots were rejected as a model (DECISIONS.md §
Settled), so the three slots are written as 16:00–02:00, 12:00–03:00 and 12:00–22:00 with
`provenance: "unverified"` — the days are confirmed, the hours are derived. The validator now
**errors** on a null `start` or `end`, so the state cannot come back.

**Still worth asking the association**, because it would turn three inferred instants into
published ones: *la chasse au trésor a-t-elle des horaires précis, ou tourne-t-elle pendant
toute l'ouverture du festival ?* Either answer is a content edit; nothing in the app changes.

## ✅ Resolved — opening hours are real but soft, and the app says so

**There are no gates.** Entry is free and the plage de Préverenges is a public beach, so the site
is physically open at any hour; the published times are when the stands, bars and animations are
running. That is why four Slots legitimately start at 10:00 on days the festival "opens" at
12:00 — the morning yoga on the open beach, and the mur de grimpe, which is inside the festival
and is the genuinely odd one.

**The times stay as published.** What the app adds is honesty rather than a correction: the
Edition carries an `openingNote` for the Horaires screen —

> L'entrée est libre et la plage de Préverenges est publique : on peut s'y trouver à toute heure.
> Les horaires ci-dessus sont ceux du festival — quand les stands, les bars et les animations
> tournent. Quelques activités commencent avant, comme le yoga du matin et le mur de grimpe.

The validator keeps warning on those four Slots. That is deliberate: the note explains the
situation to a visitor, but whoever edits this file should still be told when a Slot falls
outside the hours, because next year one of them may be a mistake.

## ✅ Resolved — transports and paiement

Both are filled in, from the text already written against them in the Plus mockup, so the screens
designed around them still hold.

**Transports.** All six modes carry prose. The night buses carry structure instead of a paragraph:
`departures` groups them **by night rather than one row per bus**, so seven departures fit in four
lines, and the 03:00 with no onward connection to Lausanne carries a note rather than being buried
in a list. Every other mode carries `departures: null`, so there is one shape to read.

> Two link gaps remain and are guesses nobody should make up: the **MBC timetable PDFs** for lines
> 701 and 705, and the **parking plan**. Both are stored as empty link arrays rather than invented
> URLs. A shuttle laid on for one edition would not belong here anyway — that goes on the Edition.

**Paiement.** `carte`, `TWINT` and `espèces`, the last as `accepted: false`.

> **Apple Pay and Google Pay are deliberately not methods**, though the mockup listed them.
> Contactless wallets almost certainly work wherever the cards do, but "almost certainly" is not
> what a list of accepted methods claims, and the rule here is that an unconfirmed method is left
> out rather than rendered as a shrug. It is stated in a `note` instead, which is free text and does
> not pretend otherwise. Still worth asking: is there a **bancomat** nearby, and is there a
> **deposit on cups** and how much?

## 9. Accessibilité — withdrawn until there is something to say

**This is the one gap that shipped as a screen, and it should not have.** The block was published as
an empty list with a contact address, and the screen built around admitting it knew nothing. That is
defensible as a promise and indefensible as an answer: someone deciding whether to travel thirty
kilometres to a beach opened a page that told them to write an email. The section, its DTO, its
domain model, its use case and its screen are out of the app as of this branch, and `validate.js`
errors if the key comes back before the content does.

**It comes back.** Not as a shape waiting to be filled, but the day the association confirms or
denies actual facts. The shape it had was right — `{ id, name, available, note }`, same rule as
paiement, and **recording what is *not* available matters as much as what is**: "no accessible
toilets" is something a person needs before deciding to travel, and silence tells them nothing.

Candidates to confirm or deny: step-free access to the site, accessible toilets, a PMR parking
space, what the ground is actually like (sand, grass, gravel — this is a beach), free entry for a
companion, and whether the stage area has a viewing spot. Worth asking the association directly
rather than waiting for them to publish — it is the single question here most likely to decide
whether somebody comes at all.

## ✅ Resolved — the match screening is 2026, not leftover

The screenings are **Coupe du monde 2026** fixtures. The Friday quarter-final and the Saturday
match are current content, not the stale 2025 line they were suspected of being. Both slots are
`confirmed`.

## ✅ Resolved — past editions

`editions.json` is **the list behind the archive entry in Plus**, so the app can show which years
exist without fetching all of them. It is the only file the app reads on demand rather than at
launch, and the only feature that does not work offline.

**Nothing from an archive is stored.** No caching, no images kept, no rows written — opening a past
edition fetches it every time, and closing the app leaves nothing behind. That is a deliberate
asymmetry with the current edition, which is cached hard precisely because it has to work in a field
with no signal. Browsing 2019 is a November sofa activity: the network is there, and paying disk for
it would mean an archive that silently goes stale with no way to notice.

It lists only 2026. The festival has run since **2015**, and browsing past line-ups is a stated
goal. Back-filling is additive and safe: drop an `editions/2019/` folder next to `2026/`, add a
line to `editions.json`, and nothing else changes. No app release needed.

Worth knowing before back-filling: a past edition does **not** need to be complete to be worth
having. A line-up and a poster is already more than exists anywhere today. `provenance:
"archived"` is there precisely for this — data taken from a past edition's record rather than
confirmed fresh.

## 10. Annonces — ten of the thirteen are invented

`announcements.json` was carrying three annonces, which is not enough to see what the list looks
like when it is a list. Ten more were written so the feed scrolls and the *Toutes* screen has a
reason to exist.

**Every one of them is fiction**, marked `provenance: "unverified"` like the three that were
already there. They are plausible rather than arbitrary — dates announced in January, a call for
Hot'Staff in March, first names in April, the bus pyjama in May, a full parking on the Saturday
afternoon — but nothing in them was published by the association, and the app does not currently
show provenance on an annonce the way it does on a price.

**These must be deleted or replaced before the app is put in front of anyone.** They are the one
piece of invented content that reads as if the association said it, which is a different thing
from a guessed price: a wrong price is wrong, and a wrong annonce is somebody else's voice.

What is worth keeping from them is the shape. Between them they cover every case the screen has to
survive: an annonce with a URL and one without, one that is a single sentence, one pushed *during*
the festival as a correction (`parking-2026`, 14:20 on the Saturday), and a run long enough that
Accueil's two-item summary is visibly a summary.
