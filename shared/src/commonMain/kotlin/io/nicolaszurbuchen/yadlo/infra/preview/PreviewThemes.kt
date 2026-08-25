package io.nicolaszurbuchen.yadlo.infra.preview

import androidx.compose.ui.tooling.preview.Preview

/**
 * Light and dark, from one preview function — the multipreview pattern.
 *
 * **This is what replaces the two-functions-per-file shape.** Every preview in the app used to be
 * written twice: `FooScreenPreview` and `FooScreenDarkPreview`, identical but for the `darkTheme`
 * argument, which meant a change to the fixtures had to be made in both and a preview that drifted
 * apart from its own dark twin looked fine in review. One annotated function renders both, and a
 * third rendering — a large font scale, a small screen — becomes one line here rather than
 * twenty-three new functions.
 *
 * The theme still comes from the system rather than from a parameter: `YadloTheme` reads
 * `isSystemInDarkTheme()`, and the tooling sets exactly that from `uiMode`. So the dark rendering
 * is the real dark theme rather than a preview-only override, which is the point of not passing
 * `darkTheme` by hand.
 *
 * **It is `infra/` rather than `app/design/` because it does not know this app exists.** It sets a
 * system flag; that `YadloTheme` happens to read the flag is the theme's business, not this
 * file's. Nothing here names a colour, a font or a screen, and the whole annotation would work
 * unchanged in any Compose app — which is the placement rule's definition of plumbing.
 */
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = PreviewUiMode.NIGHT_YES)
annotation class PreviewThemes
