package ch.chrigu.wotr.bot

object Permutations {
    fun <T> of(all: List<T>): List<List<T>> {
        if (all.isEmpty())
            return listOf(emptyList())
        else {
            val first = listOf(all.first())
            val other = of(all.drop(1))
            return listOf(first) + other.map { first + it } + other
        }
    }
}
