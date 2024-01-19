package ch.chrigu.wotr.action

import ch.chrigu.wotr.dice.DieUsage
import ch.chrigu.wotr.gamestate.GameState

class DieAction(private val use: DieUsage, private val action: GameAction) : GameAction {
    override fun apply(oldState: GameState): GameState {
        return oldState.useDie(use).let { action.apply(it) }
    }
}
