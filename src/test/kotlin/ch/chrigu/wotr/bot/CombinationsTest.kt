package ch.chrigu.wotr.bot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CombinationsTest {
    @Test
    fun `should list all combinations`() {
        val result = Combinations.allSizes(listOf("a", "b", "c"))
        assertThat(result).containsExactlyInAnyOrder(
            emptyList(), listOf("a"), listOf("b"), listOf("c"),
            listOf("a", "b"), listOf("a", "c"), listOf("b", "c"),
            listOf("a", "b", "c")
        )
    }

    @Test
    fun `should list combinations of size 2`() {
        val result = Combinations.allSizes(listOf("a", "b", "c"))
        assertThat(result).containsExactlyInAnyOrder(
            listOf("a", "b"), listOf("a", "c"), listOf("b", "c")
        )
    }
}
