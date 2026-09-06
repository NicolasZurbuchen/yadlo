package io.nicolaszurbuchen.yadlo

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertFalse
import kotlin.test.Test

class LayerBoundariesTest {
    companion object {
        /**
         * Every top-level check below is written against this rather than against a bare `.core.`
         * or `.app.` substring, because those match libraries too — `.core.` alone flags every
         * `androidx.compose.animation.core` import in the theme, which is how this was found.
         */
        private val projectPrefix =
            Konsist
                .scopeFromProduction(moduleName = "shared")
                .packages
                .map { it.name }
                .reduce { acc, name -> acc.commonPrefixWith(name).trimEnd('.') }

        /** True when [importName] is a project import from the top-level package [topLevel]. */
        private fun isProjectImportFrom(
            importName: String,
            topLevel: String,
        ): Boolean = importName.startsWith("$projectPrefix.$topLevel.")
    }

    /**
     * **A feature may not reach into another feature at all**, not merely into its internals.
     *
     * This used to exempt `domain`, flagging only imports containing `.presentation` or `.data`, so
     * `feature.search` could have taken a model straight off `feature.programme`. Nothing ever did
     * — which is what made tightening it free, and is the only moment worth doing it in. A domain
     * type two features share is `core/` material by the placement rule, and borrowing one across
     * the boundary is how a slice quietly stops being one.
     *
     * The substring test also used bare `"feature."`, which any library package could satisfy; it
     * is anchored to the project prefix now, like the rest of this file.
     */
    @Test
    fun `feature layers should not depend on other features`() {
        Konsist.scopeFromProject()
            .files
            .filter { it.hasPackage("..feature..") }
            .assertFalse { file ->
                val currentFeature = (file.packagee?.name ?: "").substringAfter("feature.").substringBefore(".")

                file.imports.any { import ->
                    isProjectImportFrom(import.name, "feature") &&
                        import.name.substringAfter("feature.").substringBefore(".") != currentFeature
                }
            }
    }

    /**
     * **`infra/` imports nothing of this app**, which is the property the placement rule leans on:
     * a file goes here when it would be as much at home in another project, and that is only
     * checkable if nothing here names a Slot, a token or a screen.
     *
     * Widened from *may not import features*. Everything else in the tree was already fenced —
     * `design/` may not reach the domain, `core/` may not reach the features, nothing may reach
     * `app/` — and `infra/` was the one layer whose stated invariant nothing enforced, so
     * `infra -> core` and `infra -> design` were both open. It carried two dead exclusions too,
     * `AppModule` and `NavGraph`, neither of which was ever in scope; an exclusion that matches
     * nothing reads as a known exception and is really an invitation.
     *
     * The two generated packages are not exceptions to it. `cache` is SQLDelight's output and
     * `shared` is Compose's resource accessor: both are written by the build from files `infra/`
     * already owns, and neither is a layer.
     */
    @Test
    fun `infra should not depend on anything else in the project`() {
        val layers = listOf("app", "core", "design", "feature")

        Konsist.scopeFromProject()
            .files
            .filter { it.hasPackage("..infra..") }
            .assertFalse { file ->
                file.imports.any { import -> layers.any { isProjectImportFrom(import.name, it) } }
            }
    }

    /**
     * **Nothing inside `shared` may import `app/`.** That is what makes it the composition root
     * rather than a package that happens to hold `App.kt`: it imports everything, and an importer
     * would close a cycle with whatever it imports.
     *
     * Unstateable until now. `app/design/` was the most depended-on package in the codebase (#76),
     * and `app/navigation/TabChrome` was read by seven feature files until it moved to
     * `design/theme/` (#79), which is where layout tokens belong anyway.
     *
     * The platform binaries are the deliberate exception and are outside this scope: `MainActivity`,
     * `YadloApplication` and `BootReceiver` in `androidApp/` reach for `App`, `initKoin` and
     * `ReminderScheduler`, which is a binary consuming its own root rather than a layer violation.
     */
    @Test
    fun `nothing in shared outside the app shell may import it`() {
        val belowTheRoot = listOf("core", "design", "feature", "infra")

        Konsist
            .scopeFromProduction(moduleName = "shared")
            .files
            .filter { file -> belowTheRoot.any { file.hasPackage("..$it..") } }
            .assertFalse {
                it.imports.any { import -> isProjectImportFrom(import.name, "app") }
            }
    }

    /**
     * **`core/` is below the features, and `app/` is above them.** An import in either direction
     * from here is the dependency graph folding back on itself: `core/` would be reaching up into
     * something that composes it, and every screen that reads `core/` would inherit the reach.
     *
     * This was violated until `design/` moved out of `app/`. Nine files in
     * `core/content/presentation/` imported `app.design.theme` and `app.design.component`, which
     * made the graph `app -> core -> app` — and no rule saw it, because every package rule keyed on
     * `feature` or `common` and neither end of that cycle was either.
     */
    @Test
    fun `core should not depend on features or the app shell`() {
        Konsist.scopeFromProject()
            .files
            .filter { it.hasPackage("..core..") }
            .assertFalse {
                it.imports.any { import ->
                    isProjectImportFrom(import.name, "feature") || isProjectImportFrom(import.name, "app")
                }
            }
    }

    /**
     * **The design system is the base of the graph, so nothing it imports may sit above it.**
     *
     * It is what makes `design/` a system rather than the folder shared UI ends up in: the whole
     * of it can be read without meeting a Slot, a Phase or a tab stack. A component that needs one
     * of those owns a rule about the subject and belongs beside that subject — see #74.
     *
     * `core/` closed last: `YadloDietaryTagUiModel` held one `DietaryCoverage` import until the
     * Stand-level half of `toDietaryTags` moved to `core/content/presentation/mapper/`. With it
     * gone, the whole of `design/` can be read without meeting the domain, which is the property
     * that makes it a system rather than the folder shared UI ends up in.
     */
    @Test
    fun `design should not depend on the domain, the features or the app shell`() {
        Konsist.scopeFromProject()
            .files
            .filter { it.hasPackage("..design..") }
            .assertFalse {
                it.imports.any { import ->
                    isProjectImportFrom(import.name, "core") ||
                        isProjectImportFrom(import.name, "feature") ||
                        isProjectImportFrom(import.name, "app")
                }
            }
    }
}
