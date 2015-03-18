package misc

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

import scala.runtime.IntRef

/**
 * sbt clean 'run -f 1 -i 3 -wi 3 -t 1 LocalRef'
 *
 *
 * Observation: it seems that the closure creation is hampering escape analysis. so we have to get rid of the closure.
 *
 *
 * Benchmark                   Mode  Cnt   Score   Error  Units
 * LocalRef.forLoopInlined     avgt   10  12.643 ± 0.544  us/op
 * LocalRef.forLoopNonInlined  avgt   10  13.034 ± 0.535  us/op
 * LocalRef.whileLoopLocal     avgt   10   3.115 ± 0.047  us/op
 * LocalRef.whileLoopRef       avgt   10   4.048 ± 0.332  us/op
 */
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class LocalRef {
  @Benchmark
  def forLoopNonInlined: Int = {
    val loop = new ForNonInlined
    var r1 = 0
    var r2 = 0
    for (x <- loop) { r1 += x; r2 += r1 }
    r2
  }

  @Benchmark
  def forLoopInlined: Int = {
    val loop = new ForInlined
    var r1 = 0
    var r2 = 0
    for (x <- loop) { r1 += x; r2 += r1 }
    r2
  }

  @Benchmark
  def whileLoopRef: Int = {
    val r1 = new IntRef(0)
    val r2 = new IntRef(0)
    var x = 0
    while(x < 10000) {
      r1.elem += x; r2.elem += r1.elem
      x += 1
    }
    r2.elem
  }

  @Benchmark
  def whileLoopLocal: Int = {
    var r1 = 0
    var r2 = 0
    var x = 0
    while (x < 10000) {
      r1 += x; r2 += x
      x += 1
    }
    r2
  }
}

class ForNonInlined {
  @noinline def foreach(f: Int => Unit): Unit = {
    var x = 0
    while (x < 10000) {
      f(x)
      x += 1
    }
  }
}

class ForInlined {
  @inline final def foreach(f: Int => Unit): Unit = {
    var x = 0
    while (x < 10000) {
      f(x)
      x += 1
    }
  }
}

