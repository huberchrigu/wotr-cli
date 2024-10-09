package ch.chrigu.wotr.nation

import kotlinx.serialization.Serializable

@Serializable
data class Nation(val box: Int, val active: Boolean, val name: NationName) {
    init {
        require(box >= 0)
        if (!active) require(box > 0)
    }

    fun isAtWar() = box == 0

    fun moveDown() = copy(box = box - 1) // TODO: Should happen automatically with MoveActions and AttackActions
    fun activate(): Nation {
        require(!active)
        return copy(active = true)
    }
}
