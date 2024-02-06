package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.AttackAction
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.player.Player
import org.jline.terminal.Terminal
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(4)
@Component
class AttackStrategy(private val terminal: Terminal) : BotStrategy {
    override fun getActions(state: GameState) = state.location.values.flatMap { location ->
        val nonBesiegedFigures = location.nonBesiegedFigures
        if (location.besiegedFigures.armyPlayer == Player.SHADOW) {
            listOf(AttackAction(terminal, location.besiegedFigures, nonBesiegedFigures, location.name))
        } else if (nonBesiegedFigures.armyPlayer == Player.SHADOW) {
            getSiegeAttack(location) + location.adjacentLocations.mapNotNull { adjacent ->
                getFieldBattle(location, state.location[adjacent]!!)
            }
        } else {
            emptyList()
        }
    }

    private fun getFieldBattle(location: Location, adjacent: Location): AttackAction? {
        val defender = adjacent.nonBesiegedFigures
        return if (defender.armyPlayer == Player.FREE_PEOPLE)
            AttackAction(terminal, location.nonBesiegedFigures, defender, location.name, adjacent.name)
        else
            null
    }

    private fun getSiegeAttack(location: Location) = if (location.besiegedFigures.armyPlayer == Player.FREE_PEOPLE)
        listOf(AttackAction(terminal, location.nonBesiegedFigures, location.besiegedFigures, location.name))
    else
        emptyList()
}
