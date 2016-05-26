// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

public class TestPrinterInterleafing {
	public static void foo() {
		PrinterArray pa1 = new PrinterArray(1);
		pa1.sendJob(0);
		PrinterArray pa2 = new PrinterArray(2);
		pa1 = pa2;
		pa1.sendJob(1);
	}
}
