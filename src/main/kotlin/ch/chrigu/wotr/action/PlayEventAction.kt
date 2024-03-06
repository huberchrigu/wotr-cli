package ch.chrigu.wotr.action

import ch.chrigu.wotr.card.EventType
import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.gamestate.GameState
import org.jline.terminal.Terminal

data class PlayEventAction(private val type: EventType, private val terminal: Terminal) : GameAction {
    override fun apply(oldState: GameState): GameState {
        val newState = oldState.copy(cards = oldState.cards.play(type))
        terminal.writer().println("Search your $type deck for the first card whose requirement can be met and that will alter the game state. Play it.")
        return newState
    }

    override fun simulate(oldState: GameState): GameState {
        return oldState.copy(cards = oldState.cards.play(type))
    }

    override fun requiredDice() = setOf(DieType.EVENT) + if (type == EventType.CHARACTER)
        setOf(DieType.CHARACTER)
    else
        setOf(DieType.ARMY, DieType.ARMY_MUSTER, DieType.MUSTER)

    override fun toString() = "Play $type event"
}
