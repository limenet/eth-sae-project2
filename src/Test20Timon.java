// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

public class Test20Timon {
	public static void foo(PrinterArray pa) {
		int b = 1;
		for (int j = 2; j < 10; j++) {
			int f = 1 / j;
			if (j == 1) {
				b = 10;
			}
		}
		pa = new PrinterArray(5);
		pa.sendJob(b);

	}
}
