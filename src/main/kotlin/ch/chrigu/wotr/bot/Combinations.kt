package ch.chrigu.wotr.bot

import ch.chrigu.wotr.bot.Combinations.ofSize

object Combinations {
    fun <T> allSizes(all: List<T>): List<List<T>> = TypedCombinations<T>().allSizesRec(all)

    fun <T> ofSize(all: List<T>, size: Int): List<List<T>> = TypedCombinations<T>().ofSizeRec(all to size)
}

private class TypedCombinations<T> {
    val allSizesRec = DeepRecursiveFunction<List<T>, List<List<T>>> { all ->
        if (all.isEmpty())
            listOf(emptyList())
        else {
            val first = listOf(all.first())
            val other = callRecursive(all.drop(1))
            other.map { first + it } + other
        }
    }

    val ofSizeRec = DeepRecursiveFunction<Pair<List<T>, Int>, List<List<T>>> { (all, size) ->
        if (all.size < size) emptyList()
        else if (size == 1) all.map { listOf(it) }
        else all.flatMapIndexed { index, pick -> ofSize(all.drop(index + 1), size - 1).map { listOf(pick) + it } }
    }
}