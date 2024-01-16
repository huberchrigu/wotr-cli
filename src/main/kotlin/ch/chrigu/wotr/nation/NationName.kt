package ch.chrigu.wotr.nation

import ch.chrigu.wotr.player.Player
import java.util.*

enum class NationName(val player: Player, val shortcut: String) {
    GONDOR(Player.FREE_PEOPLE, "go"), ROHAN(Player.FREE_PEOPLE, "ro"), ELVES(Player.FREE_PEOPLE, "el"), DWARVES(Player.FREE_PEOPLE, "dw"), NORTHMEN(Player.FREE_PEOPLE, "no"),
    FREE_PEOPLE(Player.FREE_PEOPLE, "fp"),
    SAURON(Player.SHADOW, "sa"), ISENGARD(Player.SHADOW, "is"), SOUTHRONS_AND_EASTERLINGS(Player.SHADOW, "se");

    val fullName = name.lowercase().split("_")
        .joinToString(" ") { part -> part.replaceFirstChar { it.titlecase(Locale.getDefault()) } }

    override fun toString() = fullName

    companion object {
        fun find(name: String) = entries.first { name.equals(it.fullName, true) || name.equals(it.shortcut, true) }
    }
}
