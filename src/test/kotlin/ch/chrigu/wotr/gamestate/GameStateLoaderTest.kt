package ch.chrigu.wotr.gamestate

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

class GameStateLoaderTest {
    @Test
    fun `should save and load initial state`() {
        val initialState = GameStateFactory.newGame()
        val file = File("build/test.json")
        GameStateLoader.saveFile(file, initialState)
        val loaded = GameStateLoader.loadFile(file)
        assertThat(loaded.toString()).isEqualTo(initialState.toString())
    }
}
