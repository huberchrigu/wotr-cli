package ch.chrigu.wotr.action

import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.dice.DieUsage
import ch.chrigu.wotr.gamestate.GameState

class DieAction(private val use: DieUsage, private val actions: List<GameAction>) : GameAction {
    override fun apply(oldState: GameState): GameState {
        return oldState.useDie(use).let { initial -> actions.fold(initial) { state, action -> action.apply(state) } }
    }

    override fun tryToCombine(other: GameAction): GameAction? {
        if (other !is DieAction || use != other.use) return null
        val allActions = actions + other.actions
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
