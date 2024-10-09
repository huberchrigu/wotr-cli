package ch.chrigu.wotr.action

import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.nation.NationName

data class PoliticsMarkerAction(private val name: NationName) : GameAction { // TODO: Provide a function for that returns the function (like affectedLocations)
    override fun apply(oldState: GameState) = oldState.updateNation(name) { moveDown() }

    override fun requiredDice() = setOf(DieType.MUSTER, DieType.ARMY_MUSTER)

    override fun toString() = "Move politics marker of $name"
}
