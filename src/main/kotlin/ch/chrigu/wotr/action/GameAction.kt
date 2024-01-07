package ch.chrigu.wotr.action

import ch.chrigu.wotr.gamestate.GameState

interface GameAction {
    fun apply(oldState: GameState): GameState
}
