package ch.chrigu.wotr.commands

import ch.chrigu.wotr.gamestate.GameStateHolder
import ch.chrigu.wotr.gamestate.GameStateLoader
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option
import java.io.File

@Command(group = "Save/Load")
class SaveLoadCommands(private val gameStateHolder: GameStateHolder) {
    @Command(command = ["save"], description = "Save current game")
    fun save(@Option file: File = File("wotr.json")) = GameStateLoader.saveFile(file, gameStateHolder.current)

    @Command(command = ["load"], description = "Load game")
    fun load(@Option file: File = File("wotr.json")) = gameStateHolder.reset(
        GameStateLoader.loadFile(file)
    )
}
