package ch.chrigu.wotr

import ch.chrigu.wotr.figure.FiguresCompletionProvider
import ch.chrigu.wotr.figure.ReinforcementsCompletionProvider
import ch.chrigu.wotr.gamestate.GameStateHolder
import ch.chrigu.wotr.location.LocationCompletionProvider
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.shell.test.ShellAssertions.assertThat
import org.springframework.shell.test.ShellScreenAssert
import org.springframework.shell.test.ShellTestClient
import org.springframework.shell.test.ShellTestClient.BaseShellSession
import org.springframework.shell.test.ShellWriteSequence
import org.springframework.shell.test.autoconfigure.ShellTest
import java.util.concurrent.TimeUnit

@ShellTest
@Import(WotrPromptProvider::class, GameStateHolder::class)
class CommandShellTest(@Autowired private val client: ShellTestClient) {
    private lateinit var session: BaseShellSession<*>

    @BeforeEach
    fun initSession() {
        session = client.interactive().run()
        assertScreen { containsText("wotr") }
    }

    @AfterEach
    fun undo() {
        write { text("undo").carriageReturn() }
    }

    @Test
    fun `should move figures`() {
        write { text("move --from ri").ctrl('\t') }
        assertScreen {
            containsText("rivendell")
        }

        write { text(" -t tr -w ").ctrl('\t') }
        assertScreen {
            containsText("021")
        }

        write { text("011").carriageReturn() }
        assertScreen {
            containsText("Trollshaws: 011 (Elves)")
        }
    }

    @Test
    fun `should kill figure`() {
        write { text("k orthanc 100").carriageReturn() }
        assertScreen {
            containsText("Orthanc: 310")
        }
    }

    @Test
    fun `should muster figure`() {
        write { text("muster or 010").carriageReturn() }
        assertScreen {
            containsText("Orthanc: 420")
        }
    }

    private fun assertScreen(shellAssert: ShellScreenAssert.() -> ShellScreenAssert) {
        await().atMost(5, TimeUnit.SECONDS).untilAsserted {
            val screen = session.screen()
            assertThat(screen).shellAssert()
        }
    }

    private fun write(cmd: ShellWriteSequence.() -> ShellWriteSequence) {
        session.write(session.writeSequence().cmd().build())
    }

    @TestConfiguration
    class Config {
        @Bean
        fun locationCompletionProvider() = LocationCompletionProvider()

        @Bean
        fun figuresCompletionProvider(gameStateHolder: GameStateHolder) = FiguresCompletionProvider(gameStateHolder)

        @Bean
        fun reinforcementsCompletionProvider(gameStateHolder: GameStateHolder) = ReinforcementsCompletionProvider(gameStateHolder)
    }
}
