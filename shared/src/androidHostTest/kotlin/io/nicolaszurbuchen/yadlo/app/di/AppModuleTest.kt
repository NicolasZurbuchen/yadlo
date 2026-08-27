package io.nicolaszurbuchen.yadlo.app.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import io.ktor.client.engine.HttpClientEngine
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsKindUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsStoreFactory
import io.nicolaszurbuchen.yadlo.infra.di.platformModule
import io.nicolaszurbuchen.yadlo.infra.platform.BuildFlags
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify
import kotlin.test.Test

/**
 * **The one failure this app had no net for.** A module can bind a class whose constructor needs
 * something no module provides, and nothing catches it: Konsist checks structure rather than
 * resolvability, and every unit test constructs its subject directly. The first sign is a crash
 * the first time that screen is opened, on a device.
 *
 * `verify()` walks every definition and resolves the constructor signatures without instantiating
 * anything, so it runs in milliseconds on the JVM and needs no Android framework.
 *
 * `agents/agent-architecture-convention.md` records this gap and argues the fix belongs upstream in
 * the base template, so that every project forked from it starts with a verified graph. That is
 * right about where the *fix* belongs and does not change that the hole is open here now — the
 * template can gain this without the app waiting for it.
 */
class AppModuleTest {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun appModule_resolvesEveryConstructorItBinds() {
        // The graph as an entry point actually assembles it: `initKoin` adds the BuildFlags module
        // to `appModule`, and each platform adds its own `platformModule`. Verifying `appModule`
        // alone would pass while missing exactly the bindings that differ by platform, which are
        // the ones hardest to get right.
        val graph =
            module {
                includes(appModule + platformModule)
                single { BuildFlags(isDebug = false, version = "test") }
            }

        graph.verify(
            extraTypes =
                listOf(
                    // Supplied by `androidContext()` at startup rather than by any module, which is
                    // why `DatabaseDriverFactory` and `Notifier` cannot be declared in common code.
                    Context::class,
                    // The two parameterised definitions. A NavKey argument is not a dependency, so
                    // it is passed at resolution rather than bound — `HappeningViewModel` takes a
                    // Happening id and `StandsViewModel` the kind of directory it is showing.
                    String::class,
                    StandsKindUiModel::class,
                    // `verify()` cannot see inside a definition written as a lambda: it reflects on
                    // the bound type's primary constructor instead. The SQLDelight query classes are
                    // taken off the database — `single { get<AppDatabase>().cachedDocumentQueries }`
                    // — and never constructed, so their `SqlDriver` parameter is one the graph is
                    // right not to provide. Listing it here is not excusing a gap; it is telling
                    // the checker which constructors are never called.
                    SqlDriver::class,
                    // The same blind spot, on the two parameterised ViewModels. Each builds its
                    // StoreFactory inside the lambda so the NavKey argument can be threaded through
                    // — `HappeningViewModel(HappeningStoreFactory(get(), …, happeningId))` — so the
                    // factory is never resolved from the graph and correctly has no binding. What
                    // the factories themselves need *is* checked: `get()` still resolves against
                    // this graph at runtime, and every one of those dependencies is bound normally.
                    HappeningStoreFactory::class,
                    StandsStoreFactory::class,
                    // `single { createHttpClient(get()) }` — the engine is an expect/actual factory
                    // rather than a binding, so `HttpClient`'s constructor asks for something the
                    // graph deliberately does not hold.
                    HttpClientEngine::class,
                    // The one entry here broader than I would like, and worth knowing about: it is
                    // needed by `TimeTravelClock(source, enabled)` and `WallClock(source)`, both
                    // built in lambdas in `timeModule` with `enabled` computed from BuildFlags. A
                    // primitive in this list excuses *any* constructor taking a Boolean, so a future
                    // `singleOf(::Foo)` whose Foo takes one would pass unchecked. No such definition
                    // exists today; if one appears, the honest fix is to bind the flag rather than
                    // widen this further.
                    Boolean::class,
                ),
        )
    }
}
