// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = MAY_OUT_OF_BOUNDS

public class TestMultiplePrinters {
	public static void foo() {
		PrinterArray pa1 = new PrinterArray(5);
		PrinterArray pa2 = new PrinterArray(7);
		PrinterArray pa3 = pa2;
		PrinterArray pa4 = new PrinterArray(1);
		PrinterArray pa5 = pa3;
		int i = 2;
		pa2.sendJob(1);
		pa1.sendJob(2);
		pa2.sendJob(3);
		pa3.sendJob(4);
		pa1.sendJob(4);
		pa4.sendJob(5);
		pa5.sendJob(1);
		i++;
	}
}
