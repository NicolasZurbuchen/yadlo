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

Two things are deliberately **not** done yet:

1. **`feature/pokemonexplorer/` is still in the tree.** It is the template's example feature and
   is kept only as a working side-by-side reference while the first real Yadlo feature is built.
   Delete it — along with the `pokemonExplorerModule` entry in `app/di/AppModule.kt`,
   `PokemonExplorerNavigatorImpl.kt`, the initial route in `app/navigation/NavConfig.kt`, its
   strings in `composeResources/values/strings.xml`, its `.sq` file, its `error_pokemon_*`
   entries, and the `pokemon-explorer` commit scope — once the pattern is internalised. Do not
   ship it.
2. **Three template capabilities assumed by SPEC.md have not been verified**: offline-first disk
   caching of a fetched JSON bundle, local notifications on both platforms, and disk-cached
   remote images via Coil3. Coil3 and SQLDelight are wired; the notification and
   bundled-fallback paths are not. Verify before designing around them.

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

## How work lands: branch and pull request, never straight to `main`

**Never commit or push to `main`.** Every change — code, content, documentation, a one-line
typo fix — goes on a branch and reaches `main` through a pull request that the maintainer
merges.

```bash
git checkout -b feat/some-change
```

This is not ceremony. An agent can run the whole verification suite, read its own diff and
still be confident about something wrong; the pull request is where a human sees the change as
a whole before it becomes the trunk. Speed is not the constraint on this project — the festival
is eleven months out — so there is no work worth skipping review to land faster.

Branch names follow the commit type: `feat/…`, `fix/…`, `refactor/…`, `docs/…`, `ci/…`,
`build/…`. Commit messages keep the Conventional Commits format enforced by Commitlint, and
the scope list in `commitlint.config.js` is the only vocabulary allowed.

Open the pull request with `gh pr create`. The body should say what changed and, more usefully,
what was verified and what was *not* — an unrun code path, a decision taken under an
assumption, a behaviour that needs a device to confirm. Do not merge your own pull request
unless explicitly asked to.

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

Run all three, not just the one you think is relevant — `ktlintCheck` in particular has a
history of catching violations across files a narrower, filtered test run never touches.

If a Konsist rule fails and you're tempted to change the rule to make it pass: don't. See
`agents/agent-architecture-convention.md`'s last section.
