// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = MAY_OUT_OF_BOUNDS

// 2 methods, sendJob corner case, method doing nothing

public class Test7 {
	public static void foo() {
		PrinterArray pa = new PrinterArray(9999);
		pa.sendJob(9999);
	}

	public static void foo2() {
		// I do nothing
	}
}
