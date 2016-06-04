// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = MAY_OUT_OF_BOUNDS

class TestPointerAssignemnt {
	/*
	public static void foo() {
		PrinterArray pa = new PrinterArray(10);
		PrinterArray pb = new PrinterArray(20);
		PrinterArray pc = new PrinterArray(30);
		for (int i = 1; i <= 20; i++) {
			pa = pb;
			pb = pc;
			pc = pa;
		}
		pc.sendJob(25);
	}
	*/
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
}