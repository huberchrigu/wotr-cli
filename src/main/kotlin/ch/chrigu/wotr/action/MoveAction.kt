package ch.chrigu.wotr.action

import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.At
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName
import kotlin.math.max

data class MoveAction(private val fromLocation: LocationName, private val toLocation: LocationName, val figures: Figures) : GameAction {
    init {
        require(!figures.isEmpty()) { "Move action requires at least one figure" }
        require(fromLocation != toLocation) { "Move action must have two different locations" }
    }

    override val alteringLocations = listOf(fromLocation, toLocation)

    override fun apply(oldState: GameState): GameState {
        return oldState.move(figures, At(fromLocation), At(toLocation))
    }

    override fun tryToCombine(other: GameAction): GameAction? {
        if (other is MoveAction && fromLocation == other.fromLocation && toLocation == other.toLocation) {
            val union = figures.union(other.figures)
            if (union.all.size == max(figures.all.size, other.figures.all.size)) return null
            return MoveAction(fromLocation, toLocation, union)
        }
        return null
    }

    override fun toString() = "Move $figures from $fromLocation to $toLocation"
    fun isCharacterMovement() = figures.all.all { it.isCharacterOrNazgul() }
    fun isArmyMovement() = figures.all.any { it.type.isUnit }
    override fun requiredDice() = setOf(DieType.CHARACTER, DieType.ARMY, DieType.ARMY_MUSTER)
}
