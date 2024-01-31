package ch.chrigu.wotr.action

import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName

class MoveAction(private val fromLocation: LocationName, private val toLocation: LocationName, private val figures: Figures) : GameAction {
    override fun apply(oldState: GameState) = oldState.removeFrom(fromLocation, figures).addTo(toLocation, figures)

    override fun tryToCombine(other: GameAction): GameAction? {
        if (other is MoveAction && fromLocation == other.fromLocation && toLocation == other.toLocation) {
            return MoveAction(fromLocation, toLocation, figures + other.figures)
        }
        return null
    }

    override fun toString() = "Move $figures from $fromLocation to $toLocation"
    fun isCharacterMovement() = figures.all.all { it.isCharacterOrNazgul() }
    fun isArmyMovement() = figures.all.any { it.type.isUnit }
    override fun requiredDice() = setOf(DieType.CHARACTER, DieType.ARMY, DieType.ARMY_MUSTER)
}
