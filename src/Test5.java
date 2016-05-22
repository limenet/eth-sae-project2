// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

public class Test5 {
	public static void foo(int n) {
		int p = 0;
		if (n == 0) {
			p = n;
		}
		else {
			p = 1/n;
		}
	}

}
