package ch.chrigu.wotr.nation

data class Nation(val box: Int, val active: Boolean, val name: NationName) {
    init {
        require(box >= 0)
        if (!active) require(box > 0)
    }

    fun isOnWar() = box == 0

    fun moveDown() = copy(box = box - 1)
}
