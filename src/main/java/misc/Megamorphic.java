package misc;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

/**
 * Example Run
 *
 * lucmac:benchmarks luc$ sbt clean 'run -f 1 -i 3 -wi 3 -t 1 Megamorphic'
 *
 * [info] Benchmark                    Mode  Samples    Score  Score error  Units
 * [info] m.Megamorphic.monoMorphic    avgt        3   18.619        5.639  us/op
 * [info] m.Megamorphic.biMorphic      avgt        3   19.759        6.056  us/op
 * [info] m.Megamorphic.quadMorphic    avgt        3  197.160      123.749  us/op  // some were fast, 20 us, some 130 us, most 200 us
 * [info] m.Megamorphic.hexaMorphic    avgt        3  189.815       10.789  us/op  // all were slow
 * [info] m.Megamorphic.hexaInlined    avgt        3   18.735       10.178  us/op  // fast
 */

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class Megamorphic {

    private static int N = 10000;

    interface Fun {
        int apply(int x);
    }

    class Coll {
        final int reduce(Fun f) {
            int r = 0;
            for (int i = 0; i < N; i++)
                r += f.apply(i);
            return r;
        }
    }

    Coll c = new Coll();

    @Benchmark
    public int monoMorphic() {
        int r = 0;
        Fun f = i -> i + 1;
        r += c.reduce(f);
        r += c.reduce(f);
        r += c.reduce(f);
        r += c.reduce(f);
        r += c.reduce(f);
        r += c.reduce(f);
        return r;
    }

    @Benchmark
    public int biMorphic() {
        int r = 0;
        Fun f1 = i -> i + 1;
        Fun f2 = i -> i + 1;
        r += c.reduce(f1);
        r += c.reduce(f2);
        r += c.reduce(f1);
        r += c.reduce(f2);
        r += c.reduce(f1);
        r += c.reduce(f2);
        return r;
    }

    @Benchmark
    public int quadMorphic() {
        int r = 0;
        Fun f1 = i -> i + 1;
        Fun f2 = i -> i + 1;
        Fun f3 = i -> i + 1;
        Fun f4 = i -> i + 1;
        r += c.reduce(f1);
        r += c.reduce(f2);
        r += c.reduce(f3);
        r += c.reduce(f4);
        r += c.reduce(f1);
        r += c.reduce(f3);
        return r;
    }

    @Benchmark
    public int hexaMorphic() {
        int r = 0;
        Fun f1 = i -> i + 1;
        Fun f2 = i -> i + 1;
        Fun f3 = i -> i + 1;
        Fun f4 = i -> i + 1;
        Fun f5 = i -> i + 1;
        Fun f6 = i -> i + 1;
        r += c.reduce(f1);
        r += c.reduce(f2);
        r += c.reduce(f3);
        r += c.reduce(f4);
        r += c.reduce(f5);
        r += c.reduce(f6);
        return r;
    }

    @Benchmark
    public int hexaInlined() {
        int r = 0;
        Fun f1 = i -> i + 1;
        Fun f2 = i -> i + 1;
        Fun f3 = i -> i + 1;
        Fun f4 = i -> i + 1;
        Fun f5 = i -> i + 1;
        Fun f6 = i -> i + 1;

        int r1 = 0;
        for (int i = 0; i < N; i++)
            r1 += f1.apply(i);
        r += r1;

        r1 = 0;
        for (int i = 0; i < N; i++)
            r1 += f2.apply(i);
        r += r1;

        r1 = 0;
        for (int i = 0; i < N; i++)
            r1 += f3.apply(i);
        r += r1;

        r1 = 0;
        for (int i = 0; i < N; i++)
            r1 += f4.apply(i);
        r += r1;

        r1 = 0;
        for (int i = 0; i < N; i++)
            r1 += f5.apply(i);
        r += r1;

        r1 = 0;
        for (int i = 0; i < N; i++)
            r1 += f6.apply(i);
        r += r1;

        return r;
    }
}
