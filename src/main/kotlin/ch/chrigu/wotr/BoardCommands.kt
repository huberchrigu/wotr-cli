package ch.chrigu.wotr

import ch.chrigu.wotr.action.KillAction
import ch.chrigu.wotr.action.MoveAction
import ch.chrigu.wotr.action.MusterAction
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameStateHolder
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName
import org.springframework.shell.command.CommandRegistration
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option
import org.springframework.shell.command.annotation.OptionValues

@Command(group = "Board", description = "Commands that modify the board state")
class BoardCommands(private val gameStateHolder: GameStateHolder) {
    @Command(command = ["move"], alias = ["mo"], description = "Move figures from one location to another")
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

    @Command(command = ["kill"], alias = ["k"], description = "Remove figures from location")
    fun killCommand(
        @Option(shortNames = ['l']) @OptionValues(provider = ["locationCompletionProvider"]) location: String,
        @Option(shortNames = ['w'], arity = CommandRegistration.OptionArity.ZERO_OR_MORE) @OptionValues(provider = ["figuresCompletionProvider"]) who: Array<String>?
    ): Location {
        val locationName = LocationName.get(location)
        val figures = Figures.parse(who ?: emptyArray(), locationName, gameStateHolder.current)
        val newState = gameStateHolder.apply(KillAction(locationName, figures))
        return newState.location[locationName]!!
    }

    @Command(command = ["muster"], alias = ["mu"], description = "Move figures from reinforcements to location")
    fun musterCommand(
        @Option(shortNames = ['l']) @OptionValues(provider = ["locationCompletionProvider"]) location: String,
        @Option(shortNames = ['w'], arityMin = 1) @OptionValues(provider = ["reinforcementsCompletionProvider"]) who: Array<String>
    ): Location {
        val locationName = LocationName.get(location)
        val figures = Figures.parse(who, gameStateHolder.current.reinforcements, gameStateHolder.current.location[locationName]!!.nation)
        val newState = gameStateHolder.apply(MusterAction(figures, locationName))
        return newState.location[locationName]!!
    }
}
