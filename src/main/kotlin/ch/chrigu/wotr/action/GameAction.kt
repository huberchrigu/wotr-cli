package ch.chrigu.wotr.action

import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.gamestate.GameState

interface GameAction {
    fun apply(oldState: GameState): GameState
    fun tryToCombine(other: GameAction): GameAction? = null
    fun requiredDice(): Set<DieType> = throw IllegalStateException("The action ${javaClass.simpleName} does not support to be played within a die action")
}
