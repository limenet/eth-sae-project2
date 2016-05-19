package ch.ethz.sae;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCases {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException, InterruptedException {

		String[] testClasses = execCmd("find " + cwd() + "/src -maxdepth 1 -name Test*.java").split("\\n");
		for (String testClass : testClasses) {
			if (testClass != "") {
				List<String> contents = readFile(testClass);
				String divOutputExpected = "";
				String boundsOutputExpected = "";
				for (int i = 0; i <= 1; i++) {
					String line = contents.get(i);
					String[] split = line.split("=");
					String testType = split[0].substring(3, split[0].length() - 1);
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
				String[] actualOutputOfTest = execCmd(cwd() + "/run.sh " + className).split("\\n");
				for (int i = actualOutputOfTest.length -2 ; i <= actualOutputOfTest.length -1; i++) {
					String output = actualOutputOfTest[i].substring(className.length() + 1);
					if (output.contains("DIV_ZERO")) {
						assertEquals(divOutputExpected, output);
					} else if (output.contains("OUT_OF_BOUNDS")) {
						assertEquals(boundsOutputExpected, output);
					}
					
				}
			}
		}
		System.out.println("Test finished");
	}
	
	private String cwd(){
	  String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().toString();
	  
	  return relPath.substring(5, relPath.length() - 5);
	}
	
	private List<String> readFile(String filename)
	{
	  List<String> records = new ArrayList<String>();
	  try
	  {
	    BufferedReader reader = new BufferedReader(new FileReader(filename));
	    String line;
	    while ((line = reader.readLine()) != null)
	    {
	      records.add(line);
	    }
	    reader.close();
	    return records;
	  }
	  catch (Exception e)
	  {
	    System.err.format("Exception occurred trying to read '%s'.", filename);
	    e.printStackTrace();
	    return null;
	  }
	}
	private String execCmd(String cmd) throws java.io.IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
     
        return s.hasNext() ? s.next() : "";
    }

}
