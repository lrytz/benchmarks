package misc

import java.nio.charset.Charset
import java.nio.file.{Files, Path}

import org.openjdk.jmh.annotations.{Benchmark, Scope, Setup, State, _}

import scala.reflect.io.AbstractFile

/**
 * sbt \
 *   clean \
 *   'set List(scalacOptions := Nil, scalaHome := Some(file("/Users/luc/scala/scala-2.11.8/")), scalaVersion := "2.11.8")' \
 *   "jmh:run -p compilerArgs=@/Users/luc/scala/scala2/sandbox/files -f 1 -wi 90 -i 10 -bm avgt CompilerBenchmark"
 *
 * where @files contains
 *   -nowarn
 *   -language:_
 *   /Users/luc/scala/better-files/core/src/main/scala/better/files/Cmds.scala
 *   /Users/luc/scala/better-files/core/src/main/scala/better/files/File.scala
 *   /Users/luc/scala/better-files/core/src/main/scala/better/files/Implicits.scala
 *   /Users/luc/scala/better-files/core/src/main/scala/better/files/package.scala
 *   /Users/luc/scala/better-files/core/src/main/scala/better/files/Scanner.scala
 *   /Users/luc/scala/better-files/core/src/main/scala/better/files/ThreadBackedFileMonitor.scala
 *
 */

@State(Scope.Thread)
class CompilerBenchmark {

  @Param(Array("-version"))
  var compilerArgs: String = _

  @Benchmark
  def compile: Boolean = {
    val driver = new scala.tools.nsc.MainClass {
      override def processSettingsHook(): Boolean = {
        settings.usejavacp.value = true
        true
      }
    }
    driver.process(compilerArgs.split(" +"))
    true
  }
}
