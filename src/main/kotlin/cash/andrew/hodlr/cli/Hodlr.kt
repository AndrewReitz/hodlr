package cash.andrew.hodlr.cli

import cash.andrew.hodlr.component
import cash.andrew.hodlr.componentBuilder
import cash.andrew.hodlr.config.ConfigLoader
import cash.andrew.hodlr.config.ConfigNotFoundException
import cash.andrew.hodlr.config.DEFAULT_LOG_LEVEL
import cash.andrew.hodlr.logging.LogLevel
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.switch
import com.github.ajalt.clikt.parameters.types.file
import com.typesafe.config.ConfigException
import java.io.File

class Hodlr : CliktCommand() {
  private val logLevel: LogLevel by option(help = "LogLevel - default is INFO")
      .switch(
          "--trace" to LogLevel.TRACE,
          "--debug" to LogLevel.DEBUG,
          "--info" to LogLevel.INFO,
          "--warn" to LogLevel.WARN,
          "--error" to LogLevel.ERROR
      ).default(DEFAULT_LOG_LEVEL)

  private val config: File by option("--config", help = "Config file to load. Default is hodlr.conf")
      .file(exists = true, folderOkay = false)
      .default(File("hodlr.conf"))

  override fun run() {
    if (!config.exists()) {
      echo("Configuration was not provided. Please provide one with --config", err = true)
      throw ConfigNotFoundException()
    }

    val configContainer = try {
      ConfigLoader().load(config)
    } catch (e: ConfigException) {
      echo(e.message, err = true)
      throw e
    }

    component = componentBuilder
        .configContainer(configContainer)
        .logLevel(logLevel)
        .build()
  }
}

