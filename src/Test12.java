// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = MAY_OUT_OF_BOUNDS

public class Test12 {
	public static void foo(int n) {
		PrinterArray pa = new PrinterArray(20);
		int a = n / 10000;
		pa.sendJob(a);
	}
}
