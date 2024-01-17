package ch.chrigu.wotr.location

import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.FiguresType
import ch.chrigu.wotr.nation.NationName
import ch.chrigu.wotr.player.Player

data class Location(
    val name: LocationName,
    val adjacentLocations: List<LocationName>,
    val nation: NationName?,
    val type: LocationType,
    val nonBesiegedFigures: Figures,
    val besiegedFigures: Figures = Figures(emptyList()),
    val captured: Boolean = false
) {
    init {
        if (nation == null) {
            require(type == LocationType.NONE || type == LocationType.FORTIFICATION) { "This location must have a nation: $this" }
        }
        if (type != LocationType.STRONGHOLD) {
            require(besiegedFigures.isEmpty()) { "Only strongholds may have besieged figures: $this" }
        } else if (!besiegedFigures.isEmpty()) {
            require(!nonBesiegedFigures.isEmpty()) { "Besieged figures require besieging figures: $this" }
        }
        require(nonBesiegedFigures.type == FiguresType.LOCATION) { "Figures must have location type: $this" }
        require(besiegedFigures.type == FiguresType.LOCATION) { "Figures must have location type: $this" }
    }

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

    private fun currentlyOccupiedBy() = if (captured) nation?.player?.opponent else nation?.player

    private fun newCapturedValueForArmy(armyPlayer: Player?) = if (currentlyOccupiedBy() == armyPlayer || armyPlayer == null)
        captured
    else
        !captured

    override fun toString() = name.fullName + ": " + nonBesiegedFigures.toString() + printStronghold()
    private fun printStronghold() = if (type == LocationType.STRONGHOLD)
        "/$besiegedFigures"
    else
        ""
}
