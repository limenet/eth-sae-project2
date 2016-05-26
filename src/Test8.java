// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

// Division inside sendJob

public class Test8 {
	public static void foo(int n) {
		PrinterArray pa = new PrinterArray(20);
		if (n >= 0) {
			// n >= 0 => 1/n <= 1 or div-by-zero
			pa.sendJob(1 / n);
		}
	}
}
