// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

public class Test13 {
	public static void foo() {
		int n = 0;
		if (n == 0) {
			int b = 0;
		} else {
			int b = 1 / n;
		}

	}
}
