package ch.chrigu.wotr.action

import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.At
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.nation.NationName
import kotlin.math.max

data class MoveAction(private val fromLocation: LocationName, private val toLocation: LocationName, val figures: Figures) : GameAction {
    init {
        require(!figures.isEmpty()) { "Move action requires at least one figure" }
        require(fromLocation != toLocation) { "Move action must have two different locations" }
    }

    override val alteringLocations = listOf(fromLocation, toLocation)

    override fun apply(oldState: GameState): GameState {
        if (toLocation.nation != null) {
            val foreignNationsNotAtWar = getArmyNations()
                .filter { isForeign(it) }
                .mapNotNull { oldState.nation[it] }
                .filter { !it.isAtWar() }
                .toList()
            check(foreignNationsNotAtWar.isEmpty()) { "Figures not at war cannot move: $foreignNationsNotAtWar" }
        }
        return oldState.move(figures, At(fromLocation), At(toLocation))
    }

    private fun isForeign(it: NationName) = it != toLocation.nation

    private fun getArmyNations() = figures.getArmy().asSequence()
        .map { it.nation }
        .distinct()

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
