package ch.chrigu.wotr.location

import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.FiguresType
import ch.chrigu.wotr.nation.NationName

data class Location(
    val name: LocationName,
    val adjacentLocations: List<LocationName>,
    val nation: NationName?,
    val type: LocationType,
    val nonBesiegedFigures: Figures,
    val besiegedFigures: Figures = Figures(emptyList()), // TODO: Change if move/remove
    val captured: Boolean = false // TODO: Change if move
) {
    val victoryPoints: Int
        get() = if (type == LocationType.STRONGHOLD) 2 else if (type == LocationType.CITY) 1 else 0

    init {
        if (nation == null) {
            require(type == LocationType.NONE || type == LocationType.FORTIFICATION)
        }
        if (type != LocationType.STRONGHOLD) {
            require(besiegedFigures.isEmpty())
        } else if (!besiegedFigures.isEmpty()) {
            require(!nonBesiegedFigures.isEmpty())
        }
        require(nonBesiegedFigures.type == FiguresType.LOCATION)
        require(besiegedFigures.type == FiguresType.LOCATION)
    }

    fun remove(figures: Figures) = copy(nonBesiegedFigures = nonBesiegedFigures - figures)

    fun add(figures: Figures) = copy(nonBesiegedFigures = nonBesiegedFigures + figures)

    override fun toString() = name.fullName + ": " + nonBesiegedFigures.toString()
}
