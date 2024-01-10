package ch.chrigu.wotr.figure

import ch.chrigu.wotr.gamestate.GameStateHolder
import ch.chrigu.wotr.location.LocationName
import org.springframework.shell.CompletionContext
import org.springframework.shell.CompletionProposal
import org.springframework.shell.completion.CompletionProvider
import org.springframework.stereotype.Component

@Component
class FiguresCompletionProvider(private val gameStateHolder: GameStateHolder) : CompletionProvider {
    override fun apply(context: CompletionContext): List<CompletionProposal> {
        val nation = LocationName.get(getFromValue(context))
        val location = gameStateHolder.current.location[nation]!!
        return FigureParser(getWhoValue(context)).getCompletionProposals(location.nonBesiegedFigures)
            .map { CompletionProposal(it) }
    }

    private fun getWhoValue(context: CompletionContext) = context.words.dropWhile { it != "-w" && it != "--who" }.drop(1)

    private fun getFromValue(context: CompletionContext) = if (context.words.first().startsWith("-"))
        context.words[1]
    else
        context.words.first()
}
