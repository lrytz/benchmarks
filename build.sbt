import JmhKeys._

name := "Java Benchmarks"

version := "1.0-SNAPSHOT"

jmhSettings

outputTarget in Jmh := target.value / s"scala-${scalaBinaryVersion.value}"

libraryDependencies ++= Seq(
  // Add your own project dependencies in the form:
  // "group" % "artifact" % "version"
)
