// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS
public class TestMath {
	public static void foo() {
		int i;

		i = 1 / (2 + (-2));
	}

	public static void bar() {
		int i = 0;

		i = i / i;
	}

	public static void baz() {
		int i = 1 + (2 + (3 + (4 + (5 / (-1 + 1)))));
	}

	public static void foobar(int n) {
		int i = (1 + (2 + (3 + (4 + 5))) + n) / 0;
	}
	
	public static void spam(int n) {
		int i = 5 / n;
	}
}
