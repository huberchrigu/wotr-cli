package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.GameAction
import ch.chrigu.wotr.gamestate.GameState

interface BotStrategy {
    fun getActions(state: GameState): List<ProposedBotAction>
}

data class ProposedBotAction(val points: Int, val action: GameAction)
