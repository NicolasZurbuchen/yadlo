package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.design.component.YadloFigureGrid
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.HomeBlockUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * The closing figures of the edition just finished, the ENDED half of story 62.
 *
 * One block with the numbers inside it, not a title over a field of tiles: they are one statement
 * about one edition, and the caveat at the foot applies to all of them at once.
 *
 * The grid itself is [YadloFigureGrid], shared with *L'histoire de Yadlo* — the same three numbers
 * are printed on both screens, and this one used to draw them its own way.
 */
@Composable
fun FiguresBlock(
    block: HomeBlockUiModel.Figures,
    modifier: Modifier = Modifier,
) {
    SectionBlock(title = block.title, modifier = modifier) {
        YadloFigureGrid(figures = block.items, caveat = block.caveat?.asString())
    }
}
