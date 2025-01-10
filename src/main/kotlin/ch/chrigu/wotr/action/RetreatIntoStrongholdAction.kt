package ch.chrigu.wotr.action

import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.location.LocationType

data class RetreatIntoStrongholdAction(private val location: LocationName) : GameAction {
    init {
        require(location.type == LocationType.STRONGHOLD) { "$location must be a stronghold" }
    }

    override val alteringLocations = listOf(location)

    override fun apply(oldState: GameState): GameState {
        return oldState.retreatIntoStronghold(location)
    }

    override fun toString() = "Retreat into stronghold $location"
}
