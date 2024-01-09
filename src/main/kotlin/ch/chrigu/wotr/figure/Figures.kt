package ch.chrigu.wotr.figure

import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.nation.NationName
import ch.chrigu.wotr.player.Player

data class Figures(private val all: List<Figure>) {
    init {
        require(all.distinct().size == all.size)
        require(all.all { it.nation.player == player }) // TODO: Characters may be on same field as armies
        require(numUnits() <= 10)
        if (numUnits() == 0) {
            require(all.none { it.isFreePeopleLeader })
        }
    }

    val player: Player?
        get() = all.firstOrNull()?.nation?.player

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
     * Excludes Nazgul, because they can move alone like characters.
     */
    fun getArmyWithoutCharacters() = all.filter { it.type.isUnit || it.isFreePeopleLeader }

    /**
     * @see getArmyWithoutCharacters
     */
    fun getArmyPerNation() = getArmyWithoutCharacters().groupBy { it.nation }

    fun union(other: Figures) = Figures(all.union(other.all).toList())

    override fun toString() = (all.groupBy { it.nation }.map { (nation, figures) -> printArmy(figures) + " (${nation.fullName})" } +
            all.mapNotNull { it.type.shortcut })
        .joinToString(", ")

    private fun printArmy(figures: List<Figure>) = figures.count { it.type == FigureType.REGULAR }.toString() +
            figures.count { it.type == FigureType.ELITE } +
            figures.count { it.type == FigureType.LEADER_OR_NAZGUL }

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
        fun parse(who: String?, location: LocationName, gameState: GameState): Figures {
            val figures = gameState.location[location]!!.nonBesiegedFigures
            return if (who == null)
                figures
            else
                FigureParser(who).select(figures)
        }
    }
}
