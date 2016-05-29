// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = MAY_OUT_OF_BOUNDS

// this is a good one, what happens if you send a job with an argument divided by 0 ?
public class Test4Timon {
	public static void foo() {
		int c = 10;
		int n = 0;
		int x = c / n;
		PrinterArray pa = new PrinterArray(c);
		pa.sendJob(x);
	}
}
