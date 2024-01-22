package ch.chrigu.wotr.gamestate

import ch.chrigu.wotr.dice.Dice
import ch.chrigu.wotr.dice.DieUsage
import ch.chrigu.wotr.fellowship.Fellowship
import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.FiguresType
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.player.Player

data class GameState(
    val location: Map<LocationName, Location>,
    val reinforcements: Figures,
    val companions: List<Figure>,
    val fellowship: Fellowship = Fellowship(),
    val dice: Dice = Dice()
) {
    init {
        require(reinforcements.type == FiguresType.REINFORCEMENTS)
    }

    val fellowshipLocation
        get() = fellowship.getFellowshipLocation(this)

    fun removeFrom(locationName: LocationName, figures: Figures) = location(locationName) { remove(figures) }
    fun addTo(locationName: LocationName, figures: Figures) = location(locationName) { add(figures) }
    fun addToReinforcements(figures: Figures) = copy(reinforcements = reinforcements + figures)
    fun removeFromReinforcements(figures: Figures) = copy(reinforcements = reinforcements - figures)
    fun useDie(use: DieUsage) = copy(dice = dice.useDie(use))

    fun numMinions() = location.values.sumOf { it.allFigures().count { figure -> figure.type.minion } }
    fun hasAragorn() = has(FigureType.ARAGORN)
    fun hasGandalfTheWhite() = has(FigureType.GANDALF_THE_WHITE)

    private fun has(figureType: FigureType) = location.values.any { l -> l.allFigures().any { f -> f.type == figureType } }

    override fun toString() = "VP: ${getVictoryPoints(Player.FREE_PEOPLE)} vs. ${getVictoryPoints(Player.SHADOW)}, Pr ${fellowship.progress}, Co ${fellowship.corruption}"

    private fun location(locationName: LocationName, modifier: Location.() -> Location) = copy(location = location + (locationName to location[locationName]!!.run(modifier)))

    private fun getVictoryPoints(player: Player) = location.values.filter { it.nation?.player != player && it.captured }
        .fold(0) { a, b -> a + b.victoryPoints }

}
