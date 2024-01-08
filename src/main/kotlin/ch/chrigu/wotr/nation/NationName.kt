package ch.chrigu.wotr.nation

import ch.chrigu.wotr.player.Player

enum class NationName(val player: Player) {
    GONDOR(Player.FREE_PEOPLE), ROHAN(Player.FREE_PEOPLE), ELVES(Player.FREE_PEOPLE), DWARVES(Player.FREE_PEOPLE), NORTHMEN(Player.FREE_PEOPLE),
    SAURON(Player.SHADOW), ISENGARD(Player.SHADOW), SOUTHRONS_AND_EASTERLINGS(Player.SHADOW);
}
