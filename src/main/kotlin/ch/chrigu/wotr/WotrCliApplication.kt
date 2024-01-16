package ch.chrigu.wotr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.shell.command.annotation.EnableCommand

@SpringBootApplication
@EnableCommand(BoardCommands::class, UndoRedoCommands::class)
class WotrCliApplication

fun main(args: Array<String>) {
    runApplication<WotrCliApplication>(*args)
}
