package ch.ethz.sae;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import apron.ApronException;
import apron.Coeff;
import apron.Interval;
import apron.Scalar;
import soot.Unit;
import soot.jimple.DefinitionStmt;
import soot.jimple.IntConstant;
import soot.jimple.internal.ImmediateBox;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JDivExpr;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.internal.JimpleLocalBox;
import soot.jimple.spark.sets.DoublePointsToSet;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.ValueBox;
import soot.toolkits.graph.BriefUnitGraph;

public class Verifier {

	public static void main(String[] args) throws ApronException {
		if (args.length != 1) {
			System.err
					.println("Usage: java -classpath soot-2.5.0.jar:./bin ch.ethz.sae.Verifier <class to test>");
			System.exit(-1);
		}
		String analyzedClass = args[0];
		SootClass c = loadClass(analyzedClass);

		System.out.println(analyzedClass + "\n\n");

		PAG pointsToAnalysis = doPointsToAnalysis(c);

		int programCorrectFlag = 1;
		int divisionByZeroFlag = 1;

		for (SootMethod method : c.getMethods()) {

			Analysis analysis = new Analysis(new BriefUnitGraph(
					method.retrieveActiveBody()), c);
			analysis.run();

			if (!verifyBounds(method, analysis, pointsToAnalysis)) {
				programCorrectFlag = 0;
			}
			if (!verifyDivisionByZero(method, analysis)) {
				divisionByZeroFlag = 0;
			}
		}

		if (divisionByZeroFlag == 1) {
			System.out.println(analyzedClass + " NO_DIV_ZERO");
		} else {
			System.out.println(analyzedClass + " MAY_DIV_ZERO");
		}

		if (programCorrectFlag == 1) {
			System.out.println(analyzedClass + " NO_OUT_OF_BOUNDS");
		} else {
			System.out.println(analyzedClass + " MAY_OUT_OF_BOUNDS");
		}
	}

	private static boolean verifyDivisionByZero(SootMethod method,
			Analysis fixPoint) throws ApronException {
		for (Unit u : method.retrieveActiveBody().getUnits()) {
			AWrapper state = fixPoint.getFlowBefore(u);

			try {
				if (state.get().isBottom(Analysis.man)) {
					// unreachable code
					continue;
				}
			} catch (ApronException e) {
				e.printStackTrace();
			}

			// TODO: Check that all divisors are not zero
			// @limenet 2016-05-23 15:11 this is implemented EXCEPT FOR
			// loop widening (which is performed in Analysis.java)

			if (u instanceof DefinitionStmt) {
				DefinitionStmt defStmt = (DefinitionStmt) u;
				if (defStmt.getRightOp() instanceof JDivExpr) {
					JDivExpr divExpr = (JDivExpr) defStmt.getRightOp();
					Value rightOp = divExpr.getOp2();

					if (rightOp instanceof IntConstant) {
						// division by a constant
						// check if constant is 0
						IntConstant divisor = (IntConstant) rightOp;
						if (divisor.value == 0) {
							return false;
						}
					} else if (rightOp instanceof JimpleLocal) {
						// check if divisor is a local variable
						// check if local may be 0
						JimpleLocal divisor = (JimpleLocal) rightOp;
						System.out.println(u);
						if (state.get().getBound(state.man, divisor.toString())
								.cmp(new Interval(0, 0)) == 1) {
							return false;
						}
					} else {
						// TODO handle the case where divisor is not a constant
						System.out.println("// TODO (div-by-zero): "
								+ rightOp.getClass());
						return false;
					}
				}
			}
		}

		// Return false if the method may have division by zero errors
		return true;
	}

