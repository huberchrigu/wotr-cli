package ch.chrigu.wotr.location

import ch.chrigu.wotr.gamestate.GameState

class LocationFinder(private val state: GameState) {
    fun getShortestPath(from: LocationName, to: LocationName): List<LocationPath> {
        val all = getAllPaths(from, to)
        val shortest = all.minOf { it.getLength() }
        return all.filter { it.getLength() == shortest }
    }

    private fun getAllPaths(from: LocationName, to: LocationName) = if (from == to)
        listOf(LocationPath(emptyList()))
    else
        adjacent(from).flatMap { visit(it, to, listOf(from)) }

    private fun adjacent(from: LocationName) = state.location[from]!!.adjacentLocations

    private fun visit(next: LocationName, goal: LocationName, visited: List<LocationName>): List<LocationPath> {
        val path = LocationPath(listOf(next))
        if (next == goal) {
            return listOf(path)
        }
        return (adjacent(next) - visited.toSet()).flatMap { visit(it, goal, visited + listOf(next)) }
            .map { path + it }
    }
}

data class LocationPath(private val locations: List<LocationName>) {
    fun getLength() = locations.size

    operator fun plus(other: LocationPath) = LocationPath(locations + other.locations)
}
