package ch.chrigu.wotr.action

import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.dice.DieUsage
import ch.chrigu.wotr.gamestate.GameState

data class DieAction(private val use: DieUsage, private val actions: List<GameAction>) : GameAction {
    override fun apply(oldState: GameState): GameState {
        return oldState.useDie(use).let { initial -> actions.fold(initial) { state, action -> action.apply(state) } }
    }

    override fun simulate(oldState: GameState): GameState {
        return oldState.useDie(use).let { initial -> actions.fold(initial) { state, action -> action.simulate(state) } }
    }

    override fun tryToCombine(other: GameAction): GameAction? {
        if (other !is DieAction || use != other.use) return null
        val combined = if (actions.size == 1 && other.actions.size == 1)
            actions[0].tryToCombine(other.actions[0])
        else null
        if (combined != null) return DieAction(use, listOf(combined))
        val allActions = actions + other.actions
        if (!allActions.all { it is MoveAction }) return null
        val figures1 = actions.map { (it as MoveAction).figures }.reduce { a, b -> a + b }
        val figures2 = other.actions.map { (it as MoveAction).figures }.reduce { a, b -> a + b }
        if (!figures1.intersect(figures2).isEmpty()) return null
        if (allActions.all { isCharacterMovement(it) }) {
            return DieAction(use, allActions)
        } else if (allActions.size == 2 && allActions.all { isArmyMovement(it) } && (use.use != DieType.CHARACTER || use.useRing)) {
            return DieAction(use, allActions)
        }
        return null
    }

    private fun isCharacterMovement(action: GameAction) = action is MoveAction && action.isCharacterMovement()
    private fun isArmyMovement(action: GameAction) = action is MoveAction && action.isArmyMovement()

    override fun toString() = "Use $use to $actions"
}
