package ch.chrigu.wotr.location

import org.springframework.shell.CompletionContext
import org.springframework.shell.CompletionProposal
import org.springframework.shell.completion.CompletionProvider
import org.springframework.stereotype.Component

@Component
class LocationCompletionProvider : CompletionProvider {
    override fun apply(t: CompletionContext) = LocationName.search(t.currentWord())
        .map { CompletionProposal(it) }
}
