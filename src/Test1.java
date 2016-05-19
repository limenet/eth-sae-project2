/**
 * An example application you may want to analyze to test your analysis.
 *
 */
public class Test1 {
	
	public static String DIV_OUTPUT = "NO_DIV_ZERO";
	
    public static String BOUNDS_OUTPUT = "NO_OUT_OF_BOUNDS";
    
    public static void foo() {
        PrinterArray pa = new PrinterArray(5);
        pa.sendJob(2);
    }
}
