# CLAUDE.md

Guidance for Claude Code (or any agent) working in this repository.

## What this repository is

**Yadlo** — a Compose Multiplatform (Android + iOS) companion app for the Yadlo festival, a
three-day lakeside music, sport and beach festival held at Préverenges (Vaud, Switzerland)
each July since 2015.

It is currently an unofficial project with the ambition of becoming the official app for the
2027 edition. The app exists because the festival's information has never existed as
structured data — it lives in a page-builder website, much of it inside images. This app is
the first place the programme, the activities, the stands and the practical information are
modelled as data, which is what makes a personal schedule, reminders and offline access
possible at all.

The codebase was forked from a personal KMP/CMP template. The architecture, the Konsist rules
that enforce it, and the test conventions all come from there and are not up for
renegotiation feature by feature.

## The documents, and which one answers what

Read these before doing any non-trivial work. They do not overlap.

| Document | Answers |
|---|---|
| [`CONTEXT.md`](CONTEXT.md) | **The ubiquitous language.** Every domain term — Happening, Slot, FestivalDay, Lane, Venue, Edition, Phase, Plan, Wishlist, Menu, Mark, Provenance — with its definition and the words to avoid. Use these names in code. |
| [`SPEC.md`](SPEC.md) | **What is being built.** Problem statement, 80 user stories, every implementation and testing decision, and what is explicitly out of scope. Links the seven published UI prototypes at the top. |
| [`DECISIONS.md`](DECISIONS.md) | **Why it is being built that way.** The long-form record, including the alternatives that were rejected and the reasoning that killed them. Consult before reopening a settled question. |
| [`agents/agent-architecture-convention.md`](agents/agent-architecture-convention.md) | Package placement decision procedure, layer shape, MVI vocabulary, DI, error handling, testing conventions. |
| [`agents/agent-commit-convention.md`](agents/agent-commit-convention.md) | Deterministic `type`/`scope` selection for commit messages. |
| [`agents/agent-design-system-convention.md`](agents/agent-design-system-convention.md) | The colour/typography/spacing layer model under `app/design/theme/`. |
| [`README.md`](README.md) | Explains the project to a *human*. Not the technical source of truth for an agent. |

The UI prototypes linked from SPEC.md are **normative for layout and interaction**, not
decoration. Where prose and prototype disagree about a screen, the prototype was the thing the
decision was actually made against — check DECISIONS.md, then ask.

---

## State of the fork

This repo was forked from the template and renamed on 2026-08-10. The placeholder identity
(`AppName` / `io.nicolaszurbuchen.appname`) is fully gone: root package is
`io.nicolaszurbuchen.yadlo`, the Application class is `YadloApplication`, the theme composable
is `YadloTheme`, and the Compose generated-resources package is now `yadlo.shared.generated.resources`.

**`feature/pokemonexplorer/` is gone.** The template's example feature was kept as a working
side-by-side reference and deleted once all four tabs had been built against the pattern six times
over. Nothing of it remains — feature tree, `.sq` file, navigator, DI entry, `NavConfig` entries,
strings, `AppError.PokemonExplorer` and the `pokemon-explorer` commit scope all went together. The
six real features are the reference now; `agents/agent-architecture-convention.md` still describes
the shape.

**Local notifications now exist**, on both platforms, and they are the one part of the app that
does **not** read the injected clock — see the rule below. Remote push still does not exist and is
still deferred: it is what story 16's dormant-user case would need, and DECISIONS.md refuses it on
content grounds rather than technical ones until the association is on board. Story 17, the
end-of-slot warning, was dropped rather than deferred; the reasoning is in DECISIONS.md.

*Plus › Notifications* is built: one switch, stored in its own SQLDelight table under
`common/reminder/`. It is **not** a generic settings store — a second preference is when that shape
is earned. *Effacer mes données* is unrelated and needs no storage at all: it is an action wanting a
repository that can delete, and the screen for it only has to count what is saved.

The other two capabilities the earlier draft assumed are settled: SQLDelight was always wired, and
Coil3's disk cache is now configured in `infra/image/` and verified on an Android device. The
bundled-snapshot fallback is no longer wanted — see DECISIONS.md § No bundled snapshot in v1.

---

## Judgment calls not obvious from the code alone

Everything structural, deterministic, or repeatable lives in
`agents/agent-architecture-convention.md` and is enforced by Konsist — it isn't restated here.
What follows is the handful of things that came up as real corrections and are genuinely easy
to get wrong once.

- **Compose call sites**: `Modifier` is always the **last** argument at the call site, not just
  last in the function signature (trailing lambdas aside). Nothing enforces this mechanically —
  it's a review-time check.
- **Don't add defensive nullability**: don't make a field or parameter nullable "just in case"
  if the call site is only ever reachable with a valid value given how the layer above maps
  things. Nullability should describe a real, reachable state, not hedge against a scenario the
  codebase already prevents.
- **Magic numbers get a comment, not just a name**: a named constant with no explanation just
  moves the "why this bound?" question one file over instead of answering it.
- **Gradle version catalog bundles**: if three or more libraries are always added together (see
  `ktor-common`, `compose-common`, `mvikotlin-common` in `[bundles]`), define a bundle and
  consume it via `libs.bundles.x` instead of listing each one at every call site.
- **Package placement decision criteria** (also in `agents/agent-architecture-convention.md`,
  repeated because it's the single most load-bearing judgment call here): does this file know
  about a specific feature? **Multiple** features → `app/`. Exactly **one** → `feature/<name>/`.
  **Zero**, and pure technical plumbing → `infra/`. **Zero**, but shared domain vocabulary →
  `common/`. Don't pre-emptively put something in `common/` because it *might* be shared later.

### Yadlo-specific rules that override nothing but are easy to violate

- **No `Clock.System.now()` outside the composition root.** The clock is injected everywhere.
  This is what makes `Phase` and every live-state pill testable eleven months before the
  festival, and it is a first-commit requirement, not a convenience.
- **All instant comparisons happen in `Europe/Zurich`**, never the device wall clock.
- **`FestivalDay` is a window, not a date.** A 01:30 set on Saturday morning belongs to Friday.
  Never derive a day by truncating an instant to a calendar date.
- **Slot ids are Edition-qualified** (`2026:dubside-sat`) so a reused id cannot resurrect last
  year's saved plan.
- **Use the CONTEXT.md vocabulary in code.** Not `Event`, not `Session`, not `Attraction` —
  `Happening` and `Slot`. The *Avoid* lists in CONTEXT.md are binding.

---

## Verification before calling anything done

```bash
./gradlew :konsistTest:test
```

```bash
./gradlew :shared:testAndroidHostTest
```

```bash
./gradlew ktlintCheck
```

```bash
./gradlew :shared:verifyCommonMainAppDatabaseMigration
```

Run all four, not just the one you think is relevant — `ktlintCheck` in particular has a
history of catching violations across files a narrower, filtered test run never touches.

The migration check is the odd one out and the reason it is on this list: **a table added to a `.sq`
file without a matching `.sqm` compiles, runs, and passes every other command here.** It only breaks
on a device that already had the database, because SQLDelight takes the schema version from the
migration files rather than the schema files. It has been shipped that way once. Regenerate the
snapshot with `:shared:generateCommonMainAppDatabaseSchema` whenever a `.sq` file changes, and commit
the `.db` it writes.

If a Konsist rule fails and you're tempted to change the rule to make it pass: don't. See
`agents/agent-architecture-convention.md`'s last section.
