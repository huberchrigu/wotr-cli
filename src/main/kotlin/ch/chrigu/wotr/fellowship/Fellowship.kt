package ch.chrigu.wotr.fellowship

import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.player.Player
import kotlinx.serialization.Serializable

@Serializable
data class Fellowship(val progress: Int = 0, val corruption: Int = 0, val mordor: Int? = null, val discovered: Boolean = false) {
    init {
        require(progress in 0..MAX_CORRUPTION) { "Progress should be between 0 and 12, but was $progress" }
        require(corruption in 0..MAX_CORRUPTION) { "Corruption should be between 0 and 12, but was $corruption" }
        if (mordor != null) {
            require(progress == 0)
            require(mordor in 0..MORDOR_STEPS)
        }
    }

    fun numStepsLeft(state: GameState) = if (mordor == null)
        getFellowshipLocation(state).getShortestPath(LocationName.MORANNON, LocationName.MINAS_MORGUL)
            .first().getLength() + MORDOR_STEPS
    else
        MORDOR_STEPS - mordor

    fun remainingCorruption() = MAX_CORRUPTION - corruption
    fun numRerolls(state: GameState): Int {
        val fellowshipLocation = getFellowshipLocation(state)
        return listOf(
            fellowshipLocation.allFigures().any { it.isNazgul },
            fellowshipLocation.nonBesiegedFigures.armyPlayer == Player.SHADOW || fellowshipLocation.besiegedFigures.armyPlayer == Player.SHADOW,
            fellowshipLocation.currentlyOccupiedBy() == Player.SHADOW
        )
            .count { it }
    }

    fun getFellowshipLocation(state: GameState) = state.location.values.first { it.contains(FigureType.FELLOWSHIP) }

    companion object {
        const val MORDOR_STEPS = 5
        const val MAX_CORRUPTION = 12
    }
}
