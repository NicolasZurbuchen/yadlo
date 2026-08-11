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

## 4. Partners — 5 websites of 33 still missing

All 33 partners are in, across six tiers (a `soutien-public` tier was added for the two public
bodies). **28 have a verified website**; the logos on `/partenaires` carry no hyperlinks at all,
so each was found and confirmed by loading the site and reading its title.

**Not found — likely no website:**

| Partner | Tier | Note |
|---|---|---|
| Edifice | cygnes d'or | No match. `EDIFEA SA` exists in Vaud — a different name, not assumed. |
| GladiaSUP | partenaires | Runs the SUP course at the festival; no independent site found. |

**Two name corrections:** the list read *Garno Maté* → **Grano Maté**, a Vevey maté brewer; and
*Clash Solutions* → **Cash Solutions** (`cash-solutions.ch`), which is why it could not be found.

> **Cham Properties was withheld in error.** It really is Cham Swiss Properties AG, the listed
> Zug firm — the reasoning that a CHF 1.7 bn company would not sponsor a Préverenges beach
> festival at bronze tier was wrong, and it is now linked. Its URL is stored as the site root
> rather than the `/en` path it was supplied as, on the same rule that strips Spotify's
> `/intl-ja/`: a locale in a stored URL is the collector's browser setting, not the address.

**Three partners are also festival content**, worth knowing before the fiche and partner screens
are built, since they should probably cross-link:

- **Surfshop Préverenges** is at *Avenue de la Plage 1* — the festival's own address. The
  partner and the `Surf Shop` food stand are the same business.
- **Totem Escalade** runs the mur de grimpe.
- **Sherlock Events** is a mobile escape-game company in Morges, almost certainly the provider of
  the Mini Escape Game and Le trésor de Black Sam. **Summit Video** rents giant screens — the
  Diffusion de match. Neither is stated; both are worth confirming.

> The page renders all three cygne tiers as "800€", which is almost certainly a copy-paste error
> rather than three tiers at one price. Worth confirming before it is reproduced in the app.

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

**One left:** `Diffusion de match` is marked free with `provenance: "unverified"` — it has no
poster of its own, so the rule above cannot be applied to it.

## 5. Is entry to the festival free?

Nothing on the site or in any post says so either way, and it is the single most-asked question
about any festival. Everything inside is now priced; the gate is not.

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

## 7. Images — the fields exist, nothing fills them

**36/36 happenings have no image and 33/33 partners have no logo.** The fields are in place, so
this is now purely a matter of supplying files.

| Field | On | Shape |
|---|---|---|
| `images` | every happening — artist, activity, stand | `[{ "src": …, "credit": … }]`, empty when unknown |
| `logo` | every partner | a single `src`, or `null` |

**A `src` is either an absolute `https://` URL, or a path relative to the edition's
`imageBaseUrl`.** That field is `null` today because hosting is not settled, and while it is null
only absolute URLs are accepted — the validator says so rather than letting a relative path sit
there resolving against nothing. Setting `imageBaseUrl` once turns every path short and lets the
whole image set move host by editing one line.

**Photos and logos are deliberately different fields.** A photo gets cropped into a collapsing
toolbar behind a scrim; a logo must never be cropped, tinted or bled to an edge. Same data type,
opposite handling, so they do not share a name.

`credit` exists because press photos usually carry a photographer's condition. It is optional and
`null` today.

**What is needed:**

- **One photo per artist.** `/artistes` carries press photos as page assets — `AMC.jpg`,
  `AlbertChinet_PhotoPresse.jpg`, `Carlos.JPG`, `dubside.jpg` — so the association holds proper
  kits. The fiche puts this behind a collapsing toolbar, making it the most visible gap after the
  food trucks.
- **One photo per activity and per stand.** The stand list is the screen where a photo does most
  work: people choose food by looking at it.
- **A logo per partner.** These already exist on `/partenaires`, as images with no text — the
  same reason the names had to be supplied by hand.

## 8. Times missing for programmed activities

Only `Diffusion de match` now: start times (Fri 21:00, Sat 23:00) but no end.

**Chasse au trésor is no longer a gap.** Its poster states the days — vendredi, samedi,
dimanche — and deliberately gives no hours, because the clues sit around the festival for its
whole duration. The three slots keep `start: null` and are now `provenance: "confirmed"`: the
absence of times *is* the published fact, not missing data.

**Whether the app should keep it that way is an open decision, not a content gap** — see
SPEC.md § Open questions, item 0. The alternative is to treat "all day" as the day's opening
hours, which removes a nullable time and a second layout from every screen that formats a time.
The validator still warns on the three slots, correctly, because the app has to decide what to
do with them rather than silently drop them.

## 8b. Does the site really open at 12:00 on Saturday and Sunday?

This is worth a second look. **Four slots now start at 10:00 on days the published opening is
12:00** — acro-yoga (sam), yoga (dim), and the mur de grimpe on *both* days.

One activity before the gates is easy to believe on a public beach. Three, on a wall that needs
staffing by Totem Escalade, is a pattern rather than a coincidence. Either the published
"horaires d'ouverture du site" means something narrower than it sounds — the bars and food, say —
or the site genuinely opens at 10:00 and the 12:00 is wrong.

All four carry `"beforeOpening": true` and the data is internally consistent either way. But the
answer changes what the Horaires screen tells someone planning their Saturday morning.

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
