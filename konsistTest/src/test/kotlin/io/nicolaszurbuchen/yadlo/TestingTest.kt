package io.nicolaszurbuchen.yadlo

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.ext.list.withSourceSet
import com.lemonappdev.konsist.api.verify.assertTrue
import kotlin.test.Test

class TestingTest {
    companion object {
        private val scope = Konsist.scopeFromModule("shared")
    }

    private fun KoFileDeclaration.hasCorrespondingTestFile(): Boolean = scope.files.any { it.name == "${name}Test" }

    // region test coverage

    @Test
    fun `every mapper file has a corresponding test file`() {
        scope.files
            .withPackage("..mapper..")
            .withNameEndingWith("Mapper")
            .assertTrue { it.hasCorrespondingTestFile() }
    }

    /**
     * **A UiModel file that only declares its type needs no test; one that declares a function
     * does.** This is the category the coverage list was missing, and #73 found four functions
     * living in it untested — including `slotLiveStateAt`, which decides every live pill in the app
     * off the injected clock and had no assertion anywhere on two of its five states.
     *
     * They fell through because none of them is a Mapper, a UseCase, a UiMapper or a StoreFactory.
     * A `uimodel` package is meant to hold the pieces of a screen's vocabulary — types — so a
     * top-level function in one is a rule about the subject that has found a quiet home, and the
     * rules are the part worth pinning.
     *
     * The type-only files stay exempt on purpose. Requiring a test for `PhaseUiModel` would be
     * asking someone to assert that an enum has its own entries.
     */
    @Test
    fun `every uimodel file that declares a function has a corresponding test file`() {
        scope.files
            .withPackage("..uimodel")
            .withSourceSet("commonMain")
            .filter { it.functions(includeNested = false).isNotEmpty() }
            .assertTrue { it.hasCorrespondingTestFile() }
    }

    @Test
    fun `every RepositoryImpl file has a corresponding test file`() {
        scope.files
            .withNameEndingWith("RepositoryImpl")
            .assertTrue { it.hasCorrespondingTestFile() }
    }

    @Test
    fun `every DataSourceImpl file has a corresponding test file`() {
        scope.files
            .withNameEndingWith("DataSourceImpl")
            .assertTrue { it.hasCorrespondingTestFile() }
    }

    @Test
    fun `every UseCase file has a corresponding test file`() {
        scope.files
            .withNameEndingWith("UseCase")
            .assertTrue { it.hasCorrespondingTestFile() }
    }

    @Test
    fun `every UiMapper file has a corresponding test file`() {
        scope.files
            .withNameEndingWith("UiMapper")
            .assertTrue { it.hasCorrespondingTestFile() }
    }

    @Test
    fun `every StoreFactory file has a corresponding ReducerTest and ExecutorTest`() {
        scope.files
            .withNameEndingWith("StoreFactory")
            .assertTrue { file ->
                val prefix = file.name.removeSuffix("StoreFactory")
                scope.files.any { it.name == "${prefix}ReducerTest" } &&
                    scope.files.any { it.name == "${prefix}ExecutorTest" }
            }
    }

    // endregion

    // region test file location

    @Test
    fun `test files reside in commonTest or androidHostTest mirroring their subject's package`() {
        scope.files
            .withNameEndingWith("Test")
            .assertTrue { testFile ->
                (testFile.resideInSourceSet("commonTest") || testFile.resideInSourceSet("androidHostTest")) &&
                    scope.files
                        .withSourceSet("commonMain")
                        .any { it.packagee?.name == testFile.packagee?.name }
            }
    }

    @Test
    fun `every file in androidHostTest ends with Test`() {
        scope.files
            .withSourceSet("androidHostTest")
            .assertTrue { it.name.endsWith("Test") }
    }

    // endregion

    // region fakes

    @Test
    fun `top-level classes prefixed Fake implement an interface and reside in a domain fake package`() {
        scope.classes()
            .filter { it.name.startsWith("Fake") && it.isTopLevel }
            .assertTrue { fakeClass ->
                fakeClass.parents().isNotEmpty() && fakeClass.resideInPackage("..domain.fake")
            }
    }

    // endregion
}
