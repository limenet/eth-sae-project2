// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

// Division and if-clause

public class TestApron {
	public static void foo(int n) {
		int a = -6;
		int b = a;
		a = b * 5;
		a = a * n;
		b = 5 / n;
		a = 2 - a;
		a = b;
		PrinterArray pa = new PrinterArray(10);
		if ((a >= 0) && (a < 10)) {
			pa.sendJob(a);
		}
	}
}