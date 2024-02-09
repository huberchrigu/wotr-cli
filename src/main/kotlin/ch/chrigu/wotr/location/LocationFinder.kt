package ch.chrigu.wotr.location

import java.util.*

object LocationFinder {
    private val cache = Collections.synchronizedMap(HashMap<LocationName, List<GraphNode>>())

    fun getDistance(from: LocationName, to: LocationName) = getShortestPath(from, to).first().getLength()
    fun getShortestPath(from: LocationName, to: LocationName): List<LocationPath> {
        val graph = cache[from] ?: initGraph(from)
        return visit(graph, from, graph.first { it.name == to })
    }

    private fun visit(graph: List<GraphNode>, from: LocationName, to: GraphNode): List<LocationPath> = if (from == to.name)
        listOf(LocationPath(emptyList()))
    else
        to.previous.flatMap { visit(graph, from, it) }.map { it + LocationPath(listOf(to.name)) }

    private fun initGraph(from: LocationName): List<GraphNode> {
        val graph = LocationName.entries.map { if (it == from) GraphNode(it, distance = 0) else GraphNode(it) }
        val visit = LocationName.entries.toMutableSet()
        while (visit.isNotEmpty()) {
            val minDistance = graph.filter { visit.contains(it.name) && it.distance != null }.minBy { it.distance!! }
            visit.remove(minDistance.name)
            minDistance.name.adjacent()
                .map { neighbor -> graph.first { it.name == neighbor } }
                .forEach { neighbor ->
                    val newDistance = minDistance.distance!! + 1
                    if (neighbor.distance == null || newDistance < neighbor.distance!!) {
                        neighbor.distance = newDistance
                        neighbor.previous = mutableListOf(minDistance)
                    } else if (neighbor.distance == newDistance) {
                        neighbor.previous.add(minDistance)
                    }
                }
        }
        cache[from] = graph
        return graph
    }

    data class GraphNode(val name: LocationName, var previous: MutableList<GraphNode> = mutableListOf(), var distance: Int? = null)
}

data class LocationPath(val locations: List<LocationName>) {
    fun getLength() = locations.size

    operator fun plus(other: LocationPath) = LocationPath(locations + other.locations)
}
