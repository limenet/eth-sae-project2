// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = MAY_OUT_OF_BOUNDS

public class Test4 {
	public static void foo(int n) {
		int p = 0;
		PrinterArray pa = new PrinterArray(10);
		for(int i = -5; i<=5; i++) {
			p= 1/i;
		}
		pa.sendJob(n);
	}

}
