package io.nicolaszurbuchen.yadlo.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation.HomeMainDestination
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.navigation.MonYadloMainDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PlusMainDestination
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.navigation.ProgrammeDestination
import org.jetbrains.compose.resources.StringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.tab_home
import yadlo.shared.generated.resources.tab_mon_yadlo
import yadlo.shared.generated.resources.tab_plus
import yadlo.shared.generated.resources.tab_programme

/**
 * The four bottom-navigation destinations, in bar order.
 *
 * This lives in `app/` rather than any feature because it is the one place that knows all four
 * exist. Declaration order *is* display order — the bar iterates the entries — so reordering this
 * enum reorders the app.
 */
enum class Tab(
    val root: NavKey,
    val label: StringResource,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    /**
     * Whether this tab draws more blue directly under the bar.
     *
     * The chrome ends in a wave, and it can only end once: a wave drawn where the bar meets
     * another blue block would notch the page's colour into the middle of the chrome. So the
     * two tabs that carry their own band — the chip rows and axis on Programme, the scale
     * strip on Mon Yadlo — take a flat bar and put the wave at the bottom of the band instead.
     */
    val continuesChrome: Boolean = false,
) {
    HOME(
        root = HomeMainDestination,
        label = Res.string.tab_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    ),
    PROGRAMME(
        root = ProgrammeDestination,
        label = Res.string.tab_programme,
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
        continuesChrome = true,
    ),
    MON_YADLO(
        root = MonYadloMainDestination,
        label = Res.string.tab_mon_yadlo,
        continuesChrome = true,
        // The heart is the save affordance everywhere else in the app, so the tab that holds
        // saved Slots wears the same symbol rather than inventing a second vocabulary for it.
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
    ),
    PLUS(
        root = PlusMainDestination,
        label = Res.string.tab_plus,
        selectedIcon = Icons.Filled.MoreHoriz,
        unselectedIcon = Icons.Outlined.MoreHoriz,
    ),
}
