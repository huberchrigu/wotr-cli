package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.DieActionFactory
import ch.chrigu.wotr.action.GameAction
import ch.chrigu.wotr.gamestate.GameState
import org.springframework.stereotype.Component

@Component
class BotActionFactory(private val strategies: List<BotStrategy>) {
    fun getNext(state: GameState): GameAction {
        val dieActionFactory = DieActionFactory(state)
        val singleActions = strategies.flatMap { it.getActions(state) }
            .flatMap { withDice(it, dieActionFactory) }
        val combinedActions = combine(singleActions.first(), singleActions.drop(1))
        return combinedActions
            .maxBy { evaluate(it, state) }
    }

    private fun combine(action: GameAction, remainder: List<GameAction>): List<GameAction> {
        val combinations = remainder.mapNotNull { it.tryToCombine(action) } + remainder
        return listOf(action) + combine(combinations.first(), combinations.drop(1))
    }

    private fun withDice(action: GameAction, dieActionFactory: DieActionFactory) = if (action is ThrowDiceStrategy)
        listOf(action)
    else
        dieActionFactory.everyCombination(action)

    private fun evaluate(action: GameAction, state: GameState): Int {
        val newState = action.simulate(state)
        return BotEvaluationService.count(newState)
    }
}
