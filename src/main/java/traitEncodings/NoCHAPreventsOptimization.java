package traitEncodings;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Example is a simplified version of
 * http://stackoverflow.com/questions/30312096/java-default-methods-is-slower-than-the-same-code-but-in-an-abstract-class
 *
 * $ sbt 'jmh:run -f 1 -i 10 -wi 10 -t 1 NoCHAPreventsOptimization'
 *
 * [info] Benchmark                           Mode  Cnt     Score   Error  Units
 *
 * [info] NoCHAPreventsOptimization.aDefault  avgt   10  1586.156 ± 0.626  ns/op
 * [info] NoCHAPreventsOptimization.bVirtual  avgt   10   544.026 ± 0.356  ns/op
 * [info] NoCHAPreventsOptimization.cForward  avgt   10   543.695 ± 0.296  ns/op
 *
 */

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class NoCHAPreventsOptimization {
    interface I {
        int getV();
        default int accessDefault() { return getV(); }
    }

    abstract class A implements I {
        public int accessVirtual() { return getV(); }
        public int accessForward() { return I.super.accessDefault(); }
    }

    class C extends A implements I {
        public int v = 0;
        public int getV() { return v; }
    }

    final int NB = 1000;
    public C c1 = new C();
    public C c2 = new C();
    public C c3 = new C();

    @Benchmark
    public int aDefault() {
        int value = 0;
        for (int i = 0; i < NB; i++) {
            c1.v = i;
            value += c1.accessDefault();
        }
        return value;
    }

    @Benchmark
    public int bVirtual() {
        int value = 0;
        for (int i = 0; i < NB; i++) {
            c2.v = i;
            value += c2.accessVirtual();
        }
        return value;
    }

    @Benchmark
    public int cForward() {
        int value = 0;
        for (int i = 0; i < NB; i++) {
            c3.v = i;
            value += c3.accessForward();
        }
        return value;
    }
}
