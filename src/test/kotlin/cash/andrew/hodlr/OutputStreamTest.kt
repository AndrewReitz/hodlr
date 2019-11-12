package cash.andrew.hodlr

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.nio.file.Path

abstract class OutputStreamTest {

  @TempDir lateinit var tempDir: Path

  private val outContent = ByteArrayOutputStream()
  private val originalOut = System.out
  val errorContent = ByteArrayOutputStream()
  private val originalError = System.err

  val outputText: String get() = outContent.toString().trim()
  val errorText: String get() = errorContent.toString().trim()

  @BeforeEach
  fun setup() {
    System.setOut(PrintStream(outContent))
    System.setErr(PrintStream(errorContent))
  }

  @AfterEach
  fun restoreStreams() {
    System.setOut(originalOut)
    System.setErr(originalError)
  }

  val config: File get() = file("hodlr.conf")

  fun file(name: String): File = tempDir.resolve(name).toFile()
}
