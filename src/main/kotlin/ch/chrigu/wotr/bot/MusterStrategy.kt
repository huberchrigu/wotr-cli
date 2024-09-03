package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.*
import ch.chrigu.wotr.card.EventType
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.location.LocationType
import ch.chrigu.wotr.nation.NationName
import ch.chrigu.wotr.player.Player
import org.jline.terminal.Terminal
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * Includes playing event card.
 */
@Order(2)
@Component
class MusterStrategy(private val terminal: Terminal) : BotStrategy {
    override fun getActions(state: GameState): List<GameAction> {
        val nations = state.nation.values.filter { it.name.player == Player.SHADOW }
        val onWar = nations.filter { it.isAtWar() }.map { it.name }
        val settlements = state.location.values.filter { it.nation in onWar && !it.captured }
        val combinations = Combinations.ofSize(settlements, 2)
        return (nations.map { it.name } - onWar.toSet()).map { PoliticsMarkerAction(it) } +
                combinations.flatMap { (a, b) ->
                    val regularA = regular(a, state)
                    val regularB = regular(b, state)
                    val nazgulA = nazgul(a, state)
                    val nazgulB = nazgul(b, state)
                    listOfNotNull(
                        regularA?.plus(regularB),
                        nazgulA?.plus(nazgulB),
                        regularA?.plus(nazgulB),
                        nazgulA?.plus(regularB)
                    )
                } +
                settlements.mapNotNull { elite(it, state) } +
                sarumansVoice(state) +
                listOf(PlayEventAction(EventType.STRATEGY, terminal))
    }

    private fun elite(location: Location, state: GameState) = unit(state, location, FigureType.ELITE)
    private fun regular(location: Location, state: GameState) = unit(state, location, FigureType.REGULAR)
    private fun nazgul(location: Location, state: GameState) = if (location.type == LocationType.STRONGHOLD && location.nation == NationName.SAURON)
        unit(state, location, FigureType.LEADER_OR_NAZGUL)
    else
        null

    private fun sarumansVoice(state: GameState): List<GameAction> {
        if (!state.hasSaruman()) return emptyList()
        val orthancRegulars = state.location[LocationName.ORTHANC]!!.nonBesiegedFigures.all.filter { it.nation == NationName.ISENGARD && it.type == FigureType.REGULAR }.take(2)
        val elites = state.reinforcements.all.filter { it.nation == NationName.ISENGARD && it.type == FigureType.ELITE }.take(2)
        val orthanc = if (orthancRegulars.size == 2 && elites.size == 2)
            ReplaceFiguresAction(Figures(orthancRegulars), Figures(elites), LocationName.ORTHANC)
        else
            null
        val threeRegulars = state.reinforcements.all.filter { it.nation == NationName.ISENGARD && it.type == FigureType.REGULAR }.take(3)
            .let { if (it.size == 3) MusterFiguresAction(it[0] to LocationName.ORTHANC, it[1] to LocationName.SOUTH_DUNLAND, it[2] to LocationName.NORTH_DUNLAND) else null }
        return listOfNotNull(orthanc, threeRegulars)
    }

    private fun unit(state: GameState, location: Location, type: FigureType): MusterFiguresAction? {
        val unit = state.reinforcements.all.firstOrNull { it.type == type && it.nation == location.nation }
        return unit?.let { MusterFiguresAction(Figures(listOf(unit)), location.name) }
    }
}
