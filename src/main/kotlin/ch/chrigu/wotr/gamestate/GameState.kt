package ch.chrigu.wotr.gamestate

import ch.chrigu.wotr.card.Cards
import ch.chrigu.wotr.dice.Dice
import ch.chrigu.wotr.dice.DieUsage
import ch.chrigu.wotr.fellowship.Fellowship
import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.FiguresType
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.nation.Nation
import ch.chrigu.wotr.nation.NationName
import ch.chrigu.wotr.player.Player

data class GameState(
    val location: Map<LocationName, Location>,
    val nation: Map<NationName, Nation>,
    val reinforcements: Figures,
    val companions: List<Figure>,
    val fellowship: Fellowship = Fellowship(),
    val dice: Dice = Dice(),
    val cards: Cards = Cards()
) {
    init {
        require(reinforcements.type == FiguresType.REINFORCEMENTS)
    }

    val fellowshipLocation
        get() = fellowship.getFellowshipLocation(this)

    fun getLocationWith(figures: List<Figure>) = location.values.firstOrNull { it.allFigures().containsAll(figures) }
    fun findAll(filter: (Figure) -> Boolean) = location.values.flatMap { location ->
        location.allFigures().filter(filter).map { location to it }
    }

    fun vpFreePeople() = location.values.filter { it.captured && it.currentlyOccupiedBy() == Player.FREE_PEOPLE }.sumOf { it.victoryPoints }
    fun vpShadow() = location.values.filter { it.captured && it.currentlyOccupiedBy() == Player.SHADOW }.sumOf { it.victoryPoints }

    fun removeFrom(locationName: LocationName, figures: Figures) = location(locationName) { remove(figures) }
    fun addTo(locationName: LocationName, figures: Figures) = location(locationName) { add(figures) }
    fun addToReinforcements(figures: Figures) = copy(reinforcements = reinforcements + figures)
    fun removeFromReinforcements(figures: Figures) = copy(reinforcements = reinforcements - figures)
    fun useDie(use: DieUsage) = copy(dice = dice.useDie(use))

    fun numMinions() = location.values.sumOf { it.allFigures().count { figure -> figure.type.minion } }
    fun hasAragorn() = has(FigureType.ARAGORN)
    fun hasGandalfTheWhite() = has(FigureType.GANDALF_THE_WHITE)
    fun hasSaruman() = has(FigureType.SARUMAN)

    fun updateNation(name: NationName, modifier: Nation.() -> Nation) = copy(nation = nation + (name to nation[name]!!.run(modifier)))

    override fun toString() = "FP: ${dice.freePeople} [VP: ${getVictoryPoints(Player.FREE_PEOPLE)}, Pr: ${fellowship.progress}]\n" +
            "SW: ${dice.shadow} [VP: ${getVictoryPoints(Player.SHADOW)}, Co: ${fellowship.corruption}, $cards]"

    private fun has(figureType: FigureType) = location.values.any { l -> l.allFigures().any { f -> f.type == figureType } }

    private fun location(locationName: LocationName, modifier: Location.() -> Location) = copy(location = location + (locationName to location[locationName]!!.run(modifier)))

    private fun getVictoryPoints(player: Player) = location.values.filter { it.nation?.player != player && it.captured }
        .fold(0) { a, b -> a + b.victoryPoints }
}
