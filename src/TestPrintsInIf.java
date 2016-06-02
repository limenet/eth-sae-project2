// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

public class TestPrintsInIf {
	public static void foo(int n) {
		if (n >= 0 && n <= 8) {
			PrinterArray pa = new PrinterArray(9);
			pa.sendJob(n);
		}

	}
}
