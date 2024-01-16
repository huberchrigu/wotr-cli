package ch.chrigu.wotr.gamestate

import ch.chrigu.wotr.fellowship.Fellowship
import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.FiguresType
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.player.Player

data class GameState(
    val location: Map<LocationName, Location>,
    val reinforcements: Figures,
    val companions: List<Figure>,
    val fellowship: Fellowship = Fellowship()
) {
    init {
        require(reinforcements.type == FiguresType.REINFORCEMENTS)
    }

    fun removeFrom(locationName: LocationName, figures: Figures) = location(locationName) { remove(figures) }
    fun addTo(locationName: LocationName, figures: Figures) = location(locationName) { add(figures) }
    fun addToReinforcements(figures: Figures) = copy(reinforcements = reinforcements + figures)
    fun removeFromReinforcements(figures: Figures) = copy(reinforcements = reinforcements - figures)

    override fun toString() = "VP: ${getVictoryPoints(Player.FREE_PEOPLE)} vs. ${getVictoryPoints(Player.SHADOW)}, Pr ${fellowship.progress}, Co ${fellowship.corruption}"

    private fun location(locationName: LocationName, modifier: Location.() -> Location) = copy(location = location + (locationName to location[locationName]!!.run(modifier)))

    private fun getVictoryPoints(player: Player) = location.values.filter { it.nation?.player != player && it.captured }
        .fold(0) { a, b -> a + b.victoryPoints }
}
