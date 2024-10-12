package ch.chrigu.wotr.location

enum class LocationType(val settlement: Boolean = true) {
    NONE(false), VILLAGE, CITY, STRONGHOLD, FORTIFICATION(false);
}
