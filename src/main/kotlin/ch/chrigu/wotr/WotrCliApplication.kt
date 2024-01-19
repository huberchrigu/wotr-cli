package ch.chrigu.wotr

import ch.chrigu.wotr.commands.BoardCommands
import ch.chrigu.wotr.commands.BotCommands
import ch.chrigu.wotr.commands.UndoRedoCommands
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.shell.command.annotation.EnableCommand

@SpringBootApplication
@EnableCommand(BoardCommands::class, UndoRedoCommands::class, BotCommands::class)
class WotrCliApplication

fun main(args: Array<String>) {
    runApplication<WotrCliApplication>(*args)
}
