// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = MAY_OUT_OF_BOUNDS

// pointer assignment

public class Test11 {
	public static void foo() {
		PrinterArray pa = new PrinterArray(20);
		PrinterArray pb;
		pb = pa;
		pb.sendJob(30);
	}
}
