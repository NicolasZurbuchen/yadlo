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
at 12:00, on the public beach. Both slots now carry `"beforeOpening": true`, and the validator
**errors** on any unflagged slot that starts before opening — so the deliberate case is declared
in the data and the accidental case still gets caught.

> This is why `FestivalDay.start/end` and `FestivalDay.opening` are separate fields. The window
> stretches to 10:00 to contain the yoga; the opening hours stay at 12:00 because that is what a
> visitor needs to be told.

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

## 2. Restauration — six stands in, four without a menu

The festival does have a restauration page; it is simply not findable, which is why it was missed
on the first pass. Six stands are now recorded:

| Stand | Cuisine | Links | Menu |
|---|---|---|---|
| Vegan Fabrik | végétale, `végan` + `bio` | ✅ | 11 items, all prices **vendor-sourced** |
| De l'Or Bokit | guadeloupéenne | ✅ | 4 bokits + 3 sauces, from their own carte |
| Man'ouchy | libanaise | ✅ | ❌ — their site keeps the carte in PDFs |
| Guliko | géorgienne | ❌ | ❌ |
| Gyros Greek & La Focacceria | méditerranéenne | ❌ | ❌ |
| Surf Shop | crêpes, gaufres, glaces | ❌ | ❌ |

**Still needed:**

1. **Menus for the four without one.** Man'ouchy's exists as a PDF — a link, or the plain text,
   would do. The other three have nothing anywhere.
2. **Opening hours for all six.** A Stand's Slots are its opening windows, and not one is
   published, so every stand currently has none. Are they simply open whenever the site is?
3. **Prices confirmed for Yadlo.** Everything priced is marked `unverified` because none of it
   comes from the festival — see below.
4. **Links for Guliko, Gyros Greek & La Focacceria, and Surf Shop.**

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

### Le Totem and the restauration colour

Stands use `category: "restauration"`, a sixth category beside the five that drive the palette.
Stands never appear on the Programme, so this does not need a sixth measured palette colour —
the brand primary `#14618F` is the natural choice for a stand fiche. **Worth confirming** before
the fiche template is built.

## 3. Le Totem

Recorded as a venue: a climbing wall for children. `/activites` separately describes the `Coin
enfant` as including "un mur de grimpe miniature".

**The ask:** are these the same structure? Is Le Totem a programmed Activity with its own hours,
or simply part of the Coin enfant? Nothing is scheduled there today, so it is currently a place
on the map and nothing more.

## 4. Partner names

Tiers are confirmed and in the JSON (`Sponsors généraux`, `cygnes d'or`, `cygnes d'argent`,
`cygnes de bronze`, `Partenaires`), but every `members: []` is empty: on `/partenaires` the
partners exist **only as logo images** with no text.

Needed: the name of each partner, its tier, ideally a URL. Logos can stay images.

> The page renders all three cygne tiers as "800€", which is almost certainly a copy-paste error
> rather than three tiers at one price. Worth confirming before it is reproduced in the app.

## 5. Prices not stated

Confirmed: Mini Escape Game and Trésor de Black Sam CHF 10/équipe · GladiaSUP and Trampoline
flottant CHF 5/personne · SUP Yoga CHF 10/personne · Air Track, Yoga, Acro-yoga free · Silent
Party CHF 25 adulte / CHF 15 moins de 16 ans, caution casque CHF 50.

**Missing:** Jeux de société, Chasse au trésor, Tournoi de UNO, Initiation salsa et bachata,
Initiation slackline. Almost certainly free, but the app should not assert it — "free" and "we
don't know" are different claims. `Coin enfant` and `Diffusion de match` are marked free with
`provenance: "unverified"` for the same reason.

**Also: is entry itself free?** Nothing on the site says so either way.

## 6. The website's artist page is stale — how stale?

Instagram and `/artistes` disagree, and Instagram wins on every point checked:

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

## 7. Artist photos

Every artist has links and text but **no image**. `/artistes` carries press photos as page
assets — filenames like `AMC.jpg`, `AlbertChinet_PhotoPresse.jpg`, `Carlos.JPG`, `dubside.jpg` —
which suggests the association holds proper press kits.

The fiche design puts a photo behind a collapsing toolbar, so this is the single most visible
piece of missing content after the food trucks. Needed: one landscape-ish image per artist, plus
whatever credit the photographer requires.

## 8. Times missing for programmed activities

| Activity | Missing |
|---|---|
| Chasse au trésor | Runs "vendredi à dimanche" with no hours at all — three slots with `start: null` |
| Coin enfant | No hours on any day — three slots with `start: null` |
| Diffusion de match | Start times only (Fri 21:00, Sat 23:00), no end time |

Open-ended slots are legitimate in the model, but a slot with neither start nor end cannot be
placed on the Programme at all.

## 9. Transport, payment, accessibility

All three sit in `festival.json` as empty structures with `provenance: "unverified"`, because
there is no page for any of them.

- **Transports** — which bus lines serve the beach, from Lausanne and from Morges, the last bus
  or night bus, where to park, where to leave a bike, whether you can arrive by water.
- **Paiement** — cash, cards, or a festival token? An ATM nearby? A deposit on cups, and how
  much?
- **Accessibilité** — step-free access, accessible toilets, and who to write to in advance.
  Splitting this into "confirmed" and "to confirm" beats silence; an empty accessibility page
  tells a wheelchair user nothing.

## 10. The match screening may be 2025 content

`/activites` describes the Saturday screening as **Norway–England**, which was a 2025 tournament
fixture. Given gap 6, this line is likely left over. Both match slots are `unverified`.

## 11. Past editions

`editions/index.json` lists only 2026. The festival has run since **2015**, and browsing past
line-ups is a stated goal. Any archive material — line-ups, posters, photos, figures per year —
can be back-filled one file at a time without touching the app.
