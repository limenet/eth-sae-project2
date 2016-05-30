// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

public class TestMath {
	public static void foo() {
		int i;

		i = 1 / (2 + (-2));
	}
	
	/* we have a lot of testcases like this
	public static void spam(int n) {
		int i = 5 / n;
	}
	*/
}
