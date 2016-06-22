package traitEncodings;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * $ sbt clean 'jmh:run -f 1 -i 10 -wi 10 -t 1 CallPerformance'
 *
 * [info] Benchmark                                            Mode  Cnt    Score   Error  Units
 *
 * [info] traitEncodings.CallPerformance.a1monoInherited       avgt   10   11.128 ± 0.014  ns/op
 * [info] traitEncodings.CallPerformance.a2monoDefault         avgt   10   12.313 ± 0.103  ns/op
 * [info] traitEncodings.CallPerformance.a3monoDefaultStatic   avgt   10   12.289 ± 0.003  ns/op
 * [info] traitEncodings.CallPerformance.a4monoVirtual         avgt   10   12.284 ± 0.002  ns/op
 * [info] traitEncodings.CallPerformance.a5monoForwarded       avgt   10   12.356 ± 0.189  ns/op
 *
 * [info] traitEncodings.CallPerformance.b1monoInheritedN      avgt   10  101.943 ± 2.137  ns/op
 * [info] traitEncodings.CallPerformance.b2monoDefaultN        avgt   10  104.064 ± 5.089  ns/op
 * [info] traitEncodings.CallPerformance.b3monoDefaultStaticN  avgt   10  134.208 ± 1.670  ns/op
 * [info] traitEncodings.CallPerformance.b4monoVirtualN        avgt   10  104.280 ± 4.798  ns/op
 * [info] traitEncodings.CallPerformance.b5monoForwardedN      avgt   10  128.990 ± 0.074  ns/op
 *
 * [info] traitEncodings.CallPerformance.c1polyInherited       avgt   10   15.314 ± 0.003  ns/op
 * [info] traitEncodings.CallPerformance.c2polyDefault         avgt   10   63.912 ± 0.576  ns/op
 * [info] traitEncodings.CallPerformance.c3polyDefaultStatic   avgt   10   59.658 ± 0.344  ns/op
 * [info] traitEncodings.CallPerformance.c4polyVirtual         avgt   10   73.210 ± 0.114  ns/op
 * [info] traitEncodings.CallPerformance.c5polyForwarded       avgt   10   68.643 ± 0.652  ns/op
 *
 * [info] traitEncodings.CallPerformance.d1polyInheritedN      avgt   10   50.751 ± 0.041  ns/op
 * [info] traitEncodings.CallPerformance.d2polyDefaultN        avgt   10   63.036 ± 0.482  ns/op
 * [info] traitEncodings.CallPerformance.d3polyDefaultStaticN  avgt   10   77.494 ± 0.900  ns/op
 * [info] traitEncodings.CallPerformance.d4polyVirtualN        avgt   10   73.246 ± 0.123  ns/op
 * [info] traitEncodings.CallPerformance.d5polyForwardedN      avgt   10   80.009 ± 0.989  ns/op
 *
 */

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class CallPerformance {
    interface I {
        // body in default method
        default int addDefault(int a, int b) { return a + b; }
        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        default int addDefaultN(int a, int b) { return a + b; }

        // body in static method
        static int addStatic(int a, int b) { return a + b; }
        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        static int addStaticN(int a, int b) { return a + b; }

        // default method forwarding to static method
        default int addDefaultStatic(int a, int b) { return addStatic(a, b); }
        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        default int addDefaultStaticN(int a, int b) { return addStaticN(a, b); }

        // body in default method, forwarder generated in subclasses
        default int addForwarded(int a, int b) { return a + b; }
        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        default int addForwardedN(int a, int b) { return a + b; }

        // virtual method, one implementation in superclass
        int addInherited(int a, int b);
        int addInheritedN(int a, int b);

        // virtual method, many implementations
        int addVirtual(int a, int b);
        int addVirtualN(int a, int b);
    }

    static abstract class A implements I {
        public int addInherited(int a, int b) { return a + b; }
        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        public int addInheritedN(int a, int b) { return a + b; }
    }

    static class C1 extends A implements I {
        public int addForwarded(int a, int b) { return I.super.addForwarded(a, b); }
        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        public int addForwardedN(int a, int b) { return I.super.addForwardedN(a, b); }

        public int addVirtual(int a, int b) { return a + b; }
        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        public int addVirtualN(int a, int b) { return a + b; }
    }

    static class C2 extends A implements I {
        public int addForwarded(int a, int b) { return I.super.addForwarded(a, b); }
        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        public int addForwardedN(int a, int b) { return I.super.addForwardedN(a, b); }

        public int addVirtual(int a, int b) { return a + b; }
        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        public int addVirtualN(int a, int b) { return a + b; }
    }

    static class C3 extends A implements I {
        public int addForwarded(int a, int b) { return I.super.addForwarded(a, b); }
        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        public int addForwardedN(int a, int b) { return I.super.addForwardedN(a, b); }

        public int addVirtual(int a, int b) { return a + b; }
        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        public int addVirtualN(int a, int b) { return a + b; }
    }

    static class C4 extends A implements I {
        public int addForwarded(int a, int b) { return I.super.addForwarded(a, b); }
        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        public int addForwardedN(int a, int b) { return I.super.addForwardedN(a, b); }

        public int addVirtual(int a, int b) { return a + b; }
        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        public int addVirtualN(int a, int b) { return a + b; }
    }

    A[] cs = { new C1(), new C2(), new C3(), new C4() };


    // monomorphic callsite, inlining enabled

    @Benchmark
    public int a1monoInherited() {
        int res = 0;
        for (int x = 0; x < 8; x++) { res = res + cs[0].addInherited(x, x); }
        return res;
    }
    @Benchmark
    public int a2monoDefault() {
        int res = 0;
        for (int x = 0; x < 8; x++) { res = res + cs[0].addDefault(x, x); }
        return res;
    }
    @Benchmark
    public int a3monoDefaultStatic() {
        int res = 0;
        for (int x = 0; x < 8; x++) { res = res + cs[0].addDefaultStatic(x, x); }
        return res;
    }
    @Benchmark
    public int a4monoVirtual() {
        int res = 0;
        for (int x = 0; x < 8; x++) { res = res + cs[0].addVirtual(x, x); }
        return res;
    }
    @Benchmark
    public int a5monoForwarded() {
        int res = 0;
        for (int x = 0; x < 8; x++) { res = res + cs[0].addForwarded(x, x); }
        return res;
    }


    // monomorphic callsite, inlining disabled

    @Benchmark
    public int b1monoInheritedN() {
        int res = 0;
        for (int x = 0; x < 8; x++) { res = res + cs[0].addInheritedN(x, x); }
        return res;
    }
    @Benchmark
    public int b2monoDefaultN() {
        int res = 0;
        for (int x = 0; x < 8; x++) { res = res + cs[0].addDefaultN(x, x); }
        return res;
    }
    @Benchmark
    public int b3monoDefaultStaticN() {
        int res = 0;
        for (int x = 0; x < 8; x++) { res = res + cs[0].addDefaultStaticN(x, x); }
        return res;
    }
    @Benchmark
    public int b4monoVirtualN() {
        int res = 0;
        for (int x = 0; x < 8; x++) { res = res + cs[0].addVirtualN(x, x); }
        return res;
    }
    @Benchmark
    public int b5monoForwardedN() {
        int res = 0;
        for (int x = 0; x < 8; x++) { res = res + cs[0].addForwardedN(x, x); }
        return res;
    }


    // polymorphic callsite, inlining enabled

    @Benchmark
    public int c1polyInherited() {
        int res = 0;
        for (int x = 0; x < 4; x++) { res = res + cs[x].addInherited(x, x); }
        return res;
    }
    @Benchmark
    public int c2polyDefault() {
        int res = 0;
        for (int x = 0; x < 4; x++) { res = res + cs[x].addDefault(x, x); }
        return res;
    }
    @Benchmark
    public int c3polyDefaultStatic() {
        int res = 0;
        for (int x = 0; x < 4; x++) { res = res + cs[x].addDefaultStatic(x, x); }
        return res;
    }
    @Benchmark
    public int c4polyVirtual() {
        int res = 0;
        for (int x = 0; x < 4; x++) { res = res + cs[x].addVirtual(x, x); }
        return res;
    }
    @Benchmark
    public int c5polyForwarded() {
        int res = 0;
        for (int x = 0; x < 4; x++) { res = res + cs[x].addForwarded(x, x); }
        return res;
    }


    // polymorphic callsite, inlining disabled

    @Benchmark
    public int d1polyInheritedN() {
        int res = 0;
        for (int x = 0; x < 4; x++) { res = res + cs[x].addInheritedN(x, x); }
        return res;
    }
    @Benchmark
    public int d2polyDefaultN() {
        int res = 0;
        for (int x = 0; x < 4; x++) { res = res + cs[x].addDefaultN(x, x); }
        return res;
    }
    @Benchmark
    public int d3polyDefaultStaticN() {
        int res = 0;
        for (int x = 0; x < 4; x++) { res = res + cs[x].addDefaultStaticN(x, x); }
        return res;
    }
    @Benchmark
    public int d4polyVirtualN() {
        int res = 0;
        for (int x = 0; x < 4; x++) { res = res + cs[x].addVirtualN(x, x); }
        return res;
    }
    @Benchmark
    public int d5polyForwardedN() {
        int res = 0;
        for (int x = 0; x < 4; x++) { res = res + cs[x].addForwardedN(x, x); }
        return res;
    }
}
