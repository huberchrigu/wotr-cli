package ch.chrigu.wotr.figure

import ch.chrigu.wotr.nation.NationName

class FigureParser(private val who: String) {
    fun select(figures: Figures) = who.split(",").map { it.trim() }.map { part ->
        check(part.isNotEmpty())
        if (part[0].isDigit()) {
            val digits = part.takeWhile { it.isDigit() }
            require(digits.length in 1..3)
            val nation = if (part.contains("(") && part.contains(")")) NationName.find(part.substringAfter(")").substringBefore(")")) else null
            figures.subSet(digits[0].digitToInt(), digits.getOrNull(1)?.digitToInt() ?: 0, digits.getOrNull(2)?.digitToInt() ?: 0, nation)
        } else {
            require(part.length == 2)
            val resolvedCharacters = part.chunked(2).map { FigureType.fromShortcut(it) }
            figures.subSet(resolvedCharacters)
        }
    }
        .reduce { a, b ->
            require(a.union(b).isEmpty())
            a + b
        }
}
