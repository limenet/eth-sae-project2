// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = MAY_OUT_OF_BOUNDS

public class Test13Timon {
	public static void foo(int i) {
		int x = 1;
		PrinterArray p = new PrinterArray(5);
		if (i > -3 && i < 3) {
			x = x / i;
		}

		p.sendJob(x);
	}
}
