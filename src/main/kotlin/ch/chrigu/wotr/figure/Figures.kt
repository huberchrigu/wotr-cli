package ch.chrigu.wotr.figure

import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.player.Player

data class Figures(private val all: List<Figure>) {
    init {
        require(all.distinct().size == all.size)
        require(all.all { it.nation.player == player }) // TODO: Characters may be on same field as armies
        require(all.count { it.type.isUnit } <= 10)
    }

    val player: Player?
        get() = all.firstOrNull()?.nation?.player

    fun subSet(numRegular: Int, numElite: Int, numLeader: Int, characters: List<FigureType>): Figures {
        return Figures(take(numRegular, FigureType.REGULAR) + take(numElite, FigureType.ELITE) + take(numLeader, FigureType.LEADER_OR_NAZGUL) + characters.map { take(it) })
    }

    fun isEmpty() = all.isEmpty()

    operator fun plus(other: Figures) = Figures(all + other.all)
    operator fun minus(other: Figures): Figures {
        require(all.containsAll(other.all))
        return Figures(all - other.all.toSet())
    }

    override fun toString() = all.count { it.type == FigureType.REGULAR }.toString() +
            all.count { it.type == FigureType.ELITE } +
            all.count { it.type == FigureType.LEADER_OR_NAZGUL } +
            all.mapNotNull { it.type.shortcut }.joinToString()

    private fun take(type: FigureType) = all.first { it.type == type }
    private fun take(num: Int, type: FigureType): List<Figure> {
        val list = all.filter { it.type == type }.take(num)
        check(list.size == num)
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
