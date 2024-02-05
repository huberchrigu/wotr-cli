package ch.chrigu.wotr.bot

object Combinations {
    fun <T> allSizes(all: List<T>): List<List<T>> {
        if (all.isEmpty())
            return listOf(emptyList())
        else {
            val first = listOf(all.first())
            val other = allSizes(all.drop(1))
            return listOf(first) + other.map { first + it } + other
        }
    }

    fun <T> ofSize(all: List<T>, size: Int): List<List<T>> {
        if (all.size < size) return emptyList()
        if (size == 1) return all.map { listOf(it) }
        return all.flatMap { pick -> ofSize(all - pick, size - 1).map { listOf(pick) + it } }
    }
}
