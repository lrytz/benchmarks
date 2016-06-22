package traitEncodings;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * $ sbt clean 'jmh:run -f 1 -i 10 -wi 10 -t 1 InterfaceManyMembers'
 *
 * Basic example:
 *   trait I { default int add(int a, int b) { return a + b; } }
 *   class C implements I { }
 *
 * Note that we test the megamorphic case, so there's four subclasses like `C` and we make all of
 * them appear at the callsite of `add`.
 *
 * [info] Benchmark                             Mode  Cnt   Score   Error  Units
 *
 * // number of methods in an interface doesn't matter if a class only extends one interface
 * [info] InterfaceManyMembers.a1       avgt   10  19.282 ± 0.495  ns/op
 * [info] InterfaceManyMembers.a2       avgt   10  19.685 ± 0.544  ns/op
 * [info] InterfaceManyMembers.a4       avgt   10  19.099 ± 0.329  ns/op
 * [info] InterfaceManyMembers.a8       avgt   10  19.167 ± 0.510  ns/op
 * [info] InterfaceManyMembers.a16      avgt   10  19.223 ± 0.479  ns/op
 * [info] InterfaceManyMembers.a32      avgt   10  19.586 ± 0.907  ns/op
 *
 * // number of interface parents matters, slowdown of 3ns per additional interface.
 * // the slowdown always 3ns, no matter how many methods the additional trait has.
 * [info] InterfaceManyMembers.b1       avgt   10  19.319 ± 0.428  ns/op
 * [info] InterfaceManyMembers.b2       avgt   10  21.873 ± 0.436  ns/op
 * [info] InterfaceManyMembers.b4       avgt   10  24.415 ± 0.481  ns/op
 * [info] InterfaceManyMembers.b8       avgt   10  27.807 ± 1.740  ns/op
 * [info] InterfaceManyMembers.b16      avgt   10  29.033 ± 0.727  ns/op
 * [info] InterfaceManyMembers.b32      avgt   10  32.278 ± 0.817  ns/op
 *
 * // adding a forwarder to subclasses does not seem to change anything. note that the callsite
 * // still uses `invokeinterface`, the static receiver type is the interface.
 * [info] InterfaceManyMembers.c1       avgt   10  19.032 ± 1.252  ns/op
 * [info] InterfaceManyMembers.c2       avgt   10  21.208 ± 0.418  ns/op
 * [info] InterfaceManyMembers.c4       avgt   10  25.269 ± 2.064  ns/op
 * [info] InterfaceManyMembers.c8       avgt   10  26.626 ± 1.209  ns/op
 * [info] InterfaceManyMembers.c16      avgt   10  30.275 ± 3.252  ns/op
 * [info] InterfaceManyMembers.c32      avgt   10  31.232 ± 1.113  ns/op
 *
 *
 * // monomorphic calls to default methods seem to be optimized (inline caching?). here we test
 * // performance of a (non-inlined) call to a default method. the performance does not depend on
 * // the number of interfaces that the class implements, or number of methods in those interfaces.
 * // note that the invocation is an `invokeinterface` here (compare with next).
 * [info] InterfaceManyMembers.d1intf   avgt   10   3.953 ± 0.224  ns/op
 * [info] InterfaceManyMembers.d2intf   avgt   10   3.955 ± 0.189  ns/op
 * [info] InterfaceManyMembers.d4intf   avgt   10   3.997 ± 0.207  ns/op
 * [info] InterfaceManyMembers.d8intf   avgt   10   3.928 ± 0.334  ns/op
 * [info] InterfaceManyMembers.d16intf  avgt   10   3.843 ± 0.256  ns/op
 * [info] InterfaceManyMembers.d32intf  avgt   10   3.951 ± 0.170  ns/op
 *
 * // introducing a forwarder method (that the JVM inlines) to a default method does not change
 * // anything (compared to the previous case). note that the interface method is invoked with
 * // `invokespecial` here (super call).
 * [info] InterfaceManyMembers.d1virt   avgt   10   3.970 ± 0.249  ns/op
 * [info] InterfaceManyMembers.d2virt   avgt   10   4.048 ± 0.210  ns/op
 * [info] InterfaceManyMembers.d8virt   avgt   10   4.099 ± 0.169  ns/op
 * [info] InterfaceManyMembers.d4virt   avgt   10   4.013 ± 0.210  ns/op
 * [info] InterfaceManyMembers.d16virt  avgt   10   3.986 ± 0.217  ns/op
 * [info] InterfaceManyMembers.d32virt  avgt   10   3.991 ± 0.156  ns/op
 *
 */

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class InterfaceManyMembers {
    interface I1 {
        default int a1(int a, int b) { return a + b; }

        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        default int notInlinedA(int a, int b) { return a + b; }
    }

    interface I2 {
        default int b1(int a, int b) { return a + b; }
        default int b2(int a, int b) { return a + b; }

        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        default int notInlinedB(int a, int b) { return a + b; }
    }

    interface I4 {
        default int c1(int a, int b) { return a + b; }
        default int c2(int a, int b) { return a + b; }
        default int c3(int a, int b) { return a + b; }
        default int c4(int a, int b) { return a + b; }

        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        default int notInlinedC(int a, int b) { return a + b; }
    }

    interface I8 {
        default int d1(int a, int b) { return a + b; }
        default int d2(int a, int b) { return a + b; }
        default int d3(int a, int b) { return a + b; }
        default int d4(int a, int b) { return a + b; }
        default int d5(int a, int b) { return a + b; }
        default int d6(int a, int b) { return a + b; }
        default int d7(int a, int b) { return a + b; }
        default int d8(int a, int b) { return a + b; }

        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        default int notInlinedD(int a, int b) { return a + b; }
    }

    interface I16 {
        default int e1(int a, int b) { return a + b; }
        default int e2(int a, int b) { return a + b; }
        default int e3(int a, int b) { return a + b; }
        default int e4(int a, int b) { return a + b; }
        default int e5(int a, int b) { return a + b; }
        default int e6(int a, int b) { return a + b; }
        default int e7(int a, int b) { return a + b; }
        default int e8(int a, int b) { return a + b; }
        default int e9(int a, int b) { return a + b; }
        default int e10(int a, int b) { return a + b; }
        default int e11(int a, int b) { return a + b; }
        default int e12(int a, int b) { return a + b; }
        default int e13(int a, int b) { return a + b; }
        default int e14(int a, int b) { return a + b; }
        default int e15(int a, int b) { return a + b; }
        default int e16(int a, int b) { return a + b; }

        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        default int notInlinedE(int a, int b) { return a + b; }
    }

    interface I32 {
        default int f1(int a, int b) { return a + b; }
        default int f2(int a, int b) { return a + b; }
        default int f3(int a, int b) { return a + b; }
        default int f4(int a, int b) { return a + b; }
        default int f5(int a, int b) { return a + b; }
        default int f6(int a, int b) { return a + b; }
        default int f7(int a, int b) { return a + b; }
        default int f8(int a, int b) { return a + b; }
        default int f9(int a, int b) { return a + b; }
        default int f10(int a, int b) { return a + b; }
        default int f11(int a, int b) { return a + b; }
        default int f12(int a, int b) { return a + b; }
        default int f13(int a, int b) { return a + b; }
        default int f14(int a, int b) { return a + b; }
        default int f15(int a, int b) { return a + b; }
        default int f16(int a, int b) { return a + b; }
        default int f17(int a, int b) { return a + b; }
        default int f18(int a, int b) { return a + b; }
        default int f19(int a, int b) { return a + b; }
        default int f20(int a, int b) { return a + b; }
        default int f21(int a, int b) { return a + b; }
        default int f22(int a, int b) { return a + b; }
        default int f23(int a, int b) { return a + b; }
        default int f24(int a, int b) { return a + b; }
        default int f25(int a, int b) { return a + b; }
        default int f26(int a, int b) { return a + b; }
        default int f27(int a, int b) { return a + b; }
        default int f28(int a, int b) { return a + b; }
        default int f29(int a, int b) { return a + b; }
        default int f30(int a, int b) { return a + b; }
        default int f31(int a, int b) { return a + b; }
        default int f32(int a, int b) { return a + b; }

        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        default int notInlinedF(int a, int b) { return a + b; }
    }

    class A1a implements I1 { }
    class A1b implements I1 { }
    class A1c implements I1 { }
    class A1d implements I1 { }

    class A2a implements I2 { }
    class A2b implements I2 { }
    class A2c implements I2 { }
    class A2d implements I2 { }

    class A4a implements I4 { }
    class A4b implements I4 { }
    class A4c implements I4 { }
    class A4d implements I4 { }

    class A8a implements I8 { }
    class A8b implements I8 { }
    class A8c implements I8 { }
    class A8d implements I8 { }

    class A16a implements I16 { }
    class A16b implements I16 { }
    class A16c implements I16 { }
    class A16d implements I16 { }

    class A32a implements I32 { }
    class A32b implements I32 { }
    class A32c implements I32 { }
    class A32d implements I32 { }

    I1[] as1 = { new A1a(), new A1b(), new A1c(), new A1d() };
    I2[] as2 = { new A2a(), new A2b(), new A2c(), new A2d() };
    I4[] as4 = { new A4a(), new A4b(), new A4c(), new A4d() };
    I8[] as8 = { new A8a(), new A8b(), new A8c(), new A8d() };
    I16[] as16 = { new A16a(), new A16b(), new A16c(), new A16d() };
    I32[] as32 = { new A32a(), new A32b(), new A32c(), new A32d() };

    @Benchmark
    public int a1() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + as1[i].a1(i, i);
        }
        return res;
    }

    @Benchmark
    public int a2() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + as2[i].b2(i, i);
        }
        return res;
    }

    @Benchmark
    public int a4() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + as4[i].c4(i, i);
        }
        return res;
    }

    @Benchmark
    public int a8() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + as8[i].d8(i, i);
        }
        return res;
    }

    @Benchmark
    public int a16() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + as16[i].e16(i, i);
        }
        return res;
    }

    @Benchmark
    public int a32() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + as32[i].f32(i, i);
        }
        return res;
    }

    class B1a implements I1 { }
    class B1b implements I1 { }
    class B1c implements I1 { }
    class B1d implements I1 { }

    class B2a implements I1, I2 { }
    class B2b implements I1, I2 { }
    class B2c implements I1, I2 { }
    class B2d implements I1, I2 { }

    class B4a implements I1, I2, I4 { }
    class B4b implements I1, I2, I4 { }
    class B4c implements I1, I2, I4 { }
    class B4d implements I1, I2, I4 { }

    class B8a implements I1, I2, I4, I8 { }
    class B8b implements I1, I2, I4, I8 { }
    class B8c implements I1, I2, I4, I8 { }
    class B8d implements I1, I2, I4, I8 { }

    class B16a implements I1, I2, I4, I8, I16 { }
    class B16b implements I1, I2, I4, I8, I16 { }
    class B16c implements I1, I2, I4, I8, I16 { }
    class B16d implements I1, I2, I4, I8, I16 { }

    class B32a implements I1, I2, I4, I8, I16, I32 { }
    class B32b implements I1, I2, I4, I8, I16, I32 { }
    class B32c implements I1, I2, I4, I8, I16, I32 { }
    class B32d implements I1, I2, I4, I8, I16, I32 { }

    I1[] bs1 = { new B1a(), new B1b(), new B1c(), new B1d() };
    I2[] bs2 = { new B2a(), new B2b(), new B2c(), new B2d() };
    I4[] bs4 = { new B4a(), new B4b(), new B4c(), new B4d() };
    I8[] bs8 = { new B8a(), new B8b(), new B8c(), new B8d() };
    I16[] bs16 = { new B16a(), new B16b(), new B16c(), new B16d() };
    I32[] bs32 = { new B32a(), new B32b(), new B32c(), new B32d() };

    @Benchmark
    public int b1() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + bs1[i].a1(i, i);
        }
        return res;
    }

    @Benchmark
    public int b2() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + bs2[i].b2(i, i);
        }
        return res;
    }

    @Benchmark
    public int b4() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + bs4[i].c4(i, i);
        }
        return res;
    }

    @Benchmark
    public int b8() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + bs8[i].d8(i, i);
        }
        return res;
    }

    @Benchmark
    public int b16() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + bs16[i].e16(i, i);
        }
        return res;
    }

    @Benchmark
    public int b32() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + bs32[i].f32(i, i);
        }
        return res;
    }

    class C1a implements I1 { public int a1(int a, int b) { return I1.super.a1(a, b); } }
    class C1b implements I1 { public int a1(int a, int b) { return I1.super.a1(a, b); } }
    class C1c implements I1 { public int a1(int a, int b) { return I1.super.a1(a, b); } }
    class C1d implements I1 { public int a1(int a, int b) { return I1.super.a1(a, b); } }

    class C2a implements I1, I2 { public int b2(int a, int b) { return I2.super.b2(a, b); } }
    class C2b implements I1, I2 { public int b2(int a, int b) { return I2.super.b2(a, b); } }
    class C2c implements I1, I2 { public int b2(int a, int b) { return I2.super.b2(a, b); } }
    class C2d implements I1, I2 { public int b2(int a, int b) { return I2.super.b2(a, b); } }

    class C4a implements I1, I2, I4 { public int c4(int a, int b) { return I4.super.c4(a, b); } }
    class C4b implements I1, I2, I4 { public int c4(int a, int b) { return I4.super.c4(a, b); } }
    class C4c implements I1, I2, I4 { public int c4(int a, int b) { return I4.super.c4(a, b); } }
    class C4d implements I1, I2, I4 { public int c4(int a, int b) { return I4.super.c4(a, b); } }

    class C8a implements I1, I2, I4, I8 { public int d8(int a, int b) { return I8.super.d8(a, b); } }
    class C8b implements I1, I2, I4, I8 { public int d8(int a, int b) { return I8.super.d8(a, b); } }
    class C8c implements I1, I2, I4, I8 { public int d8(int a, int b) { return I8.super.d8(a, b); } }
    class C8d implements I1, I2, I4, I8 { public int d8(int a, int b) { return I8.super.d8(a, b); } }

    class C16a implements I1, I2, I4, I8, I16 { public int e16(int a, int b) { return I16.super.e16(a, b); } }
    class C16b implements I1, I2, I4, I8, I16 { public int e16(int a, int b) { return I16.super.e16(a, b); } }
    class C16c implements I1, I2, I4, I8, I16 { public int e16(int a, int b) { return I16.super.e16(a, b); } }
    class C16d implements I1, I2, I4, I8, I16 { public int e16(int a, int b) { return I16.super.e16(a, b); } }

    class C32a implements I1, I2, I4, I8, I16, I32 { public int f32(int a, int b) { return I32.super.f32(a, b); } }
    class C32b implements I1, I2, I4, I8, I16, I32 { public int f32(int a, int b) { return I32.super.f32(a, b); } }
    class C32c implements I1, I2, I4, I8, I16, I32 { public int f32(int a, int b) { return I32.super.f32(a, b); } }
    class C32d implements I1, I2, I4, I8, I16, I32 { public int f32(int a, int b) { return I32.super.f32(a, b); } }

    I1[] cs1 = { new C1a(), new C1b(), new C1c(), new C1d() };
    I2[] cs2 = { new C2a(), new C2b(), new C2c(), new C2d() };
    I4[] cs4 = { new C4a(), new C4b(), new C4c(), new C4d() };
    I8[] cs8 = { new C8a(), new C8b(), new C8c(), new C8d() };
    I16[] cs16 = { new C16a(), new C16b(), new C16c(), new C16d() };
    I32[] cs32 = { new C32a(), new C32b(), new C32c(), new C32d() };

    @Benchmark
    public int c1() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + cs1[i].a1(i, i);
        }
        return res;
    }

    @Benchmark
    public int c2() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + cs2[i].b2(i, i);
        }
        return res;
    }

    @Benchmark
    public int c4() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + cs4[i].c4(i, i);
        }
        return res;
    }

    @Benchmark
    public int c8() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + cs8[i].d8(i, i);
        }
        return res;
    }

    @Benchmark
    public int c16() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + cs16[i].e16(i, i);
        }
        return res;
    }

    @Benchmark
    public int c32() {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res = res + cs32[i].f32(i, i);
        }
        return res;
    }

    class D1a implements I1 { }
    class D1b implements I1 {
        public int notInlinedA(int a, int b) { return I1.super.notInlinedA(a, b); }
    }

    class D2a implements I1, I2 { }
    class D2b implements I1, I2 {
        public int notInlinedB(int a, int b) { return I2.super.notInlinedB(a, b); }
    }

    class D4a implements I1, I2, I4 { }
    class D4b implements I1, I2, I4 {
        public int notInlinedC(int a, int b) { return I4.super.notInlinedC(a, b); }
    }

    class D8a implements I1, I2, I4, I8 { }
    class D8b implements I1, I2, I4, I8 {
        public int notInlinedD(int a, int b) { return I8.super.notInlinedD(a, b); }
    }

    class D16a implements I1, I2, I4, I8, I16 { }
    class D16b implements I1, I2, I4, I8, I16 {
        public int notInlinedE(int a, int b) { return I16.super.notInlinedE(a, b); }
    }

    class D32a implements I1, I2, I4, I8, I16, I32 { }
    class D32b implements I1, I2, I4, I8, I16, I32 {
        public int notInlinedF(int a, int b) { return I32.super.notInlinedF(a, b); }
    }


    D1a d1a = new D1a();
    D1b d1b = new D1b();
    D2a d2a = new D2a();
    D2b d2b = new D2b();
    D4a d4a = new D4a();
    D4b d4b = new D4b();
    D8a d8a = new D8a();
    D8b d8b = new D8b();
    D16a d16a = new D16a();
    D16b d16b = new D16b();
    D32a d32a = new D32a();
    D32b d32b = new D32b();

    @Benchmark
    public int d1intf() { return d1a.notInlinedA(1, 2); }

    @Benchmark
    public int d1virt() { return d1b.notInlinedA(1, 2); }

    @Benchmark
    public int d2intf() { return d2a.notInlinedB(1, 2); }

    @Benchmark
    public int d2virt() { return d2b.notInlinedB(1, 2); }

    @Benchmark
    public int d4intf() { return d4a.notInlinedC(1, 2); }

    @Benchmark
    public int d4virt() { return d4b.notInlinedC(1, 2); }

    @Benchmark
    public int d8intf() { return d8a.notInlinedD(1, 2); }

    @Benchmark
    public int d8virt() { return d8b.notInlinedD(1, 2); }

    @Benchmark
    public int d16intf() { return d16a.notInlinedE(1, 2); }

    @Benchmark
    public int d16virt() { return d16b.notInlinedE(1, 2); }

    @Benchmark
    public int d32intf() { return d32a.notInlinedF(1, 2); }

    @Benchmark
    public int d32virt() { return d32b.notInlinedF(1, 2); }
}
