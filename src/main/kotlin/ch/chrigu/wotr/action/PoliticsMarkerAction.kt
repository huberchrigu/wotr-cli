package ch.chrigu.wotr.action

import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.nation.NationName

data class PoliticsMarkerAction(private val name: NationName) : GameAction {
    override fun alteredObjects(state: GameState) = listOfNotNull(state.nation[name])

    override fun apply(oldState: GameState) = oldState.updateNation(name) { moveDown() }

    override fun requiredDice() = setOf(DieType.MUSTER, DieType.ARMY_MUSTER)

    override fun toString() = "Move politics marker of $name"
}
