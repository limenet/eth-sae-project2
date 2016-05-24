// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

// all possible conditional statements

public class Test10 {
	public static void foo(int n) {
		int p = 0;
		int q = 1;

		if (n == 0) {
			p = 1 + q;
		} else if (n >= 50) {
			p = 2 * q;
		} else if (n <= -50) {
			p = 3 - q;
		} else if (n > 40) {
			p = 4 / q;
		} else if (n < -40) {
			p = 5;
		} else if (n != 5) {
			p = 6;
		} else {
			q = 1 / p;
		}
	}
}
