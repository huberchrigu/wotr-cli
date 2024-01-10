package ch.chrigu.wotr

import ch.chrigu.wotr.figure.FiguresCompletionProvider
import ch.chrigu.wotr.gamestate.GameStateHolder
import ch.chrigu.wotr.location.LocationCompletionProvider
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.shell.test.ShellAssertions.assertThat
import org.springframework.shell.test.ShellTestClient
import org.springframework.shell.test.autoconfigure.ShellTest
import java.util.concurrent.TimeUnit

@ShellTest
@Import(WotrPromptProvider::class, GameStateHolder::class)
class SpringShellTest(@Autowired private val client: ShellTestClient) {

    @Test
    fun `should provide suggestions`() {
        val session = client.interactive().run()
        await().atMost(2, TimeUnit.SECONDS).untilAsserted {
            assertThat(session.screen()).containsText("wotr")
        }

        session.write(session.writeSequence().text("move --from ri").ctrl('\t').build())
        await().atMost(2, TimeUnit.SECONDS).untilAsserted {
            assertThat(session.screen()).containsText("rivendell")
            assertThat(session.screen()).containsText("ri")
        }

        session.write(session.writeSequence().text(" dg -w ").ctrl('\t').build())
        await().atMost(2, TimeUnit.SECONDS).untilAsserted {
            assertThat(session.screen()).containsText("021")
        }
    }

    @TestConfiguration
    class Config {
        @Bean
        fun locationCompletionProvider() = LocationCompletionProvider()

        @Bean
        fun figuresCompletionProvider(gameStateHolder: GameStateHolder) = FiguresCompletionProvider(gameStateHolder)
    }
}
