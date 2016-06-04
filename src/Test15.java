// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = MAY_OUT_OF_BOUNDS

public class Test15 {
	public static void foo(PrinterArray pa) {
		// we can assume that pa is initialised with at least 1, therefore 0 is
		// in the range
		pa.sendJob(0);
	}
}
