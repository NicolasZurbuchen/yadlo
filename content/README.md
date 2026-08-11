# Yadlo content

The festival's data. Everything the app shows comes from here; nothing here is code.

Run `node content/validate.js` after any edit. It fails on anything structurally wrong and warns
on anything merely missing.

## Layout

```
content/
├── festival.json                 live truth — true regardless of which edition you are viewing
├── editions.json                 which editions exist; read only by the archive screen
├── editions/
│   └── 2026/
│       ├── edition.json          the frozen record of one edition
│       └── images/
│           ├── artists/          one photo per artist
│           ├── activities/
│           └── stands/
├── shared/
│   └── logos/                    partner logos, reused year after year
├── GAPS.md                       what is still missing, and who to ask
└── validate.js
```

**An edition is a folder, not a file.** Everything belonging to 2026 — its data and its photos —
lives under `editions/2026/`, so an edition can be added or archived as one unit and its image
paths never collide with another year's.

**`shared/` holds assets that outlive an edition.** A partner logo is the same file every year,
and so is a photo of the GladiaSUP course — 25 of the 38 happenings in 2026 are activities and
stands that mostly recur. Keeping those under `editions/2026/` would mean copying them into 2027
and 2028.

**Where a file sits is a filing convention, not a modelling decision.** Each edition declares its
own `images` array, so any edition may point at any path — `shared/`, its own folder, even
another year's. Nothing in the model constrains it. That means getting the folders wrong costs a
file move and a find-and-replace, never a schema change.

Given that, one rule and one default:

- **`shared/`** — the subject does not change year to year. Partner logos; a photo of the
  floating trampoline; a stand's own branding.
- **`editions/{year}/images/`** — the asset belongs to that year. Artist press photos, which the
  artist supplies fresh and which date badly; anything shot at that edition.
- **When in doubt, put it in the edition.** The two mistakes are not symmetrical: a file wrongly
  shared makes 2027 silently show a 2026 photo, which nobody notices and everybody is misled by.
  A file wrongly duplicated wastes 200 KB.

## The split between the two JSON files

The test is: **would a past-edition archive need its own copy?**

- **`festival.json`** — history, contact, social links, Hot'Staff, transport, payment,
  accessibility, FAQ. Someone reading the 2018 archive wants *today's* contact address, not
  2018's.
- **`edition.json`** — programme, activities, stands, menus, prices, opening hours, partners,
  closing figures, and whether entry was free. If the festival moves or starts charging, the 2026
  record must still say Préverenges and still say free.

### Repeating a Happening next year means copying it, and that is deliberate

GladiaSUP will run again in 2027, and its entry will be duplicated into `editions/2027/`. That is
the intended cost of a self-contained edition, not an oversight:

- An archive has to be readable **alone**. Opening 2019 fetches one file and shows everything;
  it does not depend on a shared catalogue that has since been edited.
- One file means **one fetch and one ETag**, so an edition is never half-updated.
- The things that look duplicated mostly are not. Prices move, descriptions get rewritten, an
  activity gains a booking link. Copying last year's file forces someone to look at each entry
  and confirm it is still true — which is the same work a shared catalogue would only defer.

**Happening ids are deliberately *not* edition-qualified** — `gladiasup`, not `2026:gladiasup` —
because a Happening is scoped to the file it lives in. **Slot ids are** (`2026:gladiasup-sat`),
because those are what a user saves into their plan, and a reused id must never resurrect last
year's saved festival.

## Images

- **Photos**: WebP or JPEG, about 1200px on the long edge, under ~200 KB. These are decoded on a
  phone, often on a field with no signal, so ship them pre-sized rather than uploading originals.
- **Logos**: SVG where possible, otherwise PNG with transparency. Never JPEG — a logo on a white
  box looks broken next to one that is properly transparent.
- Reference them from the JSON with an absolute `https://` URL. Every happening carries
  `images: [{ src, credit }]`; every partner carries a single `logo`.
- `credit` exists because press photos usually come with a photographer's condition attached.

## Conventions worth knowing before editing

- **Everything is French.** No `{fr, en}` objects — the validator rejects them.
- **Slot ids are edition-qualified** (`2026:dubside-sat`) so a reused id cannot resurrect last
  year's saved plan.
- **Times are written with their offset** (`2026-07-10T23:30:00+02:00`). A set running past
  midnight keeps the `dayId` of the day it started — Friday's 23:30 set ends at 01:30 and is
  still Friday's.
- **A day's `start`/`end` are the festival's opening hours**, not a bounding box. Activities may
  legitimately fall outside them: the beach is public, so the morning yoga runs before the stands
  open. The validator warns, which is the point — next year one of those may be a mistake.
- **`provenance`** is `confirmed` (the organisers said so), `archived` (from a past edition's
  record) or `unverified` (reconstructed, or read off a photo).
- **No price means free only when the organisers publish it that way.** Their activity posters
  carry a price when there is one and nothing when there is not, so a blank poster is a statement.
  A price nobody has ever published is `unverified`, not free.
