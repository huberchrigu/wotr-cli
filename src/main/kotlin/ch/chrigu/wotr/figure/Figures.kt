package ch.chrigu.wotr.figure

import ch.chrigu.wotr.combat.CombatType
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.nation.NationName
import ch.chrigu.wotr.player.Player
import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
data class Figures(val all: List<Figure>, val type: FiguresType = FiguresType.LOCATION) {
    init {
        require(all.distinct().size == all.size) { "Figures $all are not unique" }
        if (type != FiguresType.POOL) {
            require(getArmy().all { it.nation.player == armyPlayer }) { "All army members must be of the same player: ${getArmy()}" }
            require((all - getArmy().toSet()).all { !it.type.isUnit && (it.nation.player != armyPlayer || !it.type.canBePartOfArmy) && it.isCharacterOrNazgul() })
            { "All figures not belonging to an army must be the other's player unique character: ${all - getArmy().toSet()}" }
            require(numUnits() <= type.figureLimit) { "There must not be more than ${type.figureLimit} units, but was ${numUnits()}" }
            if (numUnits() == 0) {
                require(all.none { it.isFreePeopleLeader }) { "Free people leaders must not exist without army units: $all" }
            }
        }
    }

    val armyPlayer: Player?
        get() = getArmy().firstOrNull()?.nation?.player

    fun subSet(numRegular: Int, numElite: Int, numLeader: Int, nation: NationName?): Figures {
        return copy(all = take(numRegular, FigureType.REGULAR, nation) + take(numElite, FigureType.ELITE, nation) + take(numLeader, FigureType.LEADER_OR_NAZGUL, nation))
    }

    fun subSet(characters: List<FigureType>) = copy(all = characters.map { take(it) })

    fun isEmpty() = all.isEmpty()

    /**
     * Tries to downgrade an elite if [CombatType.SIEGE]. If there is no elite, it returns `null`.
     * For other combat types, `this` is returned.
     */
    @Deprecated("See downgradeOrNull()")
    fun downgradeOrNull(type: CombatType) = if (type == CombatType.SIEGE)
        downgradeOrNull()
    else
        this

    operator fun plus(other: Figures) = copy(all = all + other.all)
    operator fun plus(other: List<Figure>) = copy(all = all + other)

    /**
     * @return Pair of combined [Figures] and figures over the [FigureLimit].
     */
    fun addAsMuchAsPossible(other: Figures): Pair<Figures, List<Figure>> {
        val combined = all + other.all
        if (combined.count { it.type.isUnit } > type.figureLimit) {
            val remove = combined.firstOrNull { it.type == FigureType.ELITE } ?: combined.first { it.type == FigureType.REGULAR }
            val recursiveCall = copy(all = all - remove).addAsMuchAsPossible(other.copy(all = other.all - remove))
            return recursiveCall.first to (listOf(remove) + recursiveCall.second)
        }
        return copy(all = combined) to emptyList()
    }

    operator fun minus(other: Figures): Figures {
        require(all.containsAll(other.all))
        return copy(all = all - other.all.toSet())
    }

    /**
     * Excludes characters that do not belong to [armyPlayer].
     * @return Empty if there are no units that could form an army.
     */
    fun getArmy(): List<Figure> {
        check(type != FiguresType.POOL)
        val units = getUnits()
        return if (units.isEmpty())
            emptyList()
        else
            units + all.filter { !it.type.isUnit && it.nation.player == units.first().nation.player && it.type.canBePartOfArmy }
    }

    /**
     * @see getArmy
     */
    fun getArmyPerNation() = getArmy().groupBy { it.nation }

    fun intersect(other: Figures) = copy(all = all.intersect(other.all.toSet()).toList())

    fun union(other: Figures) = copy(all = all.union(other.all).toList())
    fun numElites() = numElites(all)

    fun numRegulars() = numRegulars(all)
    fun numLeadersOrNazgul() = numLeadersOrNazgul(all)
    fun characters() = all.filter { it.type.isUniqueCharacter }

