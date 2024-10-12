package ch.chrigu.wotr.gamestate

import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName

sealed interface Zone {
    fun remove(gameState: GameState, figures: Figures): GameState
    fun add(gameState: GameState, figures: Figures): GameState
}

data object Reinforcements : Zone {
    override fun remove(gameState: GameState, figures: Figures) = gameState.copy(reinforcements = gameState.reinforcements - figures)

    override fun add(gameState: GameState, figures: Figures) = gameState.copy(reinforcements = gameState.reinforcements + figures)
}

data object Killed : Zone {
    override fun remove(gameState: GameState, figures: Figures) = gameState.copy(killed = gameState.killed - figures)

    override fun add(gameState: GameState, figures: Figures) = gameState.copy(killed = gameState.killed + figures)
}

class At(private val name: LocationName) : Zone {
    override fun remove(gameState: GameState, figures: Figures) = gameState.location { remove(figures) }

    override fun add(gameState: GameState, figures: Figures) = gameState.addFiguresTo(figures, name)

    private fun GameState.location(modifier: Location.() -> Location) = copy(location = location + (name to location[name]!!.run(modifier)))
}

fun GameState.getPoolZoneFor(figure: Figure) = if (reinforcements.contains(figure))
    Reinforcements
else
    Killed
