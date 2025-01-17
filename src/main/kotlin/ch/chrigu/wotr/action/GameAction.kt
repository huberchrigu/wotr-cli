package ch.chrigu.wotr.action

import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName

interface GameAction {
    fun alteredObjects(state: GameState): List<Any> = alteringLocations.mapNotNull { state.location[it] }
    val alteringLocations: List<LocationName> get() = emptyList()
    fun apply(oldState: GameState): GameState

    /**
     * Like [apply], but used for evaluating a bot action.
     */
    fun simulate(oldState: GameState): GameState = apply(oldState)
    fun tryToCombine(other: GameAction): GameAction? = null
    fun tryToApply(oldState: GameState) = try {
        apply(oldState)
    } catch (e: IllegalArgumentException) {
        null
    } catch (e: IllegalStateException) {
        null
    }

    fun requiredDice(): Set<DieType> = throw IllegalStateException("The action ${javaClass.simpleName} does not support to be played within a die action")
}
