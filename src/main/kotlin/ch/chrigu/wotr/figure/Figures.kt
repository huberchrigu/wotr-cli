package ch.chrigu.wotr.figure

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
        if (type != FiguresType.REINFORCEMENTS) {
            require(getArmy().all { it.nation.player == armyPlayer }) { "All army members must be of the same player: ${getArmy()}" }
            require((all - getArmy().toSet()).all { !it.type.isUnit && (it.nation.player != armyPlayer || !it.type.canBePartOfArmy) && it.isCharacterOrNazgul() })
            { "All figures not belonging to an army must be the other's player unique character: ${all - getArmy().toSet()}" }
            require(numUnits() <= 10) { "There must not be more than 10 units, but was ${numUnits()}" }
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

    operator fun plus(other: Figures) = copy(all = all + other.all)

    operator fun minus(other: Figures): Figures {
        require(all.containsAll(other.all))
        return copy(all = all - other.all.toSet())
    }

    /**
     * Excludes characters that do not belong to [armyPlayer].
     * @return Empty if there are no units that could form an army.
     */
    fun getArmy(): List<Figure> {
        check(type == FiguresType.LOCATION)
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

    fun combatRolls() = min(5, numUnits())

    fun maxReRolls() = min(leadership(), combatRolls())

    override fun toString() = (all.groupBy { it.nation }.map { (nation, figures) -> printArmy(figures) + " ($nation)" } +
            all.mapNotNull { it.type.shortcut })
        .joinToString(", ")

    private fun getUnits() = all.filter { it.type.isUnit }

    private fun leadership() = all.sumOf { it.type.leadership }

    private fun printArmy(figures: List<Figure>) = numRegulars(figures).toString() +
            numElites(figures) +
            numLeadersOrNazgul(figures)

    private fun numLeadersOrNazgul(figures: List<Figure>) = figures.count { it.type == FigureType.LEADER_OR_NAZGUL }

    private fun numElites(figures: List<Figure>) = figures.count { it.type == FigureType.ELITE }
    private fun numRegulars(figures: List<Figure>) = figures.count { it.type == FigureType.REGULAR }

    fun numUnits() = all.count { it.type.isUnit }

    private fun take(type: FigureType) = all.first { it.type == type }

    private fun take(num: Int, type: FigureType, nation: NationName?): List<Figure> {
        val allOfType = all.filter { it.type == type }
        val list = allOfType.filter { nation == null || nation == it.nation }.take(num)
        check(list.size == num)
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
