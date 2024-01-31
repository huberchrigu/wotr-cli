package ch.chrigu.wotr.action

import ch.chrigu.wotr.gamestate.GameState

class DieActionFactory(private val state: GameState) {
    fun everyCombination(action: GameAction) = state.dice.shadow.getDice(action.requiredDice())
        .map { DieAction(it, action) }
}
