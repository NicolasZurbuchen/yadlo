package io.nicolaszurbuchen.yadlo.infra.platform

/**
 * What binary this is, supplied by each platform at Koin start-up rather than detected.
 *
 * Android reads `BuildConfig`, iOS reads `Platform.isDebugBinary` and its own bundle; all of it is
 * known at the call site and none of it is reachable from common code. Passing it in keeps the
 * answers where the platform already is, instead of an expect/actual pair per field.
 *
 * [version] is the marketing version — `1.0`, not the build number. It is what somebody writes in an
 * email when something looks wrong, which is the entire reason it is shown at all.
 */
data class BuildFlags(
    val isDebug: Boolean,
    val version: String,
)
