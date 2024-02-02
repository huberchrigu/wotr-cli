package ch.chrigu.wotr.bot

import ch.chrigu.wotr.dice.Dice
import ch.chrigu.wotr.dice.DiceAndRings
import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.fellowship.Fellowship
import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.NumberedLevel
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationType
import ch.chrigu.wotr.player.Player
import kotlin.math.max

object BotEvaluationService {
    fun count(state: GameState) = count(state.dice) +
            countVp(state) +
            count(state.fellowship, state) +
            state.location.values.sumOf { count(it, state) }

    private fun countVp(state: GameState) = if (state.vpShadow() >= 10)
        100
    else if (state.vpFreePeople() >= 4)
        90
    else
        5 * state.vpShadow() - 10 * state.vpFreePeople()

    private fun count(dice: Dice) = count(dice.shadow, Player.SHADOW) - count(dice.freePeople, Player.FREE_PEOPLE)
    private fun count(diceAndRings: DiceAndRings, player: Player) = 10 * diceAndRings.rings + diceAndRings.rolled
        .groupBy { it }.entries.sumOf { (type, list) ->
            when (type) {
                DieType.WILL_OF_THE_WEST -> 3
                DieType.ARMY_MUSTER -> max(3, list.size)
                DieType.ARMY -> if (player == Player.SHADOW) 2 else 1
                DieType.CHARACTER -> if (player == Player.FREE_PEOPLE) 2 else 1
                else -> 1
            }
        }

    private fun count(fellowship: Fellowship, state: GameState) = if (fellowship.corruption >= Fellowship.MAX_CORRUPTION)
        100
    else if (fellowship.mordor != null && fellowship.mordor >= Fellowship.MORDOR_STEPS)
        -100
    else
        fellowship.corruption * 2 - fellowship.progress - (fellowship.mordor ?: 0) * 2 +
                (if (fellowship.discovered) 5 else 0) +
                fellowship.numRerolls(state) * 5 -
                state.companions.sumOf { (it.type.level as? NumberedLevel)?.number ?: 0 } * 2

    private fun count(location: Location, state: GameState): Int {
        val figurePoints = location.allFigures().sumOf { count(it) }
        val figures = location.nonBesiegedFigures
        val armyPlayer = figures.armyPlayer ?: figurePoints
        val playerModifier = if (armyPlayer == Player.SHADOW) 1 else -1
        val siegeModifier = if (location.besiegedFigures.isEmpty()) 1 else 3
        val nearestLocationsToOccupy = location.distanceTo(state) { it.currentlyOccupiedBy()?.opponent == armyPlayer }
        val nearestArmies = if (location.besiegedFigures.isEmpty())
            location.distanceTo(state) { it.nonBesiegedFigures.armyPlayer?.opponent == armyPlayer }
                .map { (l, distance) -> Triple(l, l.nonBesiegedFigures, distance) }
        else
            listOf(Triple(location, location.besiegedFigures, 0))
        val nearArmyThreat = nearestArmies.sumOf { (location, defender, distance) -> pointsAgainstArmy(figures, defender, distance, location) }
        val nearSettlementThreat = nearestLocationsToOccupy.entries.sumOf { (l, distance) -> pointsForOccupation(figures, l, distance) }
        return figurePoints +
                nearArmyThreat * playerModifier * siegeModifier +
                nearSettlementThreat * playerModifier
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
        return max(0, armyValue(attacker) - Math.round(armyValue(defender) * defenderBonus))
    }

    private fun armyValue(army: Figures) = army.combatRolls() + army.maxReRolls() + army.numElites() * 2 + army.numRegulars()

    private fun count(figure: Figure): Int {
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
