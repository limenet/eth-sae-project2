// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

public class Test2 {
    public static void foo(int n) {
        int a = 5/n;
        PrinterArray pa = new PrinterArray(10);
        if ((a >= 0) && (a < 10)) {
            pa.sendJob(a);
        }
    }
}
