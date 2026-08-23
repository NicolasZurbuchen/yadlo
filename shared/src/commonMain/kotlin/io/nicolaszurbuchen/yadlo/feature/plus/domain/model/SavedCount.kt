package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

/**
 * How much the visitor has kept, split the way they kept it.
 *
 * **Two numbers rather than one, even though one button removes both.** *Mon programme* and *à
 * essayer* are two verbs everywhere else in the app (CONTEXT.md § The user's own festival), and a
 * screen offering to delete them would be the first place they became one undifferentiated pile —
 * which is exactly the screen where somebody most needs to know what they are about to lose.
 *
 * [isEmpty] is what the button reads. Nothing to remove is a state worth drawing, not an error:
 * this screen is reachable at any time, including before anything has been saved at all.
 */
data class SavedCount(
    val slots: Int,
    val stands: Int,
) {
    val isEmpty: Boolean
        get() = slots == 0 && stands == 0
}
