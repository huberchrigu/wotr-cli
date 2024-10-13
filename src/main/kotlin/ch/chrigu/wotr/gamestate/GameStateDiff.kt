package ch.chrigu.wotr.gamestate

class GameStateDiff(left: GameState, right: GameState) {
    private val locations = left.location.values
        .mapNotNull { Diff.from(it, right.location[it.name]) }
    private val dice = listOfNotNull(Diff.from(left.dice.freePeople, right.dice.freePeople), Diff.from(left.dice.shadow, right.dice.shadow))
    private val cards = listOfNotNull(Diff.from(left.cards, right.cards))
    private val reinforcements = listOfNotNull(Diff.from(left.reinforcements, right.reinforcements))
    private val all = locations + dice + cards + reinforcements

    override fun toString() = printColumns("left", "right") + "\n" +
            all.joinToString("\n")

    class Diff<T>(private val left: T, private val right: T) {
        override fun toString() = printColumnsMultipleLines(left.toString(), right.toString())

        companion object {
            fun <T> from(left: T, right: T) = if (left == right) null else Diff(left, right)
        }
    }

    companion object {
        private const val COLUMN_LENGTH = 50
        private fun printColumnsMultipleLines(left: String, right: String): String = printColumns(left.take(COLUMN_LENGTH), right.take(COLUMN_LENGTH)) +
                if (left.length > COLUMN_LENGTH || right.length > COLUMN_LENGTH)
                    "\n" + printColumnsMultipleLines(left.drop(COLUMN_LENGTH), right.drop(COLUMN_LENGTH))
                else
                    ""

        private fun printColumns(left: String, right: String) = left + whitespace(left) + " | $right" + whitespace(right)
        private fun whitespace(after: String) = (0 until (COLUMN_LENGTH - after.length)).joinToString("") { " " }
    }
}
