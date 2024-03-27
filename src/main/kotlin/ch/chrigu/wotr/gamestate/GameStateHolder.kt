package ch.chrigu.wotr.gamestate

import ch.chrigu.wotr.action.GameAction
import org.springframework.stereotype.Component

@Component
class GameStateHolder {
    private val history = mutableListOf(GameStateFactory.newGame())
    private var currentIndex = 0
    val current
        get() = history[currentIndex]

    fun apply(action: GameAction): GameState {
        val newState = action.apply(current)
        removeFutureActions()
        history.add(newState)
        currentIndex++
        return newState
    }

    fun undo(): GameState {
        check(allowUndo())
        currentIndex--
        return current
    }

    fun redo(): GameState {
        check(allowRedo())
        currentIndex++
        return current
    }

    fun allowUndo() = currentIndex > 0

    fun allowRedo() = currentIndex < history.size - 1

    fun reset(newState: GameState) {
        history.clear()
        history.add(newState)
        currentIndex = 0
    }

    private fun removeFutureActions() {
        repeat(history.size - currentIndex - 1) { history.removeLast() }
    }
}
