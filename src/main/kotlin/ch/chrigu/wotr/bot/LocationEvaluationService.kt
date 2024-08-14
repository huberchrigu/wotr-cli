package ch.chrigu.wotr.bot

import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.NumberedLevel
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationFinder
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.location.LocationType
import ch.chrigu.wotr.player.Player
import org.slf4j.LoggerFactory
import kotlin.math.max
import kotlin.math.min

class LocationEvaluationService(private val state: GameState) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Evaluates the score for this location:
     * * Every single [figure gives some points][scoreForFigure].
     * * If there is an army, for any possible [capturing of an enemy-controller settlement][scoreForOccupation].
     * * Also, [count points for any near enemy army][scoreForBattle], including [sieges][scoreForSiege].
     * * Also, [count points for any possible army join][scoreForJoin].
     */
    fun scoreFor(location: Location): Int {
        val figurePoints = location.allFigures().sumOf { scoreForFigure(it) }
        logger.debug("figurePoints@{}: {}", location.name, figurePoints)
        val figures = location.nonBesiegedFigures
        val armyPlayer = figures.armyPlayer ?: return figurePoints
        return figurePoints + LocationFinder.getNearestLocations(location.name)
            .filter { (_, distance) -> distance < 10 }
            .flatMap { (to, _) -> LocationFinder.getShortestPath(location.name, to) }
            .map { scoreFor(location, it.locations) }
            .filter { it > 0 }
            .sortedDescending()
            .take(5)
            .sum() * armyPlayer.toModifier()
    }

    private fun scoreFor(from: Location, to: List<LocationName>) = if (to.isEmpty()) {
        if (!from.besiegedFigures.isEmpty()) scoreForSiege(from) else 0
    } else {
        val toLocation = state.location[to.last()]
        val toPlayer = toLocation?.nonBesiegedFigures?.armyPlayer
        val fromPlayer = from.nonBesiegedFigures.armyPlayer!!
        if (toPlayer == fromPlayer.opponent)
            scoreForBattle(from, to)
        else if (toPlayer == fromPlayer)
            scoreForJoin(from, to)
        else if (toLocation?.currentlyOccupiedBy() == fromPlayer.opponent)
            scoreForOccupation(from, to)
        else
            0
    }

    private fun scoreForSiege(at: Location): Int {
        val defender = armyValue(at.besiegedFigures, LocationType.STRONGHOLD)
        val attacker = armyValue(at.nonBesiegedFigures)
        val points = max(0, Math.round(attacker - defender)).toInt() * 5
        logger.debug("scoreForSiege@{}: {}", at.name, points * at.nonBesiegedFigures.armyPlayer!!.toModifier())
        return points
    }

    private fun scoreForBattle(from: Location, to: List<LocationName>): Int {
        val points = scoreForBattleAgainstAllArmiesOnPath(from, to)
        logger.debug("scoreForBattle@{}: {}", from.name, points * from.nonBesiegedFigures.armyPlayer!!.toModifier())
        return points
    }

    private fun scoreForJoin(from: Location, to: List<LocationName>): Int {
        val fromPlayer = from.nonBesiegedFigures.armyPlayer!!
        if (pathContainsArmyOfPlayer(to, fromPlayer.opponent)) return 0
        val fromSize = from.nonBesiegedFigures.all.size
        val toSize = state.location[to.last()]!!.nonBesiegedFigures.all.size
        if (fromSize == 10 || toSize == 10) return 0
        val points = min(10, fromSize + toSize) / to.size
        logger.debug("scoreForJoin@{}: {}", from.name, points * fromPlayer.toModifier())
        return points
    }

    private fun scoreForOccupation(from: Location, to: List<LocationName>): Int {
        val points = scoreForBattleAgainstAllArmiesOnPath(from, to) * 2
        logger.debug("scoreForOccupation@{}: {}", from.name, points * from.nonBesiegedFigures.armyPlayer!!.toModifier())
        return points
    }

    private fun Player.toModifier() = when (this) {
        Player.SHADOW -> 1
        Player.FREE_PEOPLE -> -1
    }

    private fun scoreForBattleAgainstAllArmiesOnPath(from: Location, to: List<LocationName>): Int {
        val fromPlayer = from.nonBesiegedFigures.armyPlayer!!
        val defender = sumOfAllOpponentArmies(to, fromPlayer)
        val attacker = armyValue(from.nonBesiegedFigures)
        val points = max(0, Math.round(attacker - defender)).toInt() * to.last().type.toOccupationMultiplier() / to.size
        return points * points
    }

    private fun sumOfAllOpponentArmies(to: List<LocationName>, fromPlayer: Player) = to.mapNotNull { state.location[it] }
        .filter { it.nonBesiegedFigures.armyPlayer == fromPlayer.opponent }
        .sumOf { armyValue(it.nonBesiegedFigures, it.type) }

    private fun pathContainsArmyOfPlayer(to: List<LocationName>, player: Player) =
        to.any { state.location[it]?.nonBesiegedFigures?.armyPlayer == player }

    private fun LocationType.toOccupationMultiplier() = when (this) {
        LocationType.STRONGHOLD -> 5
        LocationType.CITY -> 3
        LocationType.VILLAGE -> 2
        else -> 1
    }

    private fun armyValue(army: Figures, defenderType: LocationType = LocationType.NONE): Double {
        val (numElites, numRegulars) = army.getDefenderUnits(defenderType == LocationType.STRONGHOLD)
        return (army.combatRolls() + army.maxReRolls() + numElites * 2.0 + numRegulars) * defenderType.toArmyMultiplier()
    }

    private fun LocationType.toArmyMultiplier() = when (this) {
        LocationType.STRONGHOLD -> 1.6
        LocationType.CITY -> 1.2
        LocationType.FORTIFICATION -> 1.2
        else -> 1.0
    }

    private fun scoreForFigure(figure: Figure): Int {
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
