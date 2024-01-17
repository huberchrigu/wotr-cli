package ch.chrigu.wotr.player

enum class Player {
    SHADOW, FREE_PEOPLE;

    val opponent: Player
        get() = entries.first { it != this }
}
