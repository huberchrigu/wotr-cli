package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.AssignEyesAndThrowDiceAction
import ch.chrigu.wotr.action.DieActionFactory
import ch.chrigu.wotr.action.GameAction
import ch.chrigu.wotr.gamestate.GameState
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*
import kotlin.math.min

@Component
class BotActionFactory(private val strategies: List<BotStrategy>) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private var startTime: Long = 0

    fun getNext(state: GameState): GameAction {
        val dieActionFactory = DieActionFactory(state)
        startTimer()
        val singleActions = strategies.asSequence()
            .flatMap { it.getActions(state).also { actions -> logTimer { "${actions.size} for strategy $it" } } }
            .flatMap { withDice(it, dieActionFactory) }
            .toSet()
            .also { logTimer { "Found ${it.size} actions" } }
            .mapNotNull { EvaluatedAction.create(it, state) }
            .toSortedSet()
        logTimer { "Found ${singleActions.size} evaluated actions" }
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
        logTimer { "Added ${combinations.size} combinations" }
        return if (num == 1) actions else combineFirst(num - 1, actions)
    }

    private fun withDice(action: GameAction, dieActionFactory: DieActionFactory) = if (action is AssignEyesAndThrowDiceAction)
        listOf(action)
    else
        dieActionFactory.everyCombination(action)

    private fun startTimer() {
        if (logger.isDebugEnabled) startTime = System.currentTimeMillis()
    }

    private fun logTimer(message: () -> String) {
        if (logger.isDebugEnabled) {
            val time = (System.currentTimeMillis() - startTime) / 1000.0
            logger.debug(message() + " ($time s)")
            startTime = System.currentTimeMillis()
        }
    }

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
