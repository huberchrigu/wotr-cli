package ch.chrigu.wotr.figure

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class FigureTypeTest {
    @Test
    fun `shortcuts should be unique`() {
        val duplicates = FigureType.entries.filter { it.isUniqueCharacter }
            .groupBy { it.shortcut }
            .filterValues { it.size > 1 }
        Assertions.assertThat(duplicates).isEmpty()
    }
}
