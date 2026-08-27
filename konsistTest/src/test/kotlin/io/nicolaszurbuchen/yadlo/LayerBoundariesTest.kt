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

    @Test
    fun `feature layers should not depend on other features internal implementation`() {
        Konsist.scopeFromProject()
            .files
            .filter { it.hasPackage("..feature..") }
            .assertFalse { file ->
                val packageName = file.packagee?.name ?: ""
                val currentFeature = packageName.substringAfter("feature.").substringBefore(".")

                file.imports.any { import ->
                    val importName = import.name
                    if (importName.contains("feature.")) {
                        val importedFeature = importName.substringAfter("feature.").substringBefore(".")
                        val isInternal = importName.contains(".presentation") || importName.contains(".data")

                        importedFeature != currentFeature && isInternal
                    } else {
                        false
                    }
                }
            }
    }

    /**
     * `infra/` is plumbing: it knows no feature, and it knows no domain either.
     *
     * This carried two exclusions, `AppModule` and `NavGraph`, and both were dead. `AppModule` has
     * lived in `app/di/` for as long as the rule has existed, so it was never in scope; `NavGraph`
     * is in `infra/navigation/` and has zero feature imports. An exclusion that matches nothing
     * reads as a known exception and is really an invitation — see #73.
     */
    @Test
    fun `infra should not depend on features`() {
        Konsist.scopeFromProject()
            .files
            .filter { it.hasPackage("..infra..") }
            .assertFalse {
                it.imports.any { import -> isProjectImportFrom(import.name, "feature") }
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
