package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.AssignEyesAndThrowDiceAction
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
            .toSet()
        val combinedActions = combine(singleActions.first(), singleActions.drop(1).toSet()) // TODO: Find a way to reduce num of iterations (StackOverflowException)
        return combinedActions
            .maxBy { evaluate(it, state) }
    }

    private fun combine(action: GameAction, remainder: Set<GameAction>, depth: Int = 0): Set<GameAction> {
        if (remainder.isEmpty()) return setOf(action)
        val combinations = remainder.mapNotNull { it.tryToCombine(action) } + remainder
        return setOf(action) + combine(combinations.first(), combinations.drop(1).toSet(), depth + 1)
    }

    private fun withDice(action: GameAction, dieActionFactory: DieActionFactory) = if (action is AssignEyesAndThrowDiceAction)
        listOf(action)
    else
        dieActionFactory.everyCombination(action)

    private fun evaluate(action: GameAction, state: GameState): Int {
        val newState = action.simulate(state)
        return BotEvaluationService.count(newState)
    }
}
