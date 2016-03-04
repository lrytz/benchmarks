package misc

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

/**
 * sbt clean 'jmh:run -f 1 -i 10 -wi 20 -t 1 ArrayOps'
 *
 *   [info] ArrayOps.a_base                     avgt   10  1037.800 ±  6.961  us/op
 *   [info] ArrayOps.b_inlineMap                avgt   10   625.685 ± 12.074  us/op
 *   [info] ArrayOps.c_inlineMapF               avgt   10   610.372 ±  9.026  us/op
 *   [info] ArrayOps.d_inlineForeach            avgt   10   643.262 ± 10.787  us/op
 *   [info] ArrayOps.e_inlineForeachF           avgt   10   386.709 ±  5.208  us/op
 *   [info] ArrayOps.f_skipBuilderFactory       avgt   10   391.386 ±  7.182  us/op
 *   [info] ArrayOps.g_castToArrayBuilderOfInt  avgt   10   128.211 ±  2.833  us/op
 *   [info] ArrayOps.h_inlineMake               avgt   10    69.143 ±  2.951  us/op
 *   [info] ArrayOps.i_arrayBuilder             avgt   10    68.572 ±  2.234  us/op
 *   [info] ArrayOps.j_noSizeCheckBuilder       avgt   10    69.316 ±  2.799  us/op
 *   [info] ArrayOps.k_inlineArrayOps           avgt   10    76.369 ±  9.432  us/op
 *   [info] ArrayOps.l_artisanal                avgt   10    68.024 ±  2.103  us/op
 *
 */

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class ArrayOps {
  import ArrayOps._

  val xs: Array[Int] = (0 to N).toArray

  @Benchmark
  def a_base(bh: Blackhole): Unit = {
    val r = xs.map(x => x + 1)
    bh.consume(r)
  }

  @Benchmark
  def b_inlineMap(bh: Blackhole): Unit = {
    val thiz = new collection.mutable.ArrayOps.ofInt(xs)
    val bf = Array.canBuildFrom[Int]
    val b = bf.apply
    b.sizeHint(thiz.length)
    val map_f = (x: Int) => x + 1
    thiz.foreach(x => b += map_f(x))
    bh.consume(b.result())
  }

  @Benchmark
  def c_inlineMapF(bh: Blackhole): Unit = {
    val thiz = new collection.mutable.ArrayOps.ofInt(xs)
    val bf = Array.canBuildFrom[Int]
    val b = bf.apply
    b.sizeHint(thiz.length)
    thiz.foreach(x => b += x + 1)
    bh.consume(b.result())
  }

  @Benchmark
  def d_inlineForeach(bh: Blackhole): Unit = {
    val thiz = new collection.mutable.ArrayOps.ofInt(xs)
    val bf = Array.canBuildFrom[Int]
    val b = bf.apply
    b.sizeHint(thiz.length)
    val foreach_f = (x: Int) => b += x + 1
    var i = 0
    while (i < thiz.length) {
      foreach_f(thiz(i))
      i += 1
    }
    bh.consume(b.result())
  }

  @Benchmark
  def e_inlineForeachF(bh: Blackhole): Unit = {
    val thiz = new collection.mutable.ArrayOps.ofInt(xs)
    val bf = Array.canBuildFrom[Int]
    val b = bf.apply
    b.sizeHint(thiz.length)
    var i = 0
    while (i < thiz.length) {
      b += thiz(i) + 1
      i += 1
    }
    bh.consume(b.result())
  }

  @Benchmark
  def f_skipBuilderFactory(bh: Blackhole): Unit = {
    val thiz = new collection.mutable.ArrayOps.ofInt(xs)
    val b = collection.mutable.ArrayBuilder.make[Int]()
    b.sizeHint(thiz.length)
    var i = 0
    while (i < thiz.length) {
      b += thiz(i) + 1
      i += 1
    }
    bh.consume(b.result())
  }

  @Benchmark
  def g_castToArrayBuilderOfInt(bh: Blackhole): Unit = {
    val thiz = new collection.mutable.ArrayOps.ofInt(xs)
    // prevents boxing every Int in +=
    val b = collection.mutable.ArrayBuilder.make[Int]().asInstanceOf[collection.mutable.ArrayBuilder.ofInt]
    b.sizeHint(thiz.length)
    var i = 0
    while (i < thiz.length) {
      b += thiz(i) + 1
      i += 1
    }
    bh.consume(b.result())
  }

  @Benchmark
  def h_inlineMake(bh: Blackhole): Unit = {
    val thiz = new collection.mutable.ArrayOps.ofInt(xs)
    val tag = implicitly[reflect.ClassTag[Int]]
    val b = (tag.runtimeClass match {
      case Integer.TYPE => new collection.mutable.ArrayBuilder.ofInt().asInstanceOf[collection.mutable.ArrayBuilder[Int]]
    }).asInstanceOf[collection.mutable.ArrayBuilder.ofInt]
    b.sizeHint(thiz.length)
    var i = 0
    while (i < thiz.length) {
      b += thiz(i) + 1
      i += 1
    }
    bh.consume(b.result())
  }

  @Benchmark
  def i_arrayBuilder(bh: Blackhole): Unit = {
    val thiz = new collection.mutable.ArrayOps.ofInt(xs)
    val b = new collection.mutable.ArrayBuilder.ofInt
    b.sizeHint(thiz.length)
    var i = 0
    while (i < thiz.length) {
      b += thiz(i) + 1
      i += 1
    }
    bh.consume(b.result())
  }

  @Benchmark
  def j_noSizeCheckBuilder(bh: Blackhole): Unit = {
    val thiz = new collection.mutable.ArrayOps.ofInt(xs)
    val b = new NoSizeCheckBuilder
    b.sizeHint(thiz.length)
    var i = 0
    while (i < thiz.length) {
      b += thiz(i) + 1
      i += 1
    }
    bh.consume(b.result())
  }

  @Benchmark
  def k_inlineArrayOps(bh: Blackhole): Unit = {
    val b = new NoSizeCheckBuilder
    b.sizeHint(xs.length)
    var i = 0
    while (i < xs.length) {
      b += xs(i) + 1
      i += 1
    }
    bh.consume(b.result())
  }

  @Benchmark
  def l_artisanal(bh: Blackhole): Unit = {
    val r = new Array[Int](xs.length)
    var i = 0
    while (i < xs.length) {
      r(i) = xs(i) + 1
      i += 1
    }
    bh.consume(r)
  }
}

object ArrayOps {
  final val N = 100000
}

class NoSizeCheckBuilder {
  var b = new Array[Int](16)
  var i = 0
  def sizeHint(size: Int) = b = new Array[Int](size)
  def +=(n: Int) = {
    b(i) = n
    i += 1
  }
  def result() = b
}