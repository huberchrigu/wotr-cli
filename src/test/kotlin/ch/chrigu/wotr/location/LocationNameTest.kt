package ch.chrigu.wotr.location

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LocationNameTest {
    @Test
    fun `should not have duplicate shortcuts`() {
        val duplicates = LocationName.entries.groupBy { it.shortcut }
            .filter { (_, names) -> names.size > 1 }
        assertThat(duplicates).isEmpty()
    }
}
