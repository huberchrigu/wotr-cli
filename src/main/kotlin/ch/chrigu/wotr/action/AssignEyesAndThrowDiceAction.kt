package ch.chrigu.wotr.action

import ch.chrigu.wotr.gamestate.GameState

class AssignEyesAndThrowDiceAction(private val numEyes: Int) : GameAction {
    override fun apply(oldState: GameState): GameState {
        return oldState.copy(dice = oldState.dice.assignEyesAndRollDice(oldState, numEyes), cards = oldState.cards.phase1())
    }

    override fun toString() = "Assign $numEyes and roll dice"
}
