package ch.chrigu.wotr

import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.springframework.shell.jline.PromptProvider
import org.springframework.stereotype.Component

@Component
class WotrPromptProvider : PromptProvider {
    override fun getPrompt() = AttributedString("wotr:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
}
