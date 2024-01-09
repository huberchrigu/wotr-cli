package ch.chrigu.wotr.nation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NationNameTest {
    @Test
    fun `shortcuts should be unique`() {
        assertThat(NationName.entries.groupBy { it.shortcut }.filterValues { it.size > 1 }).isEmpty()
    }
}
