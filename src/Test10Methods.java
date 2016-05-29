// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

public class Test10Methods {

	// no violations
	public static void foo(int n) {
		PrinterArray pa = new PrinterArray(20);
		int a = 3;
		pa.sendJob(a);
	}

	// no violations
	public static void foo2() {
		// I do nothing
	}

	// no violations
	public static void foo3(int n) {
		int a = 0;
		for (int i = 1; i <= n; i++) {
			a = i;
		}
	}

	// no violations
	public static void foo4() {
		PrinterArray pa = new PrinterArray(20);
		PrinterArray pb = new PrinterArray(21);
		PrinterArray pc = new PrinterArray(22);
		for (int i = 1; i <= 20000; i += 0.5) {
			pa = pb; // pa cannot be referenced ever again; "lost"
			pb = pc;
			pc = pa; // pc point alternating to the original pb or pc; after an
						// even number of iterations to original pc
		}
		// loop was executed an even number of times => pc should have intervall
		// [0, 22-1]
		pc.sendJob(21);

	}

	// test 3 from Timon Blattner
	// no violations
	public static void foo5(int i) {
		int x = 0;
		if (i < 0) {
			x = 1 - i;
			i = 0 - i;
		} else {
			x = i + 1;
		}

		PrinterArray pa = new PrinterArray(x);
		pa.sendJob(i);
	}

	// test 6 from Timon Blattner
	// no violations
	public static void foo6() {
		PrinterArray pa = new PrinterArray(5);
		PrinterArray pb = pa;
		pb.sendJob(2);
	}

	// test 10 from Timon Blattner
	// no violations
	public static void foo7() {
		PrinterArray p1 = new PrinterArray(5);
		PrinterArray p2 = new PrinterArray(1);
		PrinterArray xx = p2;

		PrinterArray yy = xx;
		xx = p1;

		xx.sendJob(4);
	}

	// test 11 from Timon Blattner
	// no violations
	public static void foo8() {
		PrinterArray pa = new PrinterArray(1);
		pa.sendJob(0);
	}

	// test 12 from Timon Blattner
	// no violations
	public static void foo9(int i) {
		int x;

		for (x = i; x <= 3; x++) {

		}

		PrinterArray pa = new PrinterArray(x);
		pa.sendJob(2);
	}

	// test 14 from Timon Blattner
	// no violations
	public static void foo10(int i) {
		int x = 1;
		PrinterArray p = new PrinterArray(5);
		if (i > -3 && i < 3) {
			x = x / 1;
		}

		p.sendJob(x);
	}
}
