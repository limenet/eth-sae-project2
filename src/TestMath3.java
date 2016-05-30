// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

public class TestMath3 {
	public static void baz() {
		int i = 1 + (2 + (3 + (4 + (5 / (-1 + 1)))));
	}

}
