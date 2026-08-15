package io.nicolaszurbuchen.yadlo.infra.platform

/**
 * What kind of binary this is, supplied by each platform at Koin start-up rather than detected.
 *
 * Android reads `BuildConfig.DEBUG` and iOS reads `Platform.isDebugBinary`; both are known at the
 * call site and neither is reachable from common code. Passing it in keeps the answer where the
 * platform already is, instead of adding an expect/actual pair for one boolean.
 */
data class BuildFlags(
    val isDebug: Boolean,
)
