package ch.chrigu.wotr.action

import ch.chrigu.wotr.card.EventType
import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.gamestate.GameState

data class DrawEventAction(private val type: EventType) : GameAction {
    override fun apply(oldState: GameState): GameState {
        return oldState.copy(cards = oldState.cards.draw(type))
    }

    override fun requiredDice() = setOf(DieType.EVENT)

    override fun toString() = "Draw $type event"
}
