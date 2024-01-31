package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.GameAction
import ch.chrigu.wotr.gamestate.GameState

interface BotStrategy {
    /**
     * All actions that make sense for this strategy.
     */
    fun getActions(state: GameState): List<GameAction>
}

@Deprecated("Use actions and evaluate points later")
data class ProposedBotAction(val points: Int, val action: GameAction)
