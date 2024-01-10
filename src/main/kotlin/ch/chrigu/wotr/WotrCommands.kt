package ch.chrigu.wotr

import ch.chrigu.wotr.action.MoveAction
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameStateHolder
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName
import org.springframework.shell.Availability
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option
import org.springframework.shell.command.annotation.OptionValues

@Command
class WotrCommands(private val gameStateHolder: GameStateHolder) {
    @Command(command = ["move"], alias = ["m"])
    fun moveCommand(
        @Option(shortNames = ['f']) @OptionValues(provider = ["locationCompletionProvider"]) from: String,
        @Option(shortNames = ['t']) @OptionValues(provider = ["locationCompletionProvider"]) to: String,
        @Option(shortNames = ['w'], arityMin = 0) @OptionValues(provider = ["figuresCompletionProvider"]) who: Array<String>
    ): Location {
        val fromLocation = LocationName.get(from)
        val toLocation = LocationName.get(to)
        val figures = Figures.parse(who, fromLocation, gameStateHolder.current)
        val newState = gameStateHolder.apply(MoveAction(fromLocation, toLocation, figures))
        return newState.location[toLocation]!!
    }

    @Command(command = ["undo"])
    fun undo() = gameStateHolder.undo()

    fun undoAvailability() = if (gameStateHolder.allowUndo()) Availability.available() else Availability.unavailable("Nothing to undo")

    @Command(command = ["redo"])
    fun redo() = gameStateHolder.redo()

    fun redoAvailability() = if (gameStateHolder.allowRedo()) Availability.available() else Availability.unavailable("Nothing to redo")
}
