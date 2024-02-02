package ch.chrigu.wotr.bot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PermutationsTest {
    @Test
    fun `should list all permutations`() {
        val result = Permutations.of(listOf("a", "b", "c"))
        assertThat(result).containsExactlyInAnyOrder(
            emptyList(), listOf("a"), listOf("b"), listOf("c"),
            listOf("a", "b"), listOf("a", "c"), listOf("b", "c"),
            listOf("a", "b", "c")
        )
    }
}
