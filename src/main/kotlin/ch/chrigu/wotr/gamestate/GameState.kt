package ch.chrigu.wotr.gamestate

import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName

data class GameState(
    val location: Map<LocationName, Location>
) {
    fun removeFrom(locationName: LocationName, figures: Figures) = TODO()
    fun addTo(locationName: LocationName, figures: Figures) = TODO()
}
