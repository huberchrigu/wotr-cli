package ch.chrigu.wotr.card

enum class EventType {
    CHARACTER, STRATEGY;

    override fun toString() = name.take(1)
}
