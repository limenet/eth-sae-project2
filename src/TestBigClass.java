// DIV_OUTPUT = NO_DIV_ZERO
// BOUNDS_OUTPUT = NO_OUT_OF_BOUNDS

// Just a bunch of random Java code
// copied together from the interwebs

// Due to our way of using Soot
// we cannot use any libraries in this file
// e.g. System.out.println()
// as such this code has been shorted.

public class TestBigClass {

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