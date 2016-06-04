// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

public class TestNoNothing {

	// no violations
	public static void foo1(int n) {
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

	public static void foo11() {
		PrinterArray pa = new PrinterArray(5);
		pa.sendJob(2);
	}

	public static void foo12(PrinterArray pa) {
		int b = 1;
		for (int j = 2; j < 10; j++) {
			int f = 1 / j;
			if (j == 1) {
				b = 10;
			}
		}
		pa = new PrinterArray(5);
		pa.sendJob(b);

	}

	public static void foo13() {
		foo14();
	}

	public static void foo14() {
		// I do nothing
	}

	public static void foo15() {
		PrinterArray pa1 = new PrinterArray(1);
		pa1.sendJob(0);
		PrinterArray pa2 = new PrinterArray(2);
		pa1 = pa2;
		pa1.sendJob(1);
	}

	public static void foo16(int n) {
		if (n >= 0 && n <= 8) {
			PrinterArray pa = new PrinterArray(9);
			pa.sendJob(n);
		}

	}

	// Just a bunch of random Java code
	// copied together from the interwebs

	// Due to our way of using Soot
	// we cannot use any libraries in this file
	// e.g. System.out.println()
	// as such this code has been shorted.

	public static void bubble_srt(int array[]) {
		int n = array.length;
		int k;
		for (int m = n; m >= 0; m--) {
			for (int i = 0; i < n - 1; i++) {
				k = i + 1;
				if (array[i] > array[k]) {
					swapNumbers(i, k, array);
				}
			}
			printNumbers(array);
		}
	}

	private static void swapNumbers(int i, int j, int[] array) {

		int temp;
		temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	private static void printNumbers(int[] input) {

		for (int i = 0; i < input.length; i++) {
		}
	}

	public static void main(String[] args) {
		int[] input = { 4, 2, 9, 6, 23, 12, 34, 0, 1 };
		bubble_srt(input);
		int[] a = { 2, 6, 3, 5, 1 };
		mergeSort(a);
	}

	public static void mergeSort(int[] a) {
		int[] tmp = new int[a.length];
		mergeSort(a, tmp, 0, a.length - 1);
	}

	private static void mergeSort(int[] a, int[] tmp, int left, int right) {
		if (left < right) {
			int center = (left + right) / 2;
			mergeSort(a, tmp, left, center);
			mergeSort(a, tmp, center + 1, right);
			merge(a, tmp, left, center + 1, right);
		}
	}

	private static void merge(int[] a, int[] tmp, int left, int right,
			int rightEnd) {
		int leftEnd = right - 1;
		int k = left;
		int num = rightEnd - left + 1;

		while (left <= leftEnd && right <= rightEnd)
			if (a[left] < a[right])
				tmp[k++] = a[left++];
			else
				tmp[k++] = a[right++];

		while (left <= leftEnd)
			// Copy rest of first half
			tmp[k++] = a[left++];

		while (right <= rightEnd)
			// Copy rest of right half
			tmp[k++] = a[right++];

		// Copy tmp back
		for (int i = 0; i < num; i++, rightEnd--)
			a[rightEnd] = tmp[rightEnd];
	}

	public static void quicksort(int[] A, int low, int hi) {

		int len = hi - low + 1;

		if (len < 2) { // base case 1
			return;
		}

		else if (len == 2) { // base case 2 (bug-prone now)
			if (A[low] <= A[hi]) {
				return;
			}
		}

		else { // start scanning and swapping and recursing
			int pivot = low; // the index of pivot

			while (len > 1) { // as long as the pivot is not at where it should
								// be...scan and swap

				// pivot = low; // updating...unnecessary?

				while (pivot < hi) { // check on the right
					if (A[pivot] <= A[hi]) {
						hi--;
					} else {
						break;
					}
				}

				if (A[pivot] > A[hi]) { // now swap pivot and hi;
					int holder = A[pivot];
					A[low] = A[hi];
					A[hi] = holder;

					pivot = hi; // update pointers
					low++;
				}

				while (pivot > low) { // check on the left
					if (A[pivot] >= A[low]) {
						low++;
					} else {
						break;
					}
				}

				if (A[pivot] < A[low]) { // now swap pivot and low;
					int holder = A[pivot];
					A[hi] = A[low];
					A[low] = holder;

					pivot = low;
					hi--; // update pointers
				}

				len = hi - low + 1; // update len for the loop check

			}

			// now the pivot is in the right position...split and recurse.
			quicksort(A, 0, pivot - 1); // recurse on the left

			quicksort(A, pivot + 1, A.length - 1); // recurse on the right

		} // the end of the else case.
	}

	public int fibIteration(int n) {
		int x = 0, y = 1, z = 1;
		for (int i = 0; i < n; i++) {
			x = y;
			y = z;
			z = x + y;
		}
		return x;
	}
}