    fun containsAll(figures: Figures) = all.containsAll(figures.all)

    fun combatRolls() = min(FigureLimit.STRONGHOLD, numUnits())

    fun maxReRolls() = min(leadership(), combatRolls())

    fun score(strongholdThreshold: Boolean): Int {
        val (numElites, numRegulars) = getDefenderUnits(strongholdThreshold)
        return combatRolls() + maxReRolls() + numElites * 2 + numRegulars
    }

    fun numUnits() = all.count { it.type.isUnit }

    fun contains(figure: Figure) = all.contains(figure)

    fun getArmyNations() = getArmy().asSequence()
        .map { it.nation }
        .distinct()

    override fun toString() = (all.groupBy { it.nation }.map { (nation, figures) -> printArmy(figures) + " ($nation)" } +
            all.mapNotNull { it.type.shortcut })
        .joinToString(", ")

    @Deprecated("Do not use this function for any other purposes than the combat simulator - reinforcements and killed units are ignored and the game state is not updated correctly")
    private fun downgradeOrNull(): Figures? {
        val elite = all.firstOrNull { it.type == FigureType.ELITE }
        if (elite == null) return null
        return (all - elite + Figure.create(1, FigureType.REGULAR, elite.nation)).toFigures()
    }

    /**
     * @return Pair of elites and regulars.
     */
    private fun getDefenderUnits(strongholdThreshold: Boolean): Pair<Int, Int> {
        val maxAllowed = if (strongholdThreshold) FigureLimit.STRONGHOLD else FigureLimit.STANDARD
        val numElites = min(maxAllowed, numElites())
        val numRegulars = min(maxAllowed - numElites, numRegulars())
        return Pair(numElites, numRegulars)
    }

    private fun getUnits() = all.filter { it.type.isUnit }

    private fun leadership() = all.sumOf { it.type.leadership }
    private fun printArmy(figures: List<Figure>) = numRegulars(figures).toString() +
            numElites(figures) +
            numLeadersOrNazgul(figures)

    private fun numLeadersOrNazgul(figures: List<Figure>) = figures.count { it.type == FigureType.LEADER_OR_NAZGUL }

    private fun numElites(figures: List<Figure>) = figures.count { it.type == FigureType.ELITE }

    private fun numRegulars(figures: List<Figure>) = figures.count { it.type == FigureType.REGULAR }

    private fun take(type: FigureType) = all.first { it.type == type }

    private fun take(num: Int, type: FigureType, nation: NationName?): List<Figure> {
        val allOfType = all.filter { it.type == type }
        val list = allOfType.filter { nation == null || nation == it.nation }.take(num)
        check(list.size == num) { "Should have found $num figures of type $type, but found only ${list.size}" }
        if (num > 0 && nation == null) {
            check(allOfType.all { it.nation == allOfType[0].nation }) { "No clear nation in $allOfType" }
        }
        return list
    }

    companion object {
        fun parse(who: Array<String>, locationName: LocationName, gameState: GameState) = parse(who, gameState.location[locationName]!!.nonBesiegedFigures)
        fun parse(who: Array<String>, figures: Figures, defaultNationName: NationName? = null): Figures {
            return if (who.isEmpty())
                figures
            else
                FigureParser(who.toList()).select(figures, defaultNationName)
        }

        fun empty() = Figures(emptyList())
        fun create(regular: Int, elite: Int, leaderOrNazgul: Int, nationName: NationName) = Figures(
            Figure.create(regular, FigureType.REGULAR, nationName) +
                    Figure.create(elite, FigureType.ELITE, nationName) +
                    Figure.create(leaderOrNazgul, FigureType.LEADER_OR_NAZGUL, nationName)
        )
    }
}

fun Collection<Figure>.toFigures() = Figures(this.toList())
fun Figure.toFigures() = Figures(listOf(this))
