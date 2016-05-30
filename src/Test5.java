// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

// MAY_DIV_BY_ZERO is due to non ==/!= 0 constraint
// in if/else branch
// if-clause

public class Test5 {
	public static void foo(int n) {
		int p = 0;
		if (n == 0) {
			p = n;
		} else {
			p = 1 / n;
		}
	}
}
