package ch.chrigu.wotr.bot

import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.NumberedLevel
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationType
import ch.chrigu.wotr.player.Player
import org.slf4j.LoggerFactory
import kotlin.math.max

class LocationEvaluationService(private val state: GameState) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun scoreFor(location: Location): Int {
        val figurePoints = location.allFigures().sumOf { countFigure(it) }
        val figures = location.nonBesiegedFigures
        val armyPlayer = figures.armyPlayer
        val playerModifier = if (armyPlayer == Player.SHADOW) 1 else if (armyPlayer == Player.FREE_PEOPLE) -1 else 0
        val siegeModifier = if (location.besiegedFigures.isEmpty()) 1 else 3
        val nearestLocationsToOccupy = location.distanceTo(state) { it.currentlyOccupiedBy()?.opponent == armyPlayer }
        val nearestArmies = if (location.besiegedFigures.isEmpty())
            location.nearestLocationWith(state) { it.nonBesiegedFigures.armyPlayer?.opponent == armyPlayer }
                .map { (l, distance) -> Triple(l, l.nonBesiegedFigures, distance) }
                .toList()
        else
            listOf(Triple(location, location.besiegedFigures, 0))
        val nearArmyThreat = nearestArmies.sumOf { (location, defender, distance) -> pointsAgainstArmy(figures, defender, distance, location) }
        val nearSettlementThreat = nearestLocationsToOccupy.entries.sumOf { (l, distance) -> pointsForOccupation(figures, l, distance) }
        val armyPoints = nearArmyThreat * playerModifier * siegeModifier
        val settlementPoints = nearSettlementThreat * playerModifier
        logger.debug("Points for {}: {} figurePoints + {} armyPoints + {} settlementPoints", location, figurePoints, armyPoints, settlementPoints)
        return figurePoints + armyPoints + settlementPoints
    }

    private fun pointsForOccupation(attacker: Figures, location: Location, distance: Int): Int {
        val defender = if (distance == 0) location.besiegedFigures else location.nonBesiegedFigures
        return if (defender.armyPlayer == null)
            max(50 - distance * 2, 0) * (location.victoryPoints * 10 + 1)
        else
            pointsAgainstArmy(attacker, defender, distance, location) * (location.victoryPoints * 2 + 1)
    }

    private fun pointsAgainstArmy(attacker: Figures, defender: Figures, distance: Int, location: Location): Int {
        val stronghold = distance == 0 || location.type == LocationType.STRONGHOLD
        val defenderBonus = if (stronghold)
            2.5f
        else if (location.type == LocationType.CITY || location.type == LocationType.FORTIFICATION)
            1.5f
        else
            1.0f
        val maxPoints = armyValue(attacker) - Math.round(armyValue(defender) * defenderBonus)
        return max(0, maxPoints) * (5 - distance)
    }

    private fun armyValue(army: Figures) = army.combatRolls() + army.maxReRolls() + army.numElites() * 2 + army.numRegulars()

    private fun countFigure(figure: Figure): Int {
        val modifier = if (figure.nation.player == Player.SHADOW) 1 else -1
        val type = figure.type
        val points = if (type.isUniqueCharacter)
            ((type.level as? NumberedLevel)?.number ?: 0) * 2 + type.enablesDice * 10
        else if (type == FigureType.ELITE)
            3
        else
            1
        return modifier * points
    }
}
