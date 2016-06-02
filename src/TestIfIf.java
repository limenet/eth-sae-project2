// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

public class TestIfIf {
	public static void foo(int n) {
		if (n >= 0) {
			if (n != 0) {
				int a = 1 / n; // should be recognised as non zero division
			}
		}
	}
}
