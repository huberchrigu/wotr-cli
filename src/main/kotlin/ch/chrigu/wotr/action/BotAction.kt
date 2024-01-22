package ch.chrigu.wotr.action

import ch.chrigu.wotr.bot.BotActionFactory
import ch.chrigu.wotr.gamestate.GameState
import org.jline.terminal.Terminal

class BotAction(private val terminal: Terminal, private val botActionFactory: BotActionFactory) : GameAction {
    override fun apply(oldState: GameState): GameState {
        val nextAction = botActionFactory.getNext(oldState)
        terminal.writer().println(nextAction.toString())
        return nextAction.apply(oldState)
    }

    override fun toString() = "Bot action"
}
