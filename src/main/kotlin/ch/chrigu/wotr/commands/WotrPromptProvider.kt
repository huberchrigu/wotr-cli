package ch.chrigu.wotr.commands

import ch.chrigu.wotr.gamestate.GameStateHolder
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.springframework.shell.jline.PromptProvider
import org.springframework.stereotype.Component

@Component
class WotrPromptProvider(private val gameStateHolder: GameStateHolder) : PromptProvider {
    override fun getPrompt() = AttributedString("${gameStateHolder.current}>", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
}
