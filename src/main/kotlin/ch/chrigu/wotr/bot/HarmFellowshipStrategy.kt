package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.EventType
import ch.chrigu.wotr.action.GameAction
import ch.chrigu.wotr.action.MoveAction
import ch.chrigu.wotr.action.PlayEventAction
import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.player.Player
import org.jline.terminal.Terminal
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(1)
@Component
class HarmFellowshipStrategy(private val terminal: Terminal) : BotStrategy {
    override fun getActions(state: GameState) = getEventAction() +
            getNazgulAction(state) +
            getArmyAction(state)

    private fun getNazgulAction(state: GameState) = state.findAll { it.isNazgulOrWitchKing() }
        .map { (location, figure) -> MoveAction(location.name, state.fellowshipLocation.name, Figures(listOf(figure))) }

    private fun getArmyAction(state: GameState): List<GameAction> {
        val fellowshipLocation = state.fellowshipLocation
        val armies = fellowshipLocation.adjacentArmies(Player.SHADOW, state)
        return armies.map {
            val from = state.getLocationWith(it)!!
            MoveAction(from.name, fellowshipLocation.name, Figures(listOf(regularIfPossible(it))))
        }
    }

    private fun getEventAction() = listOf(PlayEventAction(EventType.CHARACTER, terminal))

    private fun regularIfPossible(figures: List<Figure>) = figures.firstOrNull { it.type == FigureType.REGULAR } ?: figures.first { it.type.isUnit }
}
