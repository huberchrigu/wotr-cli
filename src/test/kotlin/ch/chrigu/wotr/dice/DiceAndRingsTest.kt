package ch.chrigu.wotr.dice

import ch.chrigu.wotr.player.Player
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DiceAndRingsTest {
    @Test
    fun `should use only one die`() {
        val testee = DiceAndRings(listOf(DieType.ARMY, DieType.ARMY, DieType.ARMY_MUSTER), 0, Player.SHADOW)
        val result = testee.use(DieUsage(DieType.ARMY, false, Player.SHADOW))
        assertThat(result.rolled).containsExactly(DieType.ARMY, DieType.ARMY_MUSTER)
    }
}
