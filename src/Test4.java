// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = MAY_OUT_OF_BOUNDS

// for-loop and if-clause

public class Test4 {
	public static void foo(int n) {
		int p = 0;
		PrinterArray pa = new PrinterArray(10);
		for (int i = -1; i <= 5; i++) {
			p = 1 / i;
		}
		if (n >= 0) {
			pa.sendJob(n);
		}
	}

	public static void foo2(PrinterArray pa) {
		if (pa == null) {

		}
	}
}
