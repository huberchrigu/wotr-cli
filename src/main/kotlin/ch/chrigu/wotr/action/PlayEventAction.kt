package ch.chrigu.wotr.action

import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.gamestate.GameState
import org.jline.terminal.Terminal

data class PlayEventAction(private val type: EventType, private val terminal: Terminal) : GameAction {
    override fun apply(oldState: GameState): GameState {
        terminal.writer().println("Search your $type deck for the first card whose requirement can be met and that will alter the game state. Play it.")
        return oldState
    }

    override fun simulate(oldState: GameState): GameState {
        return oldState
    }

    override fun requiredDice() = setOf(DieType.EVENT) + if (type == EventType.CHARACTER)
        setOf(DieType.CHARACTER)
    else
        setOf(DieType.ARMY, DieType.ARMY_MUSTER, DieType.MUSTER)

    override fun toString() = "Play $type event"
}

enum class EventType {
    CHARACTER, STRATEGY;

    override fun toString() = name.lowercase()
}
