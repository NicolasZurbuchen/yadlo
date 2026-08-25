# Agent Architecture Convention

This file exists so an agent can place a new file in the right package and shape a new screen or feature correctly, without re-deriving the architecture from scratch or asking a human. The concrete examples throughout are the six real features; the pattern is what's fixed, not any one feature or its file count.

## Package placement — decision procedure

Walk this in order for any new file:

1. Does it know about **more than one** feature? → `app/`
2. Does it know about **exactly one** feature? → `feature/<name>/`
3. Does it know about **zero** features, and is it pure technical plumbing with no domain vocabulary? → `infra/`
4. Does it know about **zero** features, but represents domain vocabulary shared across features? → `common/`

Don't place something in `common/` speculatively because it *might* be reused later. A single-feature project has almost nothing there — wait until a second feature actually needs the same domain concept before promoting it out of the first one.

## Shape of `app/`, `common/`, and `infra/`

```
app/
├── App.kt                       # Root Composable: theme + image loader + NavGraph
├── design/
│   ├── component/               # App-wide reusable composables (e.g. AppErrorBanner) — cross-feature; a component shared only within one feature belongs in feature/<name>/presentation/component/ instead, and one used by only a single screen belongs in feature/<name>/presentation/screen/<screen>/component/
│   └── theme/                   # Design tokens, color palette, spacing, typography — see agent-design-system-convention.md
├── di/
│   └── AppModule.kt             # Aggregates every feature/infra Koin module into one list — the only DI file allowed to know about more than one feature
└── navigation/
    ├── impl/                    # Concrete *NavigatorImpl classes — the only place allowed to know about more than one feature's destinations at once
    ├── NavConfig.kt
    └── NavigationModule.kt

common/
└── error/                       # AppError / AppException, single throw-catch mechanism — a single-feature project has little else here; it only grows when a second feature needs to share the same domain concept as the first

infra/
├── database/                    # SQLDelight driver setup (expect/actual)
├── mvi/                         # MVIKotlin base wiring (StoreFactory binding)
├── navigation/                  # AppNavigator, NavKeyHandler, NavGraph — feature-agnostic, zero feature imports
├── network/                     # Ktor client configuration (expect/actual engine)
├── platform/                    # expect/actual platform utilities (BackHandler, Platform)
├── text/                        # String utilities with no domain vocabulary (diacritic folding for search)
└── ui/                          # UiText — resource/raw/composite text abstraction
```

## Layer shape inside a feature

```
feature/<name>/
├── data/
│   ├── datasource/
│   │   ├── local/                    # *LocalDataSource(Impl); a mapper/ subfolder holds only top-level extension functions mapping the local storage type to a domain model
│   │   └── remote/                   # *RemoteDataSource(Impl); an api/ subfolder holds the Ktor *Api(Impl), a dto/ subfolder the wire-format *Dto classes, and a mapper/ subfolder the top-level extension functions mapping Dto -> domain model
│   ├── repository/                   # *RepositoryImpl only, implements the domain interface
│   └── di/
├── domain/
│   ├── model/                        # pure values — no Flow/StateFlow, no internal mutability
│   ├── repository/                   # interface only, no default implementations
│   └── usecase/                      # reserved for logic that touches a port (repository, clock) or coordinates more than one step; a UseCase never injects another UseCase
└── presentation/
    ├── navigation/                   # *Destination, *Navigator (interface), *NavKeyHandler — a feature only ever knows its own destinations
    ├── component/                    # composables reused across screens *within this feature only*; take a UiModel, never raw primitives. Cross-feature reuse goes in app/design/component/ instead
    └── screen/<screen>/
        ├── *Contract.kt              # exactly Intent/Label/Action/Message (sealed interfaces) + State (data class) — nothing else lives here. Written in **domain** types: a UiModel here is a rendering decision taken before anything is rendered
        ├── *StoreFactory.kt          # Bootstrapper + Executor + a nested `internal object ReducerImpl` — never a standalone *Reducer.kt file, never `private` (internal is what makes it directly unit-testable from commonTest). The Store interface and the factory, and nothing else: no top-level functions
        ├── *UiMapper.kt               # a single top-level extension function, State -> UiModel — the only place that conversion happens
        ├── *UiModel.kt                # the Composable's actual input type — no domain types as field types. Only the screen's own; every other UiModel goes in uimodel/
        ├── *ViewModel.kt              # wraps the StoreFactory; exposes `state: StateFlow<*UiModel>` and `labels: Flow<*Label>` — never State
        ├── *Route.kt / *Screen.kt     # the Screen's public function takes only Modifier, the matching *UiModel, or lambdas — never *State
        ├── *ScreenPreview.kt          # exactly two top-level declarations: a private `<Screen>StateProvider : PreviewParameterProvider` holding every fixture, and one private `@PreviewThemes` function rendering inside `YadloPreview`
        ├── component/                 # composables reused only within this screen; take a UiModel, never raw primitives
        ├── uimodel/                   # the pieces of the screen's vocabulary that are not the model itself — PhaseUiModel, SiteMomentUiModel. The screen's own *UiModel must not be in here
        └── mapper/                    # *UiMapper.kt, one per converted type, each a top-level extension function returning a UiModel. **The only place in presentation/ allowed to import the domain layer**
```

