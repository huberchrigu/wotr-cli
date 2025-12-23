package ch.chrigu.wotr.location

import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.FiguresType
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.nation.NationName
import ch.chrigu.wotr.player.Player
import kotlinx.serialization.Serializable

/**
 * @param temporaryState If true, a besieged army may exist without besieging army. A besieging army must move to this location next.
 */
@Serializable
data class Location(
    val name: LocationName,
    val nonBesiegedFigures: Figures,
    val besiegedFigures: Figures = Figures(emptyList(), FiguresType.BESIEGED),
    val captured: Boolean = false,
    val temporaryState: Boolean = false
) {
    val allFigures by lazy { nonBesiegedFigures.all + besiegedFigures.all }

    init {
        if (name.nation == null) {
            require(name.type == LocationType.NONE || name.type == LocationType.FORTIFICATION) { "This location must have a nation: $this" }
        }
        if (name.type != LocationType.STRONGHOLD) {
            require(besiegedFigures.isEmpty()) { "Only strongholds may have besieged figures: $this" }
        } else if (!besiegedFigures.isEmpty()) {
            if (temporaryState)
                require(nonBesiegedFigures.isEmpty()) { "Temporary state is only allowed when waiting for army to besiege: $this" }
            else
                require(!nonBesiegedFigures.isEmpty()) { "Besieged figures require besieging figures: $this" }
        }
        if (currentlyOccupiedBy() == Player.FREE_PEOPLE && name.type == LocationType.STRONGHOLD && besiegedFigures.isEmpty()) {
            require(nonBesiegedFigures.all.none { it.isCharacterOrNazgul() && it.nation.player == Player.SHADOW }) { "Nazgul and minions may not enter strongholds controlled by free people" }
        }
        require(nonBesiegedFigures.type == FiguresType.LOCATION) { "Figures must have location type: LOCATION" }
        require(besiegedFigures.type == FiguresType.BESIEGED) { "Figures must have location type: BESIEGED" }
    }

    val adjacentLocations: List<LocationName>
        get() = name.adjacent()
    val nation: NationName?
        get() = name.nation
    val type: LocationType
        get() = name.type

    val victoryPoints: Int
        get() = if (type == LocationType.STRONGHOLD) 2 else if (type == LocationType.CITY) 1 else 0

    /**
     * If there are no besieging army units left, the siege ends.
     */
    fun remove(figures: Figures) = if (besiegedFigures.containsAll(figures)) {
        val newBesiegedFigures = besiegedFigures - figures
        if (newBesiegedFigures.isEmpty())
            copy(besiegedFigures = newBesiegedFigures, captured = newCapturedValueForArmy(nonBesiegedFigures.armyPlayer))
        else
            copy(besiegedFigures = newBesiegedFigures)
    } else {
        val remainingFigures = nonBesiegedFigures - figures
        if (remainingFigures.numUnits() == 0 && !besiegedFigures.isEmpty())
            copy(besiegedFigures = Figures(emptyList(), FiguresType.BESIEGED), nonBesiegedFigures = besiegedFigures.copy(type = FiguresType.LOCATION))
        else
            copy(nonBesiegedFigures = remainingFigures)
    }

    /**
     * If the location is a stronghold and currently occupied by the opponent, they go to [besiegedFigures].
     * @return A pair of the new location and remaining figures (that must be added to reinforcements).
     */
    fun add(figures: Figures): Pair<Location, List<Figure>> {
        val armyPlayer = figures.armyPlayer
        return if (type == LocationType.STRONGHOLD && armyPlayer != null && nonBesiegedFigures.armyPlayer?.opponent == armyPlayer) {
            val army = Figures(nonBesiegedFigures.army)
            val (joined, remaining) = besiegedFigures.addAsMuchAsPossible(army)
            copy(nonBesiegedFigures = nonBesiegedFigures - army + figures, besiegedFigures = joined) to remaining
        } else {
            val (joined, remaining) = nonBesiegedFigures.addAsMuchAsPossible(figures)
            if (besiegedFigures.isEmpty())
                copy(nonBesiegedFigures = joined, captured = newCapturedValueForArmy(armyPlayer)) to remaining
            else
                copy(nonBesiegedFigures = joined, temporaryState = false) to remaining
        }
    }

    fun currentlyOccupiedBy() = if (captured) nation?.player?.opponent else nation?.player

    fun contains(type: FigureType) = allFigures.any { it.type == type }

    fun getShortestPath(from: LocationName, to: LocationName): List<LocationPath> {
        return LocationFinder.getShortestPath(from, to)
    }

    fun adjacentArmies(player: Player, gameState: GameState) = adjacentLocations
        .map { gameState.location[it]!!.nonBesiegedFigures }
        .filter { it.armyPlayer == player }
        .map { it.army }

    fun retreatIntoStronghold() = copy(
        besiegedFigures = nonBesiegedFigures.copy(type = FiguresType.BESIEGED),
        nonBesiegedFigures = Figures.empty(),
        temporaryState = true
    )

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
