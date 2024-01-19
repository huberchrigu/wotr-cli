package ch.chrigu.wotr.commands

import ch.chrigu.wotr.gamestate.GameStateHolder
import org.springframework.context.annotation.Bean
import org.springframework.shell.Availability
import org.springframework.shell.AvailabilityProvider
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.CommandAvailability

@Command(group = "Undo/Redo")
class UndoRedoCommands(private val gameStateHolder: GameStateHolder) {
    @Command(command = ["undo"], description = "Undo last command")
    @CommandAvailability(provider = ["undoAvailability"])
    fun undo() = gameStateHolder.undo()

    @Bean
    fun undoAvailability() = AvailabilityProvider {
        if (gameStateHolder.allowUndo()) Availability.available() else Availability.unavailable("Nothing to undo")
    }

    @Command(command = ["redo"], description = "Redo undone command")
    @CommandAvailability(provider = ["redoAvailability"])
    fun redo() = gameStateHolder.redo()

    @Bean
    fun redoAvailability() = AvailabilityProvider {
        if (gameStateHolder.allowRedo()) Availability.available() else Availability.unavailable("Nothing to redo")
    }
}