### The three screen subfolders

All three are optional and appear when there is more than one of something. A screen package with
none of them is still the common case.

`uimodel/` and `mapper/` exist because a screen that converts a domain enum needs somewhere to put
both halves, and the alternatives are worse: the twin sitting loose in the screen package puts the
type the Composable is handed between two enums it merely mentions, and the converter sitting at
the bottom of the StoreFactory makes a file about wiring the only place that answer is written
down. Both were the actual state of `feature/home/` before this convention existed.

**The domain crossing happens on the way out, once.** The Store holds domain types all the way
into the State; the screen's `*UiMapper` converts at the top of its single function. That is why
the Contract may not name a UiModel — a `PhaseUiModel` on a Message drags the presentation type
backwards through the Executor and the Reducer.

### Previews

Every screen package has a `*ScreenPreview.kt`, and it has a shape rather than a habit — enforced
by `konsistTest/PreviewTest.kt`.

- **Two top-level declarations.** The provider and the preview function. Every fixture goes
  *inside* the provider, where it is visibly in service of the sequence it feeds. The pull is
  always to add a third, and each one is individually reasonable while the file stops being
  readable as "here are the states, here is the screen".
  A fixture that has to be a `const val` goes in the provider's `companion object` — still inside
  it, and still not a top-level property the screen looks like it depends on.
- **One function, not one per theme.** `@PreviewThemes` is the multipreview carrying light and
  dark, so the body is written once. The old shape — `FooScreenPreview` and `FooScreenDarkPreview`
  side by side — meant a fixture change had to be made twice and a preview that drifted from its
  own dark twin looked fine in review.
- **`YadloPreview` supplies the theme and the ground.** Compose's preview pane paints its own
  white whatever the theme says, so a screen that does not fill its background renders dark-theme
  text on a white sheet and passes a glance.

The vocabulary lives once, and in two places, because the placement rule splits it.
`PreviewThemes` and `PreviewUiMode` know nothing about this app — an annotation setting a system
ui-mode flag, and two Android constants commonMain cannot import — so they are `infra/preview/`,
beside `infra/ui/UiText` and `infra/platform/BackHandler`. `YadloPreview` imports the theme and the
palette, so it *is* the design system and gets `app/design/preview/`.

Not `app/design/component/`: a component is something a screen draws, and this is never drawn in a
shipped screen. The Konsist rule forbidding a screen suffix in a component package says the same
thing mechanically.

All three were born inside `HomeScreenPreview`, which is how twenty-three screens end up each owning
a private copy of the same annotation.

### Waiting

**A screen waits as its own silhouette, never as a spinner.** A centred `CircularProgressIndicator`
is the same picture on every screen and says only that something is happening; a shimmer skeleton
says what is about to arrive and in what shape, so the content lands in a layout the eye has
already settled on. `ShimmerPulse` and `Modifier.shimmerBlock()` are the tools; one `ShimmerPulse`
around the whole skeleton, so every block breathes off a single animated value.

### Module files are grouped by screen, not by declaration type

Each `StoreFactory` sits directly above the `ViewModel` that wraps it, under the screen they belong
to — see `HomeModule.kt`. Grouping by type instead (`factoryOf` for everything, then `viewModelOf`
for everything) means adding a screen edits two places and reading one scans two lists.

