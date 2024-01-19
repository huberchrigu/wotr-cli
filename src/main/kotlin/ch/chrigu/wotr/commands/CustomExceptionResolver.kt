package ch.chrigu.wotr.commands

import org.springframework.shell.command.CommandExceptionResolver
import org.springframework.shell.command.CommandHandlingResult
import org.springframework.stereotype.Component
import java.lang.Exception

@Component
class CustomExceptionResolver : CommandExceptionResolver {
    override fun resolve(e: Exception): CommandHandlingResult = CommandHandlingResult.of(e.message + "\n", 1)
}
