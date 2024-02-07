package ch.chrigu.wotr.bot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class CombinationsTest {
    @ParameterizedTest
    @MethodSource("allCombinations")
    fun `should list all combinations`(input: List<String>, expected: Array<List<String>>) {
        val result = Combinations.allSizes(input)
        assertThat(result).containsExactlyInAnyOrder(*expected)
    }

    @ParameterizedTest
    @MethodSource("ofSize2")
    fun `should list combinations of size 2`(input: List<String>, expected: Array<List<String>>?) {
        val result = Combinations.ofSize(input, 2)
        if (expected == null)
            assertThat(result).isEmpty()
        else
            assertThat(result).containsExactlyInAnyOrder(*expected)
    }

    companion object {
        @JvmStatic
        fun ofSize2(): Stream<Arguments> = Stream.of(
            Arguments.of(listOf("a", "b", "c"), arrayOf(listOf("a", "b"), listOf("a", "c"), listOf("b", "c"))),
            Arguments.of(listOf("a", "b"), arrayOf(listOf("a", "b"))),
            Arguments.of(listOf("a"), null)
        )

        @JvmStatic
        fun allCombinations(): Stream<Arguments> = Stream.of(
            Arguments.of(
                listOf("a", "b", "c"), arrayOf(
                    emptyList(), listOf("a"), listOf("b"), listOf("c"),
                    listOf("a", "b"), listOf("a", "c"), listOf("b", "c"),
                    listOf("a", "b", "c")
                )
            ),
            Arguments.of(listOf("a", "b"), arrayOf(emptyList(), listOf("a"), listOf("b"), listOf("a", "b"))),
            Arguments.of(listOf("a"), arrayOf(emptyList(), listOf("a")))
        )
    }
}
