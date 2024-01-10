package ch.chrigu.wotr.figure

import ch.chrigu.wotr.nation.NationName

class FigureParser(private val who: List<String>) {
    fun select(figures: Figures) = who.map { part ->
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
        .fold(Figures(emptyList())) { a, b ->
            require(a.union(b).isEmpty())
            a + b
        }

    fun getCompletionProposals(figures: Figures): List<String> {
        val prefix = who.take(who.size - 1)
        val before = FigureParser(prefix).select(figures)
        val remaining = figures - before
        val current = who.last()
        val armyOptions = (0..remaining.numRegulars())
            .flatMap { regular -> (0..remaining.numElites()).map { regular.toString() + it } }
            .flatMap { units -> (0..remaining.numLeadersOrNazgul()).map { units + it } }
        val options = armyOptions + remaining.characters().map { it.type.shortcut!! }
        return options.filter { it.startsWith(current, true) }
    }
}
