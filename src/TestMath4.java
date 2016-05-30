// DIV_OUTPUT = MAY_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

public class TestMath4 {
	public static void foobar(int n) {
		int i = (1 + (2 + (3 + (4 + 5))) + n) / 0;
	}
}
