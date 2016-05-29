// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = MAY_OUT_OF_BOUNDS

public class Test8Timon {
	public static void foo() {
		PrinterArray p1 = new PrinterArray(5);
		PrinterArray p2 = new PrinterArray(1);
		PrinterArray xx = p1;
		PrinterArray yy = p2;

		xx.sendJob(4);
		yy.sendJob(1); // yy == p2 and therefore 1 not element of [0]
	}
}
