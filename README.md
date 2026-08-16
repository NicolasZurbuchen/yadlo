# Yadlo

A Kotlin Multiplatform / Compose Multiplatform companion app for **[Yadlo](https://yadlo.ch)** —
a three-day lakeside music, sport and beach festival held at Préverenges (Vaud, Switzerland)
each July since 2015. Android and iOS from one shared codebase.

The festival's information has never existed as structured data: it lives in a page-builder
website, much of it baked into images, with no prices, no menus and no way to keep track of
what you want to see. This app is the first place the programme, the activities, the stands and
the practical information are modelled as data — which is what makes a personal schedule,
reminders and full offline access possible at all.

Four tabs — **Accueil · Programme · Mon Yadlo · Plus** — over a single content model, with the
whole app reshaping itself around where the year is (off season, announced, approaching, live,
ended).

> **Status:** unofficial project, aiming to become the official app for the 2027 edition.
> Currently in initial setup — see [SPEC.md](SPEC.md) for the full plan.

---

## 📚 Project documents

| Document | What it holds |
|---|---|
| [SPEC.md](SPEC.md) | The spec: problem statement, 80 user stories, implementation and testing decisions, scope boundary. Links every UI prototype. |
| [CONTEXT.md](CONTEXT.md) | The domain glossary — the vocabulary used in code and in conversation. |
| [DECISIONS.md](DECISIONS.md) | The long-form record of every decision, including the alternatives that were rejected and why. |
| [CLAUDE.md](CLAUDE.md) | Working guidance for agents, plus the current state of the fork. |

---

## 🧱 Tech Stack

### 🧩 Architecture
- Clean Architecture (Data, Domain, Presentation layers)
- MVI with MVIKotlin (StoreFactory, Executor, Reducer pattern)
- UseCase-driven domain interaction, reserved for logic that touches a port (a repository, a clock) — a UseCase that would just forward to a single pure domain call doesn't exist
- Each repository juggles a remote (Ktor) and local (SQLDelight) data source, with neither leaking past the repository boundary
- Konsist for structural architecture enforcement, organized by rule category rather than by layer, including rules that enforce **test coverage itself**

### 🛠 Libraries
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) — shared UI for Android and iOS
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [MVIKotlin](https://arkivanov.github.io/MVIKotlin/) — MVI framework
- [Koin](https://insert-koin.io/) — dependency injection
- [Navigation 3](https://developer.android.com/guide/navigation/navigation-3) — KMP-compatible artifacts, type-safe `NavKey` destinations
- [Ktor](https://ktor.io/) — remote data sources
- [SQLDelight](https://cashapp.github.io/sqldelight/) — local data sources
- [Coil3](https://coil-kt.github.io/coil/) — image loading, wired to the shared Ktor `HttpClient`
- [Compose Multiplatform resources](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-resources.html) — shared strings across platforms
- [Konsist](https://docs.konsist.lemonappdev.com/) — architecture test enforcement
- [Turbine](https://github.com/cashapp/turbine) — Flow/Label testing
- [ktlint](https://pinterest.github.io/ktlint/) + [ktlint-compose-rules](https://mrmans0n.github.io/compose-rules/)
- [Husky](https://typicode.github.io/husky/) + [Commitlint](https://commitlint.js.org/) — conventional commit enforcement via Git hooks

---

## 📁 Project Structure

```
shared/
├── app/                              # Composes the whole application; the only place
│   ├── App.kt                        #   allowed to know about more than one feature
│   ├── design/{component,theme}/
│   ├── di/{KoinInitializer,AppModule}.kt
│   └── navigation/
├── common/                           # Domain concepts genuinely shared across features
│   └── error/                        # AppError / AppException
├── feature/                          # Vertical slices: data + domain + presentation each
│   └── {home,programme,monyadlo,plus,happening}/
└── infra/                            # Reusable plumbing, zero feature knowledge
    ├── database/  mvi/  navigation/  network/  platform/  ui/
androidApp/                           # Android application module
iosApp/                               # iOS application module
konsistTest/                          # Architecture + coverage enforcement tests
```

Planned Yadlo feature slices: `home` (Accueil), `programme`, `monyadlo` (Plan + Wishlist),
`happening` (the shared fiche template for Artist / Activity / Stand) and `plus`.

---

## 🏛 Architecture Decisions

Four top-level packages, each with a distinct responsibility: `app/` composes the whole
application and is the only place allowed to know about more than one feature at once; `infra/`
is reusable technical plumbing with zero domain or feature knowledge; `common/` holds domain
concepts genuinely shared across features; `feature/` holds vertical feature slices, each owning
its full data/domain/presentation stack and never reaching into another feature's internals.

Each feature follows Clean Architecture layering with an MVI presentation layer (MVIKotlin's
Store/Executor/Reducer), a strict `State` (internal) vs. `UiModel` (what the Composable actually
renders) split, Koin for dependency injection, Navigation 3 for routing, and a matching Konsist
rule for nearly every convention above — this repo treats "documented but not enforced" as
equivalent to "not true."

For the full rule set see [`agents/agent-architecture-convention.md`](agents/agent-architecture-convention.md).
For the product-level decisions — content architecture, phase derivation, screen layouts,
interaction rules, visual identity — see [SPEC.md](SPEC.md) and [DECISIONS.md](DECISIONS.md).

---

## 🚀 Setup

Requires [Node.js](https://nodejs.org/) (for the Git hooks) and the Android SDK.

```bash
npm install
```

`local.properties` must point at your Android SDK (`sdk.dir=...`). For iOS, set `TEAM_ID` in
`iosApp/Configuration/Config.xcconfig`.

---

## ✅ Commit Conventions

This project uses **Husky** and **Commitlint** to enforce
[Conventional Commits](https://www.conventionalcommits.org/) at commit time.

**Format:** `<type>(<scope>): <description>`

- **Types:** `feat`, `fix`, `refactor`, `build`, `chore`, `ci`, `docs`, `perf`, `style`, `test`, `revert`.
- **Scopes:** `network`, `database`, `content`, `notification`, `di`, `navigation`, `theme`, `common`, `gradle`, `deps`, `home`, `programme`, `mon-yadlo`, `happening`, `plus`.
- A scope is **required** for `feat`, `fix`, `refactor`, and `build`.

Example: `feat(programme): add day-scoped search`

> [!IMPORTANT]
> The scope list lives in three places — `commitlint.config.js`, this README, and
> [`agents/agent-commit-convention.md`](agents/agent-commit-convention.md) — and they have
> drifted apart before. Update all three together.

For the full deterministic `type`/`scope` decision procedure, see
[`agents/agent-commit-convention.md`](agents/agent-commit-convention.md).

---

## 🧪 Running Tests

```bash
./gradlew :konsistTest:test
```

```bash
./gradlew :shared:testAndroidHostTest
```

```bash
./gradlew ktlintCheck
```

---

## 🧑‍💻 Author

**Nicolas Zurbuchen**
Android Software Engineer based in Tokyo, Japan
Contact: [nicolas.zurbuchen@outlook.com](mailto:nicolas.zurbuchen@outlook.com)
