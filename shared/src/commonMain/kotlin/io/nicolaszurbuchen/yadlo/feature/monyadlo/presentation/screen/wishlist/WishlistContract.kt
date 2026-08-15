package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist

import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model.WishlistGroup

/** Empty: the rows navigate and nothing else on this screen can be operated. */
sealed interface WishlistIntent

/** Empty: nothing here leaves the app. */
sealed interface WishlistLabel

sealed interface WishlistAction {
    data object ObserveContent : WishlistAction
}

sealed interface WishlistMessage {
    data class GroupsUpdated(
        val groups: List<WishlistGroup>,
    ) : WishlistMessage
}

/**
 * **No clock.** Everything else that lists Slots ticks; this one lists Stands, and DECISIONS.md
 * § No opening times on the Wishlist is why there is nothing here for a tick to change. Whether a
 * stand closes before the festival does is unknown, so the screen says nothing rather than
 * something invented.
 *
 * A null [groups] is "not read yet", which is a different screen from a Wishlist with nothing on it.
 */
data class WishlistState(
    val groups: List<WishlistGroup>? = null,
)
