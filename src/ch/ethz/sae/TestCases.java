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

	@Before
	public void setUp() throws Exception {
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

				output.add(new String[] { className + "-divByZero",
						divOutputExpected });
				output.add(new String[] { className + "-outOfBounds",
						boundsOutputExpected });
				output.add(new String[] { className + "-timing", "10000" });
			}
		}

		return output;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException, InterruptedException {
		String className = inputClass.split("-")[0];
		String testType = inputClass.split("-")[1];
		String run = cwd() + "/run.sh " + className;

		if (testType.equals("divByZero") || testType.equals("outOfBounds")) {
			String[] actualOutputOfTest = execCmd(run).split("\\n");
			for (int i = actualOutputOfTest.length - 2; i <= actualOutputOfTest.length - 1; i++) {
				String output = actualOutputOfTest[i].substring(className
						.length() + 1);
				if (output.contains("DIV_ZERO") && testType.equals("divByZero")) {
					assertEquals(expected, output);
				} else if (output.contains("OUT_OF_BOUNDS")
						&& testType.equals("outOfBounds")) {
					assertEquals(expected, output);
				}
			}

		} else if (testType.equals("timing")) {
			long startTime = System.nanoTime();
			execCmd(run);
			long endTime = System.nanoTime();
			long duration = (endTime - startTime) / 1000000;
			assertTrue(duration < Integer.parseInt(expected));
		}

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
