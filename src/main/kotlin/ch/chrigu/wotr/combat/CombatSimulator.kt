package ch.chrigu.wotr.combat

import ch.chrigu.wotr.action.KillAction
import ch.chrigu.wotr.action.MusterAction
import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.FiguresType
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
    fun apply(state: GameState) = initActions(state.reinforcements).fold(state) { a, b -> b.apply(a) }

    private fun initActions(reinforcements: Figures) = takeCasualty(from.copy(type = FiguresType.REINFORCEMENTS), hits, reinforcements.all.filter { it.type == FigureType.REGULAR })
        .fold(Casualty.zero()) { a, b -> a + b }
        .let {
            listOfNotNull(
                if (it.killed.isEmpty()) null else KillAction(at, it.killed), // TODO: GameState should not allow adding and removing figure, just moving from one zone to another
                if (it.spawn.isEmpty()) null else MusterAction(Figures(it.spawn), at) // TODO: Can also be from killed
            )
        }

    private fun takeCasualty(left: Figures, remainingHits: Int, regularReinforcements: List<Figure>): List<Casualty> { // TODO: Prefer killed regulars, test
        val numUnits = left.numRegulars() + left.numRegulars()
        if (numUnits == 0) return listOf(Casualty(left, 0, emptyList()))
        if (remainingHits <= 0) return emptyList()
        val take = if (numUnits > 5 && left.numRegulars() > 0)
            Casualty.kill(left.all.first { it.type == FigureType.REGULAR })
        else {
            val regular = regularReinforcements.firstOrNull { regular -> left.all.any { downgrade -> downgrade.type == FigureType.ELITE && regular.nation == downgrade.nation } }
            if (regular != null)
                Casualty.downgrade(left.all.first { it.type == FigureType.ELITE && it.nation == regular.nation }, regular)
            else if (left.numRegulars() > 0)
                Casualty.kill(left.all.first { it.type == FigureType.REGULAR })
            else
                Casualty.kill(left.all.first { it.type.isUnit })
        }
        return listOf(take) + takeCasualty(left - take.killed + Figures(take.spawn), remainingHits - take.hits, regularReinforcements - take.spawn.toSet())
    }

    class Casualty(val killed: Figures, val hits: Int, val spawn: List<Figure>) {
        operator fun plus(other: Casualty) = Casualty(killed + other.killed, hits + other.hits, spawn + other.spawn)

        companion object {
            fun kill(figure: Figure) = Casualty(Figures(listOf(figure)), if (figure.type == FigureType.REGULAR) 1 else 2, emptyList())
            fun downgrade(elite: Figure, regular: Figure) = Casualty(Figures(listOf(elite)), 1, listOf(regular))
            fun zero() = Casualty(Figures(emptyList()), 0, emptyList())
        }
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
