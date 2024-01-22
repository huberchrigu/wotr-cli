package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.GameAction
import ch.chrigu.wotr.gamestate.GameState
import org.springframework.stereotype.Component

@Component
class BotActionFactory(private val strategies: List<BotStrategy>) {
    fun getNext(state: GameState): GameAction {
        return strategies.flatMap { it.getActions(state) }
            .maxBy { it.points }
            .action
    }
}
