// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = MAY_OUT_OF_BOUNDS

class TestPointerAssignment {
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
}