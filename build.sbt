name := "Java Benchmarks"

version := "1.0-SNAPSHOT"

enablePlugins(JmhPlugin)

resolvers += Resolver.mavenLocal

// use 2.12.0-M2 as soon as it's out
scalaVersion := "2.12.0-newopt"

libraryDependencies ++= Seq(
  // Add your own project dependencies in the form:
  // "group" % "artifact" % "version"
)
