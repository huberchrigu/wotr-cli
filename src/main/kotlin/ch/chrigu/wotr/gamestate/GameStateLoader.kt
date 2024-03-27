package ch.chrigu.wotr.gamestate

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object GameStateLoader {
    fun saveFile(file: File, gameState: GameState) = file.run {
        createNewFile()
        writeText(Json.encodeToString(gameState))
    }

    fun loadFile(file: File) = Json.decodeFromString<GameState>(file.readText())
}
