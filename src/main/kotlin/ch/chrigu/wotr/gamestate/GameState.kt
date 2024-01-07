package ch.chrigu.wotr.gamestate

import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName

data class GameState(
    val location: Map<LocationName, Location>
) {
    fun removeFrom(locationName: LocationName, figures: Figures) = location(locationName) { remove(figures) }
    fun addTo(locationName: LocationName, figures: Figures) = location(locationName) { add(figures) }

    private fun location(locationName: LocationName, modifier: Location.() -> Location) = copy(location = location + (locationName to location[locationName]!!.run(modifier)))
}
