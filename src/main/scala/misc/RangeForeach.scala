package misc

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

/**
 * sbt clean 'set scalacOptions in ThisBuild ++= Seq("-Yopt:l:classpath")' 'jmh:run -f 1 -i 10 -wi 10 -t 1 RangeForeach'
 *
 * With optimizer (-Yopt:l:classpath)
 *  [info] RangeForeach.rangeForeachMega             avgt   10  158.866 ± 12.186  us/op
 *  [info] RangeForeach.rangeForeachMono             avgt   10  156.360 ± 11.526  us/op
 *  [info] RangeForeach.whileLoop                    avgt   10  112.667 ±  7.990  us/op
 *  [info] RangeForeach.rangeForeachUpdateLocalMega  avgt   10   19.035 ±  0.948  us/op
 *  [info] RangeForeach.rangeForeachUpdateLocalMono  avgt   10   36.677 ± 57.162  us/op
 *  [info] RangeForeach.whileLoopUpdateLocal         avgt   10   18.940 ±  0.975  us/op
 *
 * Without optimizer
 *  [info] RangeForeach.rangeForeachMega             avgt   10  311.347 ±   8.342  us/op
 *  [info] RangeForeach.rangeForeachMono             avgt   10  180.269 ±  25.175  us/op
 *  [info] RangeForeach.whileLoop                    avgt   10  114.701 ±   5.921  us/op
 *  [info] RangeForeach.rangeForeachUpdateLocalMega  avgt   10  113.749 ± 178.329  us/op
 *  [info] RangeForeach.rangeForeachUpdateLocalMono  avgt   10   19.125 ±   0.906  us/op
 *  [info] RangeForeach.whileLoopUpdateLocal         avgt   10   18.712 ±   1.071  us/op
 *
 * With old optimizer (-Ybackend:GenASM -Ydelambdafy:method -optimize)
 *  [info] Benchmark                                 Mode  Cnt    Score    Error  Units
 *  [info] RangeForeach.rangeForeachMega             avgt   10  149.925 ±  5.106  us/op
 *  [info] RangeForeach.rangeForeachMono             avgt   10  148.168 ± 10.390  us/op
 *  [info] RangeForeach.whileLoop                    avgt   10  118.412 ±  7.281  us/op
 *  [info] RangeForeach.rangeForeachUpdateLocalMega  avgt   10   27.211 ± 40.388  us/op
 *  [info] RangeForeach.rangeForeachUpdateLocalMono  avgt   10   18.589 ±  1.310  us/op
 *  [info] RangeForeach.whileLoopUpdateLocal         avgt   10   19.023 ±  1.011  us/op
 */

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class RangeForeach {
  @Benchmark
  def rangeForeachMono(bh: Blackhole): Unit = {
    val f = (x: Int) => bh.consume(x)
    (1 to 10000).foreach(f)
    (1 to 10000).foreach(f)
    (1 to 10000).foreach(f)
    (1 to 10000).foreach(f)
    (1 to 10000).foreach(f)
    (1 to 10000).foreach(f)
  }

  @Benchmark
  def rangeForeachMega(bh: Blackhole): Unit = {
    val f1 = (x: Int) => bh.consume(x)
    val f2 = (x: Int) => bh.consume(x)
    val f3 = (x: Int) => bh.consume(x)
    val f4 = (x: Int) => bh.consume(x)
    val f5 = (x: Int) => bh.consume(x)
    val f6 = (x: Int) => bh.consume(x)
    (1 to 10000).foreach(f1)
    (1 to 10000).foreach(f2)
    (1 to 10000).foreach(f3)
    (1 to 10000).foreach(f4)
    (1 to 10000).foreach(f5)
    (1 to 10000).foreach(f6)
  }

  @Benchmark
  def whileLoop(bh: Blackhole): Unit = {
    var i = 0
    while (i < 10000) {
      bh.consume(i)
      i += 1
    }
    i = 0
    while (i < 10000) {
      bh.consume(i)
      i += 1
    }
    i = 0
    while (i < 10000) {
      bh.consume(i)
      i += 1
    }
    i = 0
    while (i < 10000) {
      bh.consume(i)
      i += 1
    }
    i = 0
    while (i < 10000) {
      bh.consume(i)
      i += 1
    }
    i = 0
    while (i < 10000) {
      bh.consume(i)
      i += 1
    }
  }

  @Benchmark
  def rangeForeachUpdateLocalMono: Int = {
    var x = 0
    val f = (i: Int) => x += i
    (1 to 10000).foreach(f)
    (1 to 10000).foreach(f)
    (1 to 10000).foreach(f)
    (1 to 10000).foreach(f)
    (1 to 10000).foreach(f)
    (1 to 10000).foreach(f)
    x
  }

  @Benchmark
  def rangeForeachUpdateLocalMega: Int = {
    var x = 0
    val f1 = (i: Int) => x += i
    val f2 = (i: Int) => x += i
    val f3 = (i: Int) => x += i
    val f4 = (i: Int) => x += i
    val f5 = (i: Int) => x += i
    val f6 = (i: Int) => x += i
    (1 to 10000).foreach(f1)
    (1 to 10000).foreach(f2)
    (1 to 10000).foreach(f3)
    (1 to 10000).foreach(f4)
    (1 to 10000).foreach(f5)
    (1 to 10000).foreach(f6)
    x
  }


  @Benchmark
  def whileLoopUpdateLocal: Int = {
    var x = 0
    var i = 0
    while (i < 10000) {
      x += i
      i += 1
    }
    i = 0
    while (i < 10000) {
      x += i
      i += 1
    }
    i = 0
    while (i < 10000) {
      x += i
      i += 1
    }
    i = 0
    while (i < 10000) {
      x += i
      i += 1
    }
    i = 0
    while (i < 10000) {
      x += i
      i += 1
    }
    i = 0
    while (i < 10000) {
      x += i
      i += 1
    }
    x
  }
}