Not enforceable by Konsist: it is the order of DSL calls inside a lambda, which the API does not
expose. Convention only.

## MVI vocabulary

- `Intent` — a user-initiated event from the UI
- `Label` — a one-shot side effect (navigation, etc.) — a screen with genuinely nothing to signal still declares an empty sealed interface, it isn't omitted
- `Action` — a bootstrapper-initiated internal trigger
- `Message` — reducer input, produced by the executor
- `State` — an immutable, screen-logical snapshot the reducer reads and writes — **never leaves the Store/Executor/Reducer/UiMapper boundary**. If a Composable seems to need a State field, that means the UiMapper is missing a field, not a reason to pass State through. This is enforced by Konsist (`PresentationLayerTest.kt`), not just convention.

## Dependency injection (Koin)

- `factoryOf` — UseCases, StoreFactories
- `viewModelOf` — ViewModels
- `singleOf` — Repositories, DataSources, **only when every constructor parameter should be resolved from the DI graph**

`singleOf(::Impl)` resolves every constructor parameter via reflection, including ones with Kotlin default values — it does not skip them. A class with a defaulted parameter that must *not* come from the DI graph (e.g. an injected clock lambda kept as a default for deterministic testing) needs an explicit binding instead: `single<Interface> { Impl(get(), get()) }`. Omitting the argument lets Kotlin's own default apply, since Koin never touches a parameter it wasn't asked to resolve.

### Known gap — fix this in the base template, not here

**Nothing verifies the Koin graph.** A module can bind a class whose constructor needs something no
module provides, and no test catches it: `konsistTest/` checks structure, not resolvability, and the
unit tests construct their subjects directly. The failure surfaces the first time that screen is
opened on a device.

`koin-test`'s `verify()` closes this — it walks every definition and resolves the constructor
signatures at build time. It is not a drop-in for two reasons worth knowing before adding it:

1. It is not in the version catalog, so it is a new test dependency.
2. `verify()` trips on parameterised definitions — `viewModel { (id: String) -> Foo(get(), id) }` — and
   `happeningModule` has one. They need `verify(extraTypes = ...)` or an explicit exclusion, which
   is the fiddly part.

**This belongs upstream in the template**, so every project forked from it starts with a verified
graph rather than discovering the gap independently. Adding it here would fix one app; adding it
there fixes the next one too.

## Error handling

- `AppError` (a single sealed interface) plus `AppException` is the only throw/catch mechanism in the app — no ad-hoc exception types.
- Display resolution happens once, at a shared `AppError -> AppErrorUiModel` mapping (`common/error/AppErrorUiMapper.kt`), using `UiText` (`infra/ui/UiText.kt`) for anything that needs runtime data rather than only a static resource.
- Every user-facing string is a Compose Multiplatform resource (`shared/src/commonMain/composeResources/values/strings.xml`) — never a hardcoded literal in a Composable. Naming: `feature_screen_role`, or `common_role` for a cross-feature string. Add an `element` segment only when `role` alone would be ambiguous within that screen. Group entries by feature/concern with a blank line between groups, not alphabetically.

## Testing

- No mocking library. A seam is either a hand-written fake — file-local `private class` for a one-off data-source/API test, or a single shared `Fake<Feature>Repository` in `domain/fake/` when it's reused across several test files for the same feature — or an injected lambda (the clock pattern above).
- `runTest` wraps every suspend-based test, unconditionally, even ones with no real async work.
- Turbine is for `Label` flows only (one-shot events). `StateFlow` state is read synchronously after `testDispatcher.scheduler.runCurrent()` / `advanceTimeBy(...)`.
- Every `Mapper`, `RepositoryImpl`, `DataSourceImpl`, `UseCase`, `UiMapper`, and `StoreFactory` (as a matching `ReducerTest` + `ExecutorTest` pair) needs a corresponding test file — enforced by `konsistTest/TestingTest.kt`, not just this document. Add to that file's coverage list when a new category of production file gets established.

## Konsist rules — do not modify without asking

`konsistTest/` is the enforcement mechanism for every rule above. Do not loosen, delete, or work around a Konsist rule to make a change compile or a build go green. If a rule is genuinely too strict for something you're legitimately trying to do, stop and tell the developer why, and let them decide whether the rule should change — don't decide that yourself. Silently editing the rule to fit the code defeats the entire point of having it.
