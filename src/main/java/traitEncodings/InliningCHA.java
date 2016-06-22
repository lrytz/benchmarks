package traitEncodings;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * $ sbt clean 'jmh:run -f 1 -i 10 -wi 10 -t 1 InliningCHA'
 *
 * [info] Benchmark                                            Mode  Cnt   Score   Error  Units
 *
 * [info] traitEncodings.InliningCHA.a1inheritedSingleImpl     avgt   10  13.909 ± 0.008  ns/op
 * [info] traitEncodings.InliningCHA.a2inheritedMultipleImpls  avgt   10  60.620 ± 0.362  ns/op
 *
 * [info] traitEncodings.InliningCHA.b1defaultSingleImpl       avgt   10  60.808 ± 0.307  ns/op
 * [info] traitEncodings.InliningCHA.b2defaultMultipleImpls    avgt   10  60.498 ± 0.657  ns/op
 *
 */

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class InliningCHA {
    static abstract class CX extends CallPerformance.A {
        // introduce an override of `addInherited` so CHA finds multiple implementations
        public int addInherited(int a, int b) { return 0; }
    }

    static abstract class CY extends CallPerformance.A {
        // nothing changes for a default method because CHA is already not working
        public int addDefault(int a, int b) { return 0; }
    }

    CallPerformance.A[] cs = { new CallPerformance.C1(), new CallPerformance.C2(), new CallPerformance.C3(), new CallPerformance.C4() };

    @Benchmark
    public Class<? extends CallPerformance.A> a1inheritedSingleImpl() {
        int res = 0;
        for (int x = 0; x < 4; x++) { res = res + cs[x].addInherited(x, x); }
        if (res == 0)
            return CallPerformance.A.class;
        else
            return CallPerformance.C1.class;
    }

    @Benchmark
    public Class<? extends CallPerformance.A> a2inheritedMultipleImpls() {
        int res = 0;
        for (int x = 0; x < 4; x++) { res = res + cs[x].addInherited(x, x); }
        if (res == 0)
            return CallPerformance.A.class;
        else
            return CX.class;
    }

    @Benchmark
    public Class<? extends CallPerformance.A> b1defaultSingleImpl() {
        int res = 0;
        for (int x = 0; x < 4; x++) { res = res + cs[x].addDefault(x, x); }
        if (res == 0)
            return CallPerformance.A.class;
        else
            return CallPerformance.C1.class;
    }

    @Benchmark
    public Class<? extends CallPerformance.A> b2defaultMultipleImpls() {
        int res = 0;
        for (int x = 0; x < 4; x++) { res = res + cs[x].addDefault(x, x); }
        if (res == 0)
            return CallPerformance.A.class;
        else
            return CY.class;
    }

}
