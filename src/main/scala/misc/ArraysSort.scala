package misc

import java.util.Comparator
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

/**
 * lucmac:benchmarks luc$ sbt clean 'run -f 1 -i 3 -wi 3 -t 1 ArraysSort'
 *
 *   [info] ArraysSort.singleType  avgt    3   82.159 ± 68.524  us/op
 *   [info] ArraysSort.twoTypes    avgt    3   83.040 ± 33.789  us/op
 *   [info] ArraysSort.threeTypes  avgt    3  245.992 ± 78.568  us/op
 *   [info] ArraysSort.fourTypes   avgt    3  270.961 ± 41.721  us/op
 *   [info] ArraysSort.sixTypes    avgt    3  260.092 ± 94.579  us/op
 *
 * NOTE: The arrays are already sorted. We cold also create / clone random arrays during the
 * benchmark, but then we'd measure also the time spent cloning the array.
 */

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class ArraysSort {
  import ArraysSort._

  val r = new util.Random(seed)

  val xs1: Array[Int] = (0 to N).toArray
  val xs2: Array[Int] = (0 to N).toArray
  val xs3: Array[Int] = (0 to N).toArray
  val xs4: Array[Int] = (0 to N).toArray
  val xs5: Array[Int] = (0 to N).toArray
  val xs6: Array[Int] = (0 to N).toArray

  val as1 = xs1.map(x => new A(x))
  val bs2 = xs2.map(x => new B(x))
  val cs3 = xs3.map(x => new C(x))
  val ds4 = xs4.map(x => new D(x))
  val es5 = xs5.map(x => new E(x))
  val fs6 = xs6.map(x => new F(x))

  val as2: Array[A] = xs2.map(x => new A(x))
  val as3: Array[A] = xs3.map(x => new A(x))
  val as4: Array[A] = xs4.map(x => new A(x))
  val as5: Array[A] = xs5.map(x => new A(x))
  val as6: Array[A] = xs6.map(x => new A(x))

  val bs4: Array[B] = xs4.map(x => new B(x))
  val bs5: Array[B] = xs5.map(x => new B(x))
  val bs6: Array[B] = xs6.map(x => new B(x))

  val cs5: Array[C] = xs5.map(x => new C(x))
  val cs6: Array[C] = xs6.map(x => new C(x))

  @Benchmark
  def singleType(): Unit = {
    java.util.Arrays.sort(as1, 0, N, A.comp)
    java.util.Arrays.sort(as2, 0, N, A.comp)
    java.util.Arrays.sort(as3, 0, N, A.comp)
    java.util.Arrays.sort(as4, 0, N, A.comp)
    java.util.Arrays.sort(as5, 0, N, A.comp)
    java.util.Arrays.sort(as6, 0, N, A.comp)
  }

  @Benchmark
  def twoTypes(): Unit = {
    java.util.Arrays.sort(as1, 0, N, A.comp)
    java.util.Arrays.sort(bs2, 0, N, B.comp)
    java.util.Arrays.sort(as3, 0, N, A.comp)
    java.util.Arrays.sort(bs4, 0, N, B.comp)
    java.util.Arrays.sort(as5, 0, N, A.comp)
    java.util.Arrays.sort(bs6, 0, N, B.comp)
  }

  @Benchmark
  def threeTypes(): Unit = {
    java.util.Arrays.sort(as1, 0, N, A.comp)
    java.util.Arrays.sort(bs2, 0, N, B.comp)
    java.util.Arrays.sort(cs3, 0, N, C.comp)
    java.util.Arrays.sort(as4, 0, N, A.comp)
    java.util.Arrays.sort(bs5, 0, N, B.comp)
    java.util.Arrays.sort(cs6, 0, N, C.comp)
  }

  @Benchmark
  def fourTypes(): Unit = {
    java.util.Arrays.sort(as1, 0, N, A.comp)
    java.util.Arrays.sort(bs2, 0, N, B.comp)
    java.util.Arrays.sort(cs3, 0, N, C.comp)
    java.util.Arrays.sort(ds4, 0, N, D.comp)
    java.util.Arrays.sort(as5, 0, N, A.comp)
    java.util.Arrays.sort(cs6, 0, N, C.comp)
  }

  @Benchmark
  def sixTypes(): Unit = {
    java.util.Arrays.sort(as1, 0, N, A.comp)
    java.util.Arrays.sort(bs2, 0, N, B.comp)
    java.util.Arrays.sort(cs3, 0, N, C.comp)
    java.util.Arrays.sort(ds4, 0, N, D.comp)
    java.util.Arrays.sort(es5, 0, N, E.comp)
    java.util.Arrays.sort(fs6, 0, N, F.comp)
  }
}

object ArraysSort {
  final val N = 10000

  val seed = 81783902

  class A(val x: Int)
  object A {
    val comp = new Comparator[A] {
      override def compare(o1: A, o2: A): Int = if (o1.x < o2.x) -1 else if (o1.x == o2.x) 0 else 1
    }
  }

  class B(val x: Int)
  object B {
    val comp = new Comparator[B] {
      override def compare(o1: B, o2: B): Int = if (o1.x < o2.x) -1 else if (o1.x == o2.x) 0 else 1
    }
  }

  class C(val x: Int)
  object C {
    val comp = new Comparator[C] {
      override def compare(o1: C, o2: C): Int = if (o1.x < o2.x) -1 else if (o1.x == o2.x) 0 else 1
    }
  }

  class D(val x: Int)
  object D {
    val comp = new Comparator[D] {
      override def compare(o1: D, o2: D): Int = if (o1.x < o2.x) -1 else if (o1.x == o2.x) 0 else 1
    }
  }

  class E(val x: Int)
  object E {
    val comp = new Comparator[E] {
      override def compare(o1: E, o2: E): Int = if (o1.x < o2.x) -1 else if (o1.x == o2.x) 0 else 1
    }
  }

  class F(val x: Int)
  object F {
    val comp = new Comparator[F] {
      override def compare(o1: F, o2: F): Int = if (o1.x < o2.x) -1 else if (o1.x == o2.x) 0 else 1
    }
  }
}
