package ch.chrigu.wotr.commands

import ch.chrigu.wotr.action.BotAction
import ch.chrigu.wotr.bot.BotActionFactory
import ch.chrigu.wotr.gamestate.GameStateHolder
import org.jline.terminal.Terminal
import org.springframework.shell.command.annotation.Command

@Command(group = "Bot")
class BotCommands(private val gameStateHolder: GameStateHolder, private val terminal: Terminal, private val botActionFactory: BotActionFactory) {
    @Command(command = ["bot"], alias = ["b"], description = "Shadow bot takes its next move")
    fun botCommand() = gameStateHolder.apply(BotAction(terminal, botActionFactory))
}
