# Content gaps — 2026 edition

Everything below is missing from `festival.json` or `editions/2026.json` because it is not
published anywhere on yadlo.ch. This file is the list to put in front of the association; it is
not read by the app.

Each item says what is missing and what it costs. Anything marked **`provenance: "unverified"`**
in the JSON is either derived or guessed and should be replaced by a real answer.

---

## 1. Opening hours — the biggest hole

**The website never states when the festival opens or closes on any day.** Not on the home
page, not on `/activites`, not on `/a-propos`. The only site-wide time signal is a stale Wix
event page still showing the **2025** dates (`04 juil. 2025, 12:00 – 06 juil. 2025, 22:00`).

The `days[]` windows in `2026.json` are therefore **derived from the programme**, not published:
the earliest and latest slot on each day, plus a margin.

| Day | Derived window | Derived from |
|---|---|---|
| Vendredi 10 | 16:00 → 03:00 | first activity 16:00, Carlos Willenghton ends 01:30 |
| Samedi 11 | 10:00 → 03:00 | acro-yoga 10:00, Silent Party ends 02:00 |
| Dimanche 12 | 10:00 → 22:00 | yoga 10:00, Treehouse ends 21:00 |

**Why it matters more than it looks:** `FestivalDay` is a *window*, not a date. Those windows
decide which day a 01:30 set belongs to, when the app flips into LIVE, and what "the festival
opens in 3 hours" means on the home screen. Getting them wrong is wrong on every screen at once.

**The ask: 6 numbers.** Opening and closing time for each of the three days.

---

## 2. Restauration — `stands` is empty

`stands: []`. There is no food truck or bar listing on the site that could be scraped, and no
page for it in either sitemap.

This is the **most valuable content in the app** and the one thing that exists nowhere else — a
visitor currently cannot find out what there is to eat, or what anything costs, without walking
the site. Needed per stand:

- Name, what kind of food, and where it stands
- Opening windows per day
- The **menu**: groups (plats / menus / boissons), then items with a name and a price
- Dietary **marks** per item where they apply: végé, végan, sans gluten, sans lactose, piquant

Even a partial list — the bar and three trucks — is worth more than a complete programme.

---

## 3. Partner names

The tiers are confirmed and in the JSON (`Sponsors généraux`, `cygnes d'or`, `cygnes d'argent`,
`cygnes de bronze`, `Partenaires`), but every `members: []` is empty: on `/partenaires` the
partners exist **only as logo images** with no text, so there is nothing to read.

Needed: the name of each partner, its tier, and ideally a URL. Logos can stay images.

> The page renders all three cygne tiers as "800€", which is almost certainly a copy-paste
> error on the site rather than three tiers at one price. Worth confirming before it is
> reproduced in the app.

---

## 4. Prices not stated

Confirmed: Mini Escape Game and Trésor de Black Sam CHF 10/équipe · GladiaSUP and Trampoline
flottant CHF 5/personne · SUP Yoga CHF 10/personne · Air Track, Yoga, Acro-yoga free · Silent
Party CHF 25 adulte / CHF 15 moins de 16 ans, caution casque CHF 50.

**Missing:** Jeux de société, Chasse au trésor, Tournoi de UNO, Initiation salsa et bachata,
Initiation slackline. These are almost certainly free, but the app should not assert that —
"free" and "we don't know" are different claims. `Coin enfant` and `Diffusion de match` are
currently marked free with `provenance: "unverified"` for the same reason.

**Also missing: is entry itself free?** Nothing on the site says so either way.

---

## 5. Times missing for programmed activities

| Activity | Missing |
|---|---|
| Chasse au trésor | Runs "vendredi à dimanche" with no hours at all — three slots with `start: null` |
| Coin enfant | No hours on any day — three slots with `start: null` |
| Diffusion de match | Start times only (Fri 21:00, Sat 23:00), no end time |

Open-ended slots are legitimate in the model, but a slot with neither start nor end cannot be
placed on the Programme at all.

---

## 6. Three artists with no stage and no genre

`Gauthier Quenis` (dim. 12:00–14:30), `Refaire le monde` (dim. 14:30–15:30) and `Treehouse`
(dim. 19:30–21:00) are listed with times but **no stage and no genre**, so their slots carry
`venueId: null`. The names suggest these may not be music sets at all — "Refaire le monde"
reads like a talk. Confirm what they are and where they happen.

---

## 7. Two stages, which had not been assumed

The programme uses **two music venues, `Scène` and `TOTEM`**, not one. The domain model already
allows this — a single `Musique` lane can host slots at two venues — so nothing breaks, but any
decision that leaned on "there is only one stage" should be revisited.

---

## 8. Transport, payment, accessibility

All three are present in `festival.json` as empty structures with `provenance: "unverified"`,
because there is no page for any of them.

- **Transports** — which bus lines serve the beach, from Lausanne and from Morges, the last bus
  or night bus, where to park, where to leave a bike, and whether you can arrive by water.
- **Paiement** — cash, cards, or a festival token? Is there an ATM nearby? Is there a deposit
  on cups, and how much?
- **Accessibilité** — step-free access, accessible toilets, and who to write to in advance.
  Splitting this into "confirmed" and "to confirm" is better than silence; an empty
  accessibility page tells a wheelchair user nothing.

---

## 9. Which year is the site actually showing?

Mixed signals worth resolving, because it decides whether `2026.json` is a *record* or a *plan*:

- `/silent-party` and `/benevoles` both clearly say **10–12 juillet 2026**. ✅ used
- `/events/yadlo` still shows **04–06 juil. 2025**, evidently not updated.
- The match screening description names a **Norway–England** fixture, which was a 2025
  tournament match — so that line may be left over from the previous edition.

If any of the programme in `2026.json` is actually 2025 content that was never refreshed, it
needs flagging before it becomes the archive record for 2026.

---

## 10. Past editions

`editions/index.json` lists only 2026. The festival has run since **2015**, and browsing past
line-ups is a stated goal. Any archive material — line-ups, posters, photos, attendance figures
per year — can be back-filled one file at a time without touching the app.
