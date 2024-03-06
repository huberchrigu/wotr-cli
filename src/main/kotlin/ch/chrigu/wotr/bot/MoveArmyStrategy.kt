package ch.chrigu.wotr.bot

import ch.chrigu.wotr.card.EventType
import ch.chrigu.wotr.action.GameAction
import ch.chrigu.wotr.action.MoveAction
import ch.chrigu.wotr.action.PlayEventAction
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.player.Player
import org.jline.terminal.Terminal
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(3)
@Component
class MoveArmyStrategy(private val terminal: Terminal) : BotStrategy {
    override fun getActions(state: GameState): List<GameAction> {
        return (state.location.values
            .filter { it.nonBesiegedFigures.armyPlayer == Player.SHADOW }
            .flatMap { toMoveActions(it) }) +
                listOf(PlayEventAction(EventType.STRATEGY, terminal))
    }

    private fun toMoveActions(location: Location) = combinations(location.nonBesiegedFigures)
        .filter { !it.isEmpty() }
        .flatMap { figures ->
            location.adjacentLocations.map { MoveAction(location.name, it, figures) }
        }

    private fun combinations(of: Figures) = (0 until of.numRegulars()).flatMap { numRegular ->
        (0 until of.numElites()).flatMap { numElite ->
            (0 until of.numLeadersOrNazgul()).map { numLeader ->
                of.subSet(numRegular, numElite, numLeader, null)
            }
        }
    }
        .flatMap { figures -> Combinations.allSizes(figures.characters().map { it.type }).map { figures.subSet(it) } }
}
