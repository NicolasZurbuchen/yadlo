# Yadlo content

The festival's data. Everything the app shows comes from here; nothing here is code.

Run `node content/validate.js` after any edit. It fails on anything structurally wrong and warns
on anything merely missing.

## Layout

```
content/
├── festival.json                 live truth — true regardless of which edition you are viewing
├── editions.json                 which editions exist; read only by the archive screen
├── shared/                       the picture bank — spans every edition
│   ├── images/
│   │   ├── artists/
│   │   ├── activities/
│   │   └── stands/
│   └── logos/                    partners and suppliers
├── editions/
│   └── 2026/
│       ├── edition.json          the frozen record of one edition
│       └── images/               only what depicts *this edition* — the affiche, photos taken
├── GAPS.md                       what is still missing, and who to ask
└── validate.js
```

**The split is about what a picture depicts, not about when it was taken.**

- **`shared/`** — a picture *of a thing*. Dubside, the floating trampoline, De l'Or Bokit,
  Rivella's logo. The thing exists independently of any one year, so its picture is filed once
  and referenced by whichever editions need it. 25 of the 38 happenings in 2026 are activities
  and stands that mostly recur; artists come back too.
- **`editions/{year}/images/`** — a picture *of that edition*. The affiche, photographs taken
  over the weekend, a hero image made for that year's campaign. These depict an event, not a
  subject, and belong to it.

That line is easier to apply than "does this change year to year", because it asks what is in the
frame rather than requiring a prediction.

**Where a file sits is a filing convention, not a modelling decision.** Each edition declares its
own `images` array, so any edition may point at any path. Nothing in the model constrains it, and
getting the folders wrong costs a file move and a find-and-replace — never a schema change.

### The one rule the picture bank depends on: `shared/` is append-only

**Never overwrite a file in `shared/`.** If Dubside sends a new press photo for 2027, add
`dubside-2027.webp` and point 2027 at it. Leave `dubside.webp` alone.

This is the whole safety of the arrangement. Overwriting in place would silently rewrite every
past edition that referenced the file — the 2026 archive would quietly start showing a 2027
photograph, and nothing would flag it because every reference is still valid. An archive that
changes underneath you is worse than a missing image.

Nothing enforces this. It is one sentence of discipline holding up the entire structure, which is
why it is stated this plainly.

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
