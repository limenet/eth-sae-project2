// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

public class TestMultiplePrinters {
	public static void foo() {
		PrinterArray pa1 = new PrinterArray(5);
		PrinterArray pa2 = new PrinterArray(7);
		pa2.sendJob(1);
		pa1.sendJob(2);
		pa2.sendJob(3);
	}
}
