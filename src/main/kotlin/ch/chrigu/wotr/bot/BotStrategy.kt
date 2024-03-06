package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.GameAction
import ch.chrigu.wotr.gamestate.GameState

interface BotStrategy {
    /**
     * All actions that make sense for this strategy.
     */
    fun getActions(state: GameState): List<GameAction>
}
