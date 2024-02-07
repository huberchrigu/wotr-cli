package ch.chrigu.wotr.bot

object Combinations {
    fun <T> allSizes(all: List<T>): List<List<T>> {
        if (all.isEmpty())
            return listOf(emptyList())
        else {
            val first = listOf(all.first())
            val other = allSizes(all.drop(1))
            return other.map { first + it } + other
        }
    }

    fun <T> ofSize(all: List<T>, size: Int): List<List<T>> {
        if (all.size < size) return emptyList()
        if (size == 1) return all.map { listOf(it) }
        return all.flatMapIndexed { index, pick -> ofSize(all.drop(index + 1), size - 1).map { listOf(pick) + it } }
    }
}
