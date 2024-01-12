package ch.chrigu.wotr.figure

import ch.chrigu.wotr.gamestate.GameStateHolder
import org.springframework.shell.CompletionContext
import org.springframework.shell.CompletionProposal
import org.springframework.shell.completion.CompletionProvider

abstract class AbstractFiguresCompletionProvider(protected val gameStateHolder: GameStateHolder) : CompletionProvider {
    override fun apply(context: CompletionContext): List<CompletionProposal> {
        return FigureParser(getWhoValue(context)).getCompletionProposals(getAllFigures(context))
            .map { CompletionProposal(it) }
    }

    abstract fun getAllFigures(context: CompletionContext): Figures

    abstract fun getWhoValue(context: CompletionContext): List<String>
}
