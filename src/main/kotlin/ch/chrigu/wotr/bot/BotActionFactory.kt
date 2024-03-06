package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.AssignEyesAndThrowDiceAction
import ch.chrigu.wotr.action.DieActionFactory
import ch.chrigu.wotr.action.GameAction
import ch.chrigu.wotr.gamestate.GameState
import org.springframework.stereotype.Component
import java.util.*
import kotlin.math.min

@Component
class BotActionFactory(private val strategies: List<BotStrategy>) {
    fun getNext(state: GameState): GameAction {
        val dieActionFactory = DieActionFactory(state)
        val singleActions = strategies.asSequence().flatMap { it.getActions(state) }
            .flatMap { withDice(it, dieActionFactory) }
            .toSet()
            .mapNotNull { EvaluatedAction.create(it, state) }
            .toSortedSet()
        check(singleActions.isNotEmpty()) { "There is no possible bot action" }
        val combinedActions = combineFirst(min(10, singleActions.size), singleActions)
        return combinedActions.first().action
    }

    private fun combineFirst(num: Int, actions: SortedSet<EvaluatedAction>): SortedSet<EvaluatedAction> {
        val combinations = (0 until num).flatMap { index ->
            val action = actions.drop(index).first()
            actions.drop(index + 1).mapNotNull { a -> a.tryToCombine(action) }
        }
        actions.addAll(combinations)
        return if (num == 1) actions else combineFirst(num - 1, actions)
    }

    private fun withDice(action: GameAction, dieActionFactory: DieActionFactory) = if (action is AssignEyesAndThrowDiceAction)
        listOf(action)
    else
        dieActionFactory.everyCombination(action)

    companion object {
        private fun evaluate(action: GameAction, state: GameState): Int? {
            val newState: GameState
            try {
                newState = action.simulate(state)
            } catch (e: IllegalArgumentException) {
                return null
            }
            return BotEvaluationService.count(newState)
        }
    }

    data class EvaluatedAction(val action: GameAction, val score: Int, val state: GameState) : Comparable<EvaluatedAction> {

        override fun compareTo(other: EvaluatedAction): Int {
            return score.compareTo(other.score)
        }

        override fun toString() = "Score $score: $action"
        fun tryToCombine(other: EvaluatedAction): EvaluatedAction? {
            val combined = action.tryToCombine(other.action)
            return if (combined == null) null else create(combined, state)
        }

        companion object {
            fun create(action: GameAction, state: GameState) = evaluate(action, state)?.let { EvaluatedAction(action, it, state) }
        }
    }
}
