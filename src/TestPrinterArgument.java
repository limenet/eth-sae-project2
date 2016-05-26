// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = MAY_OUT_OF_BOUNDS

public class TestPrinterArgument {
	public static void foo (int n) {
		PrinterArray pa1 = new PrinterArray(3);
		pa1.sendJob(n);
	}
}
