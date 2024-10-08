package ch.chrigu.wotr.combat

import ch.chrigu.wotr.action.DowngradeAction
import ch.chrigu.wotr.action.KillAction
import ch.chrigu.wotr.figure.*
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
    private val defenderLocation: LocationName,
    private val round: Int,
    private val dieFactory: DieFactory = RandomDieFactory
) {
    fun repeat(num: Int): List<Casualties> {
        val requires = if (combatType == CombatType.SIEGE)
            6
        else if (round == 0 && locationType == LocationType.CITY || locationType == LocationType.FORTIFICATION)
            6
        else
            5
        val attackerDice = CombatDice(attacker.combatRolls(), attacker.maxReRolls(), requires, dieFactory)
        val defenderDice = CombatDice(defender.combatRolls(), defender.maxReRolls(), 5, dieFactory)
        val hits = (0 until num).map { attackerDice.roll() to defenderDice.roll() }
            .fold(0.0 to 0.0) { (a1, d1), (a2, d2) -> a1 + a2 to d1 + d2 }
            .let { (a, d) -> round(a / num) to round(d / num) }
        return listOf(Casualties(defenderLocation, defender, hits.first.toInt()), Casualties(attackerLocation, attacker, hits.second.toInt()))
    }
}

data class Casualties(private val at: LocationName, private val from: Figures, private val hits: Int) {
    fun apply(state: GameState) = initActions(state.killed + state.reinforcements).fold(state) { a, b -> b.apply(a) }

    /**
     * @param figurePool Killed figures should be first, because they take precedence for downgrades
     */
    private fun initActions(figurePool: Figures) = takeCasualty(from.copy(type = FiguresType.POOL), hits, figurePool.all.filter { it.type == FigureType.REGULAR })
        .fold(Casualty.zero()) { a, b -> a + b }
        .toActions(at)

    private fun takeCasualty(left: Figures, remainingHits: Int, regularPool: List<Figure>): List<Casualty> {
        val numUnits = left.numUnits()
        if (numUnits == 0) return listOf(Casualty(left, 0, emptyList()))
        if (remainingHits <= 0) return emptyList()
        val take = if (numUnits > 5 && left.numRegulars() > 0)
            Casualty.kill(left.all.first { it.type == FigureType.REGULAR })
        else if (left.numElites() in 1..<remainingHits)
            Casualty.kill(left.all.first { it.type == FigureType.ELITE })
        else {
            val regular = regularPool.firstOrNull { regular -> left.all.any { downgrade -> downgrade.type == FigureType.ELITE && regular.nation == downgrade.nation } }
            if (regular != null)
                Casualty.downgrade(left.all.first { it.type == FigureType.ELITE && it.nation == regular.nation }, regular)
            else if (left.numRegulars() > 0)
                Casualty.kill(left.all.first { it.type == FigureType.REGULAR })
            else
                Casualty.kill(left.all.first { it.type.isUnit })
        }
        return listOf(take) + takeCasualty(take.applyTo(left), remainingHits - take.hits, regularPool - take.downgrades.map { it.regular }.toSet())
    }

    class Casualty(private val killed: Figures, val hits: Int, val downgrades: List<Downgrade>) {
        operator fun plus(other: Casualty) = Casualty(killed + other.killed, hits + other.hits, downgrades + other.downgrades)

        fun toActions(at: LocationName) = listOfNotNull(
            if (killed.isEmpty()) null else KillAction(at, killed),
        ) + downgrades.map { (elite, regular) -> DowngradeAction(at, elite, regular) }

        fun applyTo(from: Figures) = from - killed - downgrades.map { it.elite }.toFigures() + downgrades.map { it.regular }.toFigures()

        companion object {
            fun kill(figure: Figure) = Casualty(Figures(listOf(figure)), if (figure.type == FigureType.REGULAR) 1 else 2, emptyList())
            fun downgrade(elite: Figure, regular: Figure) = Casualty(Figures.empty(), 1, listOf(Downgrade(elite, regular)))
            fun zero() = Casualty(Figures.empty(), 0, emptyList())
        }

        data class Downgrade(val elite: Figure, val regular: Figure)
    }
}

data class CombatDice(private val combatRolls: Int, private val maxReRolls: Int, val requires: Int, private val dieFactory: DieFactory = RandomDieFactory) {
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

    private fun rollDice(num: Int) = (0 until num).map { dieFactory.next() }
}

object RandomDieFactory : DieFactory {
    override fun next() = Random.nextInt(6) + 1
}

interface DieFactory {
    fun next(): Int
}
