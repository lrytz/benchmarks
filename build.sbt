name := "Java Benchmarks"

version := "1.0-SNAPSHOT"

enablePlugins(JmhPlugin)

resolvers += Resolver.mavenLocal

// use 2.12.0-M2 as soon as it's out
scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
   "org.scala-lang" % "scala-compiler" % scalaVersion.value
)
