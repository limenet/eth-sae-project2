// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = MAY_OUT_OF_BOUNDS

public class TestSendJobNaN {
	public static void foo() {
		PrinterArray pa = new PrinterArray(10);
		pa.sendJob(1 / 0);
	}
}
