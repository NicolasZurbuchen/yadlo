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

## 1. Two activities start before the site opens

`acro-yoga` runs Saturday 10:00–11:00 and `yoga-plage` Sunday 10:00–11:00, but the site opens at
12:00 on both days. The validator flags both.

Plausible as-is: the beach at Préverenges is public, so a morning yoga session two hours before
the gates is perfectly coherent. But it could equally be a stale time on `/activites`.

**The ask:** are the yoga sessions genuinely before opening, on the open beach — or should they
read 12:00?

> This is why `FestivalDay.start/end` and `FestivalDay.opening` are separate fields. The window
> has to stretch to 10:00 to contain the yoga; the opening hours stay at 12:00 because that is
> what a visitor needs to be told.

## 2. Restauration — `stands` is still empty

`stands: []`. No food truck or bar listing exists on the site, and no page for one in either
sitemap.

This is the **most valuable content in the app** and the one thing that exists nowhere else. A
visitor cannot currently find out what there is to eat, or what it costs, without walking the
site. Needed per stand:

- Name, kind of food, and where it stands
- Opening windows per day
- The **menu**: groups (plats / menus / boissons), then items with a name and a price
- Dietary **marks** where they apply: végé, végan, sans gluten, sans lactose, piquant

Even the bar plus three trucks is worth more than a complete programme.

**If this only ever existed on a printed board at the festival, say so** — that changes the job
from scraping to authoring, and it should start now rather than in June.

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

## 7. Spellings to confirm

The two sources disagree, and these become permanent ids and screen titles:

| Website | Instagram | Used |
|---|---|---|
| GAUTHIER QUENIS | GAUTIER QUENIS | **Gautier Quenis** (Instagram) |
| CARLOS WILLENGHTON | CARLOS WILLENGTON | **Carlos Willengton** (Instagram) |
| LÉMAN RECORDS | LEMAN RECORD | **Léman Records** (website — the accent and plural look right for a label name) |
| TREEHOUSE | TREE HOUSE | **Tree House** (Instagram) |

Artist names are the one thing that must be spelled the way the artist spells it.

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
