// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

// MAY_DIV_BY_ZERO is due to non ==/!= 0 constraint
// in if/else branch
public class Test17Elias {
	public static void bar(int n) {
		if (n == 0) {
			int b = 0;
		} else {
			int b = 1 / n;
		}

	}
}
