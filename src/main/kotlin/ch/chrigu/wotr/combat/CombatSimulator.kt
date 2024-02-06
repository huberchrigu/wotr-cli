package ch.chrigu.wotr.combat

import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.location.LocationType
import kotlin.math.min
import kotlin.math.round
import kotlin.random.Random

class CombatSimulator(
    private val attacker: Figures,
    private val defender: Figures,
    private val combatType: CombatType,
    private val locationType: LocationType,
    private val attackerLocation: LocationName,
    private val defenderLocation: LocationName
) {
    fun repeat(num: Int): List<Casualties> {
        val requires = if (combatType == CombatType.SIEGE)
            6
        else if (locationType == LocationType.CITY || locationType == LocationType.FORTIFICATION)
            6
        else
            5
        val attackerDice = CombatDice(attacker.combatRolls(), attacker.maxReRolls(), requires)
        val defenderDice = CombatDice(defender.combatRolls(), defender.maxReRolls(), 5)
        val hits = (0 until num).map { attackerDice.roll() to defenderDice.roll() }
            .fold(0.0 to 0.0) { (a1, d1), (a2, d2) -> a1 + a2 to d1 + d2 }
            .let { (a, d) -> round(a / num) to round(d / num) }
        return listOf(Casualties(defenderLocation, defender, hits.first.toInt()), Casualties(attackerLocation, attacker, hits.second.toInt()))
    }
}

data class Casualties(private val at: LocationName, private val from: Figures, private val hits: Int) {
    fun apply(state: GameState): GameState {
        TODO("Use kill and downgrade action")
    }
}

data class CombatDice(private val combatRolls: Int, private val maxReRolls: Int, val requires: Int) {
    init {
        require(combatRolls in 1..5)
        require(maxReRolls in 0..5)
        require(requires in 5..6)
    }

    fun roll(): Int {
        val dice = rollDice(combatRolls)
        val ok = dice.count { it >= requires }
        val nok = dice.count { it < requires }
        val reRoll = rollDice(min(nok, maxReRolls)).count { it >= requires }
        return ok + reRoll
    }

    private fun rollDice(num: Int) = (0 until num).map { Random.nextInt(6) + 1 }
}
