package ch.chrigu.wotr.gamestate

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GameStateFactoryTest {

    @Test
    fun `adjacent locations should have a pointer back`() {
        val all = GameStateFactory.newGame().location
        all.values.forEach { location ->
            location.adjacentLocations.forEach { neighbor ->
                assertThat(all[neighbor]!!.adjacentLocations).contains(location.name)
            }
        }
    }
}
