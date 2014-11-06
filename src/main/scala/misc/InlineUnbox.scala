package misc

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._

/**
 * lucmac:benchmarks luc$ sbt clean 'set scalacOptions in ThisBuild ++= Seq("-optimize")' 'run -f 1 -i 3 -wi 3 -t 1 InlineUnbox'
 *
 * [info] Benchmark           Mode  Samples   Score  Score error  Units
 * [info] m.InlineUnbox.no    avgt        3  41.763       18.118  us/op
 * [info] m.InlineUnbox.ya    avgt        3   0.001        0.000  us/op // JVM optimizes the while loop
 */

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class InlineUnbox {
  @Benchmark
  def no: Int = {
    val c = new C()
    var i = 0
    var r = 0
    while (i < InlineUnbox.N) {
      r += c.no(r)
      i += 1
    }
    r
  }

  @Benchmark
  def ya: Int = {
    val c = new C()
    var i = 0
    var r = 0
    while (i < InlineUnbox.N) {
      r += c.ya(r)
      i += 1
    }
    r
  }
}

object InlineUnbox {
  final val N = 10000
}

class C {
  @noinline final def no[T](x: T) = x
  @inline   final def ya[T](x: T) = x
}
