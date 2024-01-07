package ch.chrigu.wotr

import ch.chrigu.wotr.action.MoveAction
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameStateHolder
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.CommandAvailability
import org.springframework.shell.command.annotation.OptionValues

@Command
class WotrCommands(private val gameStateHolder: GameStateHolder) {
    @Command(command = ["move"], alias = ["m"])
    fun moveCommand(
        @OptionValues(provider = ["locationCompletionProvider"]) from: String,
        @OptionValues(provider = ["locationCompletionProvider"]) to: String,
        who: String?
    ): Location {
        val fromLocation = LocationName.get(from)
        val toLocation = LocationName.get(to)
        val figures = Figures.parse(who, fromLocation, gameStateHolder.current)
        val newState = gameStateHolder.apply(MoveAction(fromLocation, toLocation, figures))
        return newState.location[toLocation]!!
    }

    @Command(command = ["undo"])
    @CommandAvailability(provider = ["undoAvailability"])
    fun undo() = gameStateHolder.undo()

    fun undoAvailability() = gameStateHolder.allowUndo()

    @Command(command = ["redo"])
    @CommandAvailability(provider = ["redoAvailability"])
    fun redo() = gameStateHolder.redo()

    fun redoAvailability() = gameStateHolder.allowRedo()
}
