package ch.chrigu.wotr.gamestate

import ch.chrigu.wotr.card.Cards
import ch.chrigu.wotr.dice.Dice
import ch.chrigu.wotr.dice.DieUsage
import ch.chrigu.wotr.fellowship.Fellowship
import ch.chrigu.wotr.figure.*
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.nation.Nation
import ch.chrigu.wotr.nation.NationName
import ch.chrigu.wotr.player.Player
import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val location: Map<LocationName, Location>,
    val nation: Map<NationName, Nation>,
    val reinforcements: Figures,
    val companions: List<Figure>,
    val fellowship: Fellowship = Fellowship(),
    val dice: Dice = Dice(),
    val cards: Cards = Cards(),
    val killed: Figures = Figures(emptyList(), FiguresType.POOL)
) {
    init {
        require(reinforcements.type == FiguresType.POOL)
    }

    val fellowshipLocation
        get() = fellowship.getFellowshipLocation(this)

    fun getLocationWith(figures: List<Figure>) = location.values.firstOrNull { it.allFigures().containsAll(figures) }
    fun findAll(filter: (Figure) -> Boolean) = location.values.flatMap { location ->
        location.allFigures().filter(filter).map { location to it }
    }

    fun vpFreePeople() = location.values.filter { it.captured && it.currentlyOccupiedBy() == Player.FREE_PEOPLE }.sumOf { it.victoryPoints }
    fun vpShadow() = location.values.filter { it.captured && it.currentlyOccupiedBy() == Player.SHADOW }.sumOf { it.victoryPoints }

    fun move(figures: Figures, from: Zone, to: Zone) = from.remove(this, figures).let { to.add(it, figures) }

    @Deprecated("A figure should never disappear, only allow move from one zone to another")
    fun removeFrom(locationName: LocationName, figures: Figures) = location(locationName) { remove(figures) }

    @Deprecated("A figure should never disappear, only allow move from one zone to another")
    fun addTo(locationName: LocationName, figures: Figures) = location(locationName) { add(figures) }

    fun useDie(use: DieUsage) = copy(dice = dice.useDie(use))

    fun numMinions() = location.values.sumOf { it.allFigures().count { figure -> figure.type.minion } }
    fun hasAragorn() = has(FigureType.ARAGORN)
    fun hasGandalfTheWhite() = has(FigureType.GANDALF_THE_WHITE)
    fun hasSaruman() = has(FigureType.SARUMAN)

    fun updateNation(name: NationName, modifier: Nation.() -> Nation) = copy(nation = nation + (name to nation[name]!!.run(modifier)))

    override fun toString() = "FP: ${dice.freePeople} [VP: ${getVictoryPoints(Player.FREE_PEOPLE)}, Pr: ${fellowship.progress}]\n" +
            "SH: ${dice.shadow} [VP: ${getVictoryPoints(Player.SHADOW)}, Co: ${fellowship.corruption}, $cards]"

    private fun has(figureType: FigureType) = location.values.any { l -> l.allFigures().any { f -> f.type == figureType } }

    private fun location(locationName: LocationName, modifier: Location.() -> Location) = copy(location = location + (locationName to location[locationName]!!.run(modifier)))

    private fun getVictoryPoints(player: Player) = location.values.filter { it.nation?.player != player && it.captured }
        .fold(0) { a, b -> a + b.victoryPoints }

    companion object {
        fun create(locations: List<Location>, reinforcements: List<Figure> = emptyList(), killed: List<Figure> = emptyList()) =
            GameState(locations.associateBy { it.name }, emptyMap(), Figures(reinforcements, FiguresType.POOL), emptyList(), killed = Figures(killed, FiguresType.POOL))
    }
}
