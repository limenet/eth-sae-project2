// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS
public class Test6 {
	public static void foo() {
		int i;

		i = 1 / (2 + (-2));
	}

	public static void bar() {
		int i = 0;

		i = i / i;
	}

	public static void baz() {
		int i = 1 + (2 + (3 + (4 + (5 / 0))));
	}
}
