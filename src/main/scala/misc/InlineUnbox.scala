package misc

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

/**
 * lucmac:benchmarks luc$ sbt clean 'set scalacOptions in ThisBuild ++= Seq("-optimize")' 'jmh:run -f 1 -i 3 -wi 3 -t 1 InlineUnbox'
 *
 *  [info] Benchmark       Mode  Cnt   Score    Error  Units
 *  [info] InlineUnbox.no  avgt    3  65.203 ± 17.968  us/op
 *  [info] InlineUnbox.ya  avgt    3  55.585 ±  6.405  us/op
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
  @noinline final def no[T](x: T) = { Blackhole.consumeCPU(4); x }
  @inline   final def ya[T](x: T) = { Blackhole.consumeCPU(4); x }
}
