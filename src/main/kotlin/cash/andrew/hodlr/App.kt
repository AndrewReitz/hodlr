package cash.andrew.hodlr

import cash.andrew.hodlr.cli.Accounts
import cash.andrew.hodlr.config.ConfigNotFoundException
import cash.andrew.hodlr.cli.Hodlr
import cash.andrew.hodlr.cli.Products
import cash.andrew.hodlr.cli.Run
import com.github.ajalt.clikt.core.subcommands
import com.typesafe.config.ConfigException
import kotlin.system.exitProcess

lateinit var component: AppComponent

fun main(args: Array<String>) = try {
  Hodlr().subcommands(Accounts(), Products(), Run())
      .main(args)
} catch (e: ConfigException) {
  exitProcess(1)
} catch (e: ConfigNotFoundException) {
  exitProcess(1)
}