	private static boolean verifyBounds(SootMethod method, Analysis fixPoint,
			PAG pointsTo) {

		// TODO: Create a list of all allocation sites for PrinterArray

		Map<String, Integer> declaredPAs = new HashMap<String, Integer>();
		Map<String, Integer> initializedPAs = new HashMap<String, Integer>();

		for (Unit u : method.retrieveActiveBody().getUnits()) {
			AWrapper state = fixPoint.getFlowBefore(u);

			try {
				if (state.get().isBottom(Analysis.man)) {
					// unreachable code
					continue;
				}
			} catch (ApronException e) {
				e.printStackTrace();
			}

			if (u instanceof JAssignStmt) {
				JAssignStmt assignStmt = (JAssignStmt) u;

				String varNameLeft = assignStmt.getLeftOp().toString();
				String varNameRight = assignStmt.getRightOp().toString();

				if (declaredPAs.containsKey(varNameRight)) {
					System.out.println("renaming " + varNameRight + " to "
							+ varNameLeft);
					initializedPAs.put(varNameLeft,
							declaredPAs.get(varNameRight));
				}
			}

			if (u instanceof JInvokeStmt
					&& ((JInvokeStmt) u).getInvokeExpr() instanceof JSpecialInvokeExpr) {

				// TODO: Get the size of the PrinterArray given as argument to
				// the constructor
				// @limenet 2016-05-23 17:37 this is working
				// see the variable argInt
				JInvokeStmt invokeStmt = (JInvokeStmt) u;
				Value argValue = invokeStmt.getInvokeExpr().getArg(0);
				int argInt = ((IntConstant) argValue).value;

				// System.out.println(state.getStatement());
				System.out.println("init with " + argInt);
				String localName = ((JimpleLocalBox) invokeStmt.getInvokeExpr()
						.getUseBoxes().get(0)).getValue().toString();

				declaredPAs.put(localName, argInt);


			}

			if (u instanceof JInvokeStmt
					&& ((JInvokeStmt) u).getInvokeExpr() instanceof JVirtualInvokeExpr) {

				JInvokeStmt jInvStmt = (JInvokeStmt) u;

				JVirtualInvokeExpr invokeExpr = (JVirtualInvokeExpr) jInvStmt
						.getInvokeExpr();

				Local base = (Local) invokeExpr.getBase();
				DoublePointsToSet pts = (DoublePointsToSet) pointsTo
						.reachingObjects(base);

				if (invokeExpr.getMethod().getName()
						.equals(Analysis.functionName)) {

					// TODO: Check whether the 'sendJob' method's argument is
					// within bounds

					Value argValue = jInvStmt.getInvokeExpr().getArg(0);
					int argInt = ((IntConstant) argValue).value;

					String localName = ((JimpleLocalBox) jInvStmt
							.getInvokeExpr().getUseBoxes().get(0)).getValue()
							.toString();
					System.out.println(localName + ": parameter: " + argInt
							+ ", constructed with "
							+ initializedPAs.get(localName));

					// @limenet 2016-05-23 17:38
					// The following three if-statements are a very basic
					// form of bounds-checking. No pointer analysis etc. is
					// implemented here.

					if (!initializedPAs.containsKey(localName)) {
						System.out.println("Unknown PrinterArray");
						return false;
					}

					if (initializedPAs.get(localName) == null) {
						System.out
								.println("Invalid PrinterArray object (n=null)");
						return false;
					}

					if (argInt >= initializedPAs.get(localName)) {
						return false;
					}
					// Visit all allocation sites that the base pointer may
					// reference
					MyP2SetVisitor visitor = new MyP2SetVisitor();
					pts.forall(visitor);
				}

			}

		}

		// Return false if the method may have index out of bound errors
		return true;
	}

	private static SootClass loadClass(String name) {
		SootClass c = Scene.v().loadClassAndSupport(name);
		c.setApplicationClass();
		return c;
	}

	private static PAG doPointsToAnalysis(SootClass c) {
		Scene.v().setEntryPoints(c.getMethods());

		HashMap<String, String> options = new HashMap<String, String>();
		options.put("enabled", "true");
		options.put("verbose", "false");
		options.put("propagator", "worklist");
		options.put("simple-edges-bidirectional", "false");
		options.put("on-fly-cg", "true");
		options.put("set-impl", "double");
		options.put("double-set-old", "hybrid");
		options.put("double-set-new", "hybrid");

		SparkTransformer.v().transform("", options);
		PAG pag = (PAG) Scene.v().getPointsToAnalysis();

		return pag;
	}
}

class MyP2SetVisitor extends P2SetVisitor {

	@Override
	public void visit(Node arg0) {
		// TODO: Check whether the argument given to sendJob is within bounds
	}
}