package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.GameAction
import ch.chrigu.wotr.action.MoveAction
import ch.chrigu.wotr.action.PlayEventAction
import ch.chrigu.wotr.card.EventType
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
        .flatMap { figures ->
            location.adjacentLocations.map { MoveAction(location.name, it, figures) }
        }

    private fun combinations(of: Figures): List<Figures> {
        val characterCombinations = Combinations.allSizes(of.characters().map { it.type })
            .map { of.subSet(it) }
        return (0..of.numRegulars()).flatMap { numRegular ->
            (0..of.numElites()).flatMap { numElite ->
                (0..of.numLeadersOrNazgul()).map { numLeader ->
                    of.subSet(numRegular, numElite, numLeader, null)
                }
            }
        }
            .filter { !it.isEmpty() }
            .flatMap { combineWithCharacters(it, characterCombinations) }
    }

    private fun combineWithCharacters(figuresWithoutCharacters: Figures, characterCombinations: List<Figures>) = characterCombinations.map { figuresWithoutCharacters + it }
}
