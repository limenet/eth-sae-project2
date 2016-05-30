package ch.ethz.sae;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;

@RunWith(Parameterized.class)
public class TestCases {

	@Parameter(0)
	public String inputClass;

	@Parameter(1)
	public String expected;

	public String[] output = new String[2];

	public long execTime;

	@Before
	public void setUp() throws Exception {
		String className = inputClass.split("-")[0];
		String run = cwd() + "/run.sh " + className;
		long startTime = System.nanoTime();
		String actualOutputOfTest = execCmd(run);
		long endTime = System.nanoTime();
		String[] outputSplitted = actualOutputOfTest.split("\\n");
		this.execTime = (endTime - startTime) / 1000000;

		int j = 0;
		for (int i = outputSplitted.length - 2; i <= outputSplitted.length - 1; i++) {
			output[j] = outputSplitted[i].substring(className.length() + 1);
			j++;
		}
	}

	@Parameters(name = "{0}")
	public static Iterable<String[]> gatherClasses() throws IOException {
		List<String[]> output = new ArrayList<String[]>();

		String[] testClasses = execCmd(
				"find " + cwd() + "/src -maxdepth 1 -name Test*.java ").split(
				"\\n");
		Arrays.sort(testClasses);

		for (String testClass : testClasses) {
			if (testClass != "") {
				List<String> contents = readFile(testClass);
				String divOutputExpected = "";
				String boundsOutputExpected = "";
				for (int i = 0; i <= 1; i++) {
					String line = contents.get(i);
					String[] split = line.split("=");
					String testType = split[0].substring(3,
							split[0].length() - 1);
					String testOutput = split[1].substring(1);
					if (testType.equals("BOUNDS_OUTPUT")) {
						boundsOutputExpected = testOutput;
					} else if (testType.equals("DIV_OUTPUT")) {
						divOutputExpected = testOutput;
					}
				}
				String[] pathParts = testClass.split("/");
				String className = pathParts[pathParts.length - 1];
				className = className.substring(0, className.length() - 5);

				output.add(new String[] {
						className,
						divOutputExpected + "|" + boundsOutputExpected
								+ "|10000"

				});
			}
		}

		return output;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDivByZero() throws Exception {

		if (!output[0].contains("DIV_ZERO")) {
			throw new Exception("segfault");
		} else {
			assertEquals(expected.split("\\|")[0], output[0]);
		}
	}

	@Test
	public void testOutOfBounds() throws Exception {
		if (!output[1].contains("DIV_ZERO")) {
			throw new Exception("segfault");
		} else {
			assertEquals(expected.split("\\|")[1], output[1]);
		}
	}

	@Test
	public void testExecutionTime() {
		assertTrue(this.execTime < Integer.parseInt(expected.split("\\|")[2]));
	}

	// HELPERS
	private static String cwd() {
		return "/home/sae/project";
	}

	private static List<String> readFile(String filename) {
		List<String> records = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				records.add(line);
			}
			reader.close();
			return records;
		} catch (Exception e) {
			System.err.format("Exception occurred trying to read '%s'.",
					filename);
			e.printStackTrace();
			return null;
		}
	}

	private static String execCmd(String cmd) throws java.io.IOException {
		java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime()
				.exec(cmd).getInputStream()).useDelimiter("\\A");

		return s.hasNext() ? s.next() : "";
	}

}
