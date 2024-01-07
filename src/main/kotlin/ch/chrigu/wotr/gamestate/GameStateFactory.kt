package ch.chrigu.wotr.gamestate

import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName

object GameStateFactory {
    fun newGame() = GameState(LocationName.entries.associateWith { Location() })
}
