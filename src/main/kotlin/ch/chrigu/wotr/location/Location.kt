package ch.chrigu.wotr.location

import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.FiguresType
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.nation.NationName
import ch.chrigu.wotr.player.Player
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val name: LocationName,
    val nonBesiegedFigures: Figures,
    val besiegedFigures: Figures = Figures(emptyList()),
    val captured: Boolean = false
) {
    init {
        if (name.nation == null) {
            require(name.type == LocationType.NONE || name.type == LocationType.FORTIFICATION) { "This location must have a nation: $this" }
        }
        if (name.type != LocationType.STRONGHOLD) {
            require(besiegedFigures.isEmpty()) { "Only strongholds may have besieged figures: $this" }
        } else if (!besiegedFigures.isEmpty()) {
            require(!nonBesiegedFigures.isEmpty()) { "Besieged figures require besieging figures: $this" }
        }
        if (currentlyOccupiedBy() == Player.FREE_PEOPLE && name.type == LocationType.STRONGHOLD && besiegedFigures.isEmpty()) {
            require(nonBesiegedFigures.all.none { it.isCharacterOrNazgul() && it.nation.player == Player.SHADOW }) { "Nazgul and minions may not enter strongholds controlled by free people" }
        }
        require(nonBesiegedFigures.type == FiguresType.LOCATION) { "Figures must have location type: $this" }
        require(besiegedFigures.type == FiguresType.LOCATION) { "Figures must have location type: $this" }
    }

    val adjacentLocations: List<LocationName>
        get() = name.adjacent()
    val nation: NationName?
        get() = name.nation
    val type: LocationType
        get() = name.type

    val victoryPoints: Int
        get() = if (type == LocationType.STRONGHOLD) 2 else if (type == LocationType.CITY) 1 else 0

    fun remove(figures: Figures) = if (besiegedFigures.containsAll(figures)) {
        val newBesiegedFigures = besiegedFigures - figures
        if (newBesiegedFigures.isEmpty())
            copy(besiegedFigures = newBesiegedFigures, captured = newCapturedValueForArmy(nonBesiegedFigures.armyPlayer))
        else
            copy(besiegedFigures = newBesiegedFigures)
    } else {
        copy(nonBesiegedFigures = nonBesiegedFigures - figures)
    }

    fun add(figures: Figures): Location {
        val armyPlayer = figures.armyPlayer
        return if (type == LocationType.STRONGHOLD && armyPlayer != null && nonBesiegedFigures.armyPlayer?.opponent == armyPlayer) {
            val army = Figures(nonBesiegedFigures.getArmy())
            copy(nonBesiegedFigures = nonBesiegedFigures - army + figures, besiegedFigures = besiegedFigures + army)
        } else {
            copy(nonBesiegedFigures = nonBesiegedFigures + figures, captured = newCapturedValueForArmy(armyPlayer))
        }
    }

    fun currentlyOccupiedBy() = if (captured) nation?.player?.opponent else nation?.player

    fun contains(type: FigureType) = allFigures().any { it.type == type }

    fun allFigures() = nonBesiegedFigures.all + besiegedFigures.all

    fun getShortestPath(from: LocationName, to: LocationName): List<LocationPath> {
        return LocationFinder.getShortestPath(from, to)
    }

    fun adjacentArmies(player: Player, gameState: GameState) = adjacentLocations
        .map { gameState.location[it]!!.nonBesiegedFigures }
        .filter { it.armyPlayer == player }
        .map { it.getArmy() }

    fun nearestLocationWith(state: GameState, condition: (Location) -> Boolean): Sequence<Pair<Location, Int>> {
        var minValue: Int? = null
        return LocationFinder.getNearestLocations(name)
            .mapNotNull { (location, distance) -> state.location[location]?.let { it to distance } }
            .filter { (location, _) -> condition(location) }
            .onEach { if (minValue == null) minValue = it.second }
            .takeWhile { it.second == minValue }
    }

    fun distanceTo(state: GameState, condition: (Location) -> Boolean): Map<Location, Int> {
        val candidates = state.location.values.filter(condition)
        return candidates.associateWith { LocationFinder.getDistance(name, it.name) }
    }

    override fun toString() = name.fullName + ": " + nonBesiegedFigures.toString() + printStronghold()

    private fun newCapturedValueForArmy(armyPlayer: Player?) = if (currentlyOccupiedBy() == armyPlayer || armyPlayer == null)
        captured
    else
        !captured

    private fun printStronghold() = if (type == LocationType.STRONGHOLD)
        "/$besiegedFigures"
    else
        ""
}
