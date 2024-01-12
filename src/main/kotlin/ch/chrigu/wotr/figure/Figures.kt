package ch.chrigu.wotr.figure

import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.nation.NationName
import ch.chrigu.wotr.player.Player

data class Figures(private val all: List<Figure>) {
    init {
        require(all.distinct().size == all.size)
        require(getArmy().all { it.nation.player == armyPlayer })
        require((all - getArmy().toSet()).all { !it.type.isUnit && it.nation.player != armyPlayer && it.type.isUniqueCharacter })
        require(numUnits() <= 10)
        if (numUnits() == 0) {
            require(all.none { it.isFreePeopleLeader })
        }
    }

    val armyPlayer: Player?
        get() = getArmy().firstOrNull()?.nation?.player

    fun subSet(numRegular: Int, numElite: Int, numLeader: Int, nation: NationName?): Figures {
        return Figures(take(numRegular, FigureType.REGULAR, nation) + take(numElite, FigureType.ELITE, nation) + take(numLeader, FigureType.LEADER_OR_NAZGUL, nation))
    }

    fun subSet(characters: List<FigureType>) = Figures(characters.map { take(it) })

    fun isEmpty() = all.isEmpty()

    operator fun plus(other: Figures) = Figures(all + other.all)

    operator fun minus(other: Figures): Figures {
        require(all.containsAll(other.all))
        return Figures(all - other.all.toSet())
    }

    /**
     * Excludes characters that do not belong to [armyPlayer].
     * @return Empty if there are no units that could form an army.
     */
    fun getArmy(): List<Figure> {
        val units = getUnits()
        return if (units.isEmpty())
            emptyList()
        else
            units + all.filter { !it.type.isUnit && it.nation.player == units.first().nation.player }
    }

    /**
     * @see getArmy
     */
    fun getArmyPerNation() = getArmy().groupBy { it.nation }

    fun union(other: Figures) = Figures(all.union(other.all).toList())

    fun numElites() = numElites(all)
    fun numRegulars() = numRegulars(all)
    fun numLeadersOrNazgul() = numLeadersOrNazgul(all)

    fun characters() = all.filter { it.type.isUniqueCharacter }

    override fun toString() = (all.groupBy { it.nation }.map { (nation, figures) -> printArmy(figures) + " (${nation.fullName})" } +
            all.mapNotNull { it.type.shortcut })
        .joinToString(", ")

    private fun getUnits() = all.filter { it.type.isUnit }

    private fun printArmy(figures: List<Figure>) = numRegulars(figures).toString() +
            numElites(figures) +
            numLeadersOrNazgul(figures)

    private fun numLeadersOrNazgul(figures: List<Figure>) = figures.count { it.type == FigureType.LEADER_OR_NAZGUL }

    private fun numElites(figures: List<Figure>) = figures.count { it.type == FigureType.ELITE }

    private fun numRegulars(figures: List<Figure>) = figures.count { it.type == FigureType.REGULAR }

    private fun numUnits() = all.count { it.type.isUnit }

    private fun take(type: FigureType) = all.first { it.type == type }
    private fun take(num: Int, type: FigureType, nation: NationName?): List<Figure> {
        val allOfType = all.filter { it.type == type }
        val list = allOfType.filter { nation == null || nation == it.nation }.take(num)
        check(list.size == num)
        if (num > 0 && nation == null) {
            check(allOfType.all { it.nation == allOfType[0].nation })
        }
        return list
    }

    companion object {
        fun parse(who: Array<String>, locationName: LocationName, gameState: GameState) = parse(who, gameState.location[locationName]!!.nonBesiegedFigures)
        fun parse(who: Array<String>, figures: Figures): Figures {
            return if (who.isEmpty())
                figures
            else
                FigureParser(who.toList()).select(figures)
        }
    }
}
