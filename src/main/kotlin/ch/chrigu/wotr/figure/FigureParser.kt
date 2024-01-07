package ch.chrigu.wotr.figure

class FigureParser(private val who: String) {
    fun select(figures: Figures): Figures {
        val digits = who.takeWhile { it.isDigit() }
        val characters = who.substringAfter(digits)
        require(digits.length in 1..3)
        require(characters.length % 2 == 0)
        val resolvedCharacters = characters.chunked(2).map { FigureType.fromShortcut(it) }
        return figures.subSet(digits[0].digitToInt(), digits.getOrNull(1)?.digitToInt() ?: 0, digits.getOrNull(2)?.digitToInt() ?: 0, resolvedCharacters)
    }
}
