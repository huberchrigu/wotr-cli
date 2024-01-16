package ch.chrigu.wotr

import ch.chrigu.wotr.action.KillAction
import ch.chrigu.wotr.action.MoveAction
import ch.chrigu.wotr.action.MusterAction
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameStateHolder
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName
import org.springframework.context.annotation.Bean
import org.springframework.shell.Availability
import org.springframework.shell.AvailabilityProvider
import org.springframework.shell.command.CommandHandlingResult
import org.springframework.shell.command.CommandRegistration
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.CommandAvailability
import org.springframework.shell.command.annotation.ExceptionResolver
import org.springframework.shell.command.annotation.Option
import org.springframework.shell.command.annotation.OptionValues

@Command
class WotrCommands(private val gameStateHolder: GameStateHolder) {
    @Command(command = ["move"], alias = ["mo"])
    fun moveCommand(
        @Option(shortNames = ['f']) @OptionValues(provider = ["locationCompletionProvider"]) from: String,
        @Option(shortNames = ['t']) @OptionValues(provider = ["locationCompletionProvider"]) to: String,
        @Option(shortNames = ['w'], arity = CommandRegistration.OptionArity.ZERO_OR_MORE) @OptionValues(provider = ["figuresCompletionProvider"]) who: Array<String>?
    ): Location {
        val fromLocation = LocationName.get(from)
        val toLocation = LocationName.get(to)
        val figures = Figures.parse(who ?: emptyArray(), fromLocation, gameStateHolder.current)
        val newState = gameStateHolder.apply(MoveAction(fromLocation, toLocation, figures))
        return newState.location[toLocation]!!
    }

    @Command(command = ["kill"], alias = ["k"])
    fun killCommand(
        @Option(shortNames = ['l']) @OptionValues(provider = ["locationCompletionProvider"]) location: String,
        @Option(shortNames = ['w'], arityMin = 0) @OptionValues(provider = ["figuresCompletionProvider"]) who: Array<String>?
    ): Location {
        val locationName = LocationName.get(location)
        val figures = Figures.parse(who ?: emptyArray(), locationName, gameStateHolder.current)
        val newState = gameStateHolder.apply(KillAction(locationName, figures))
        return newState.location[locationName]!!
    }

    @Command(command = ["muster"], alias = ["mu"])
    fun musterCommand(
        @Option(shortNames = ['l']) @OptionValues(provider = ["locationCompletionProvider"]) location: String,
        @Option(shortNames = ['w'], arityMin = 1) @OptionValues(provider = ["reinforcementsCompletionProvider"]) who: Array<String>
    ): Location {
        val locationName = LocationName.get(location)
        val figures = Figures.parse(who, gameStateHolder.current.reinforcements, gameStateHolder.current.location[locationName]!!.nation)
        val newState = gameStateHolder.apply(MusterAction(figures, locationName))
        return newState.location[locationName]!!
    }

    @Command(command = ["undo"])
    @CommandAvailability(provider = ["undoAvailability"])
    fun undo() = gameStateHolder.undo()

    @Bean
    fun undoAvailability() = AvailabilityProvider {
        if (gameStateHolder.allowUndo()) Availability.available() else Availability.unavailable("Nothing to undo")
    }

    @Command(command = ["redo"])
    @CommandAvailability(provider = ["redoAvailability"])
    fun redo() = gameStateHolder.redo()

    @Bean
    fun redoAvailability() = AvailabilityProvider {
        if (gameStateHolder.allowRedo()) Availability.available() else Availability.unavailable("Nothing to redo")
    }

    @ExceptionResolver
    fun handleException(e: Exception) = CommandHandlingResult.of(e.message + "\n", 1)
}
