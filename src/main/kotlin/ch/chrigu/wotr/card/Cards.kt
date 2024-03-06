package ch.chrigu.wotr.card

/**
 * Yet we're interested in the number of shadow cards per type only. This is a simplification, but implementing all cards will
 * be a time intensive task.
 */
data class Cards(private val shadow: Map<EventType, Int> = EventType.entries.associateWith { 0 }) {
    init {
        require(shadow.values.all { it >= 0 }) { "Cannot play cards that are not drawn yet" }
        require(numCards() <= 6) { "Should not hold more than 6 cards" }
    }

    fun play(type: EventType) = copy(shadow = shadow.mapValues { (t, num) -> if (type == t) num - 1 else num })

    fun phase1() = if (numCards() <= 4)
        draw(EventType.CHARACTER).draw(EventType.STRATEGY)
    else if (numCards() == 5)
        draw(EventType.CHARACTER)
    else
        this

    fun draw(type: EventType) = copy(shadow = shadow.mapValues { (t, num) -> if (type == t) num + 1 else num })

    fun numCards() = shadow.values.fold(0) { a, b -> a + b }

    override fun toString() = shadow.map { (t, num) -> "$num$t" }.joinToString("/")
}
