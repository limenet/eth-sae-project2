package ch.ethz.sae;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import apron.ApronException;
import apron.Interval;
import apron.MpqScalar;
import soot.Local;
import soot.PointsToSet;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.IntConstant;
import soot.jimple.internal.JDivExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.sets.DoublePointsToSet;
import soot.jimple.spark.sets.EmptyPointsToSet;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.toolkits.graph.BriefUnitGraph;

public class Verifier {

	public static Boolean suppressErrors = false;

	public static void main(String[] args) throws ApronException {
		long startTime = System.nanoTime();

		if (args.length < 1) {
			System.err
					.println("Usage: java -classpath soot-2.5.0.jar:./bin ch.ethz.sae.Verifier <class to test> <show errors>");
			System.exit(-1);
		}
		String analyzedClass = args[0];
		if (args.length == 2) {
			if (args[1].equals("no")) {
				suppressErrors = true;
			}
		}
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
		long endTime = System.nanoTime();

		System.out.println(analyzedClass + " "
				+ ((endTime - startTime) / 1000000) + " ms");

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

	static void debug(Object what) {
		if (!suppressErrors) {
			System.out.println("Debug: " + what.toString());
		}
	}

	static void unhandled(String what) {
		if (!suppressErrors) {
			System.err.println("Can't handle " + what);
		}
	}

	static void todo(String what) {
		if (!suppressErrors) {
			System.err.println("// TODO: " + what);
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
						int cmp = state.get()
								.getBound(state.man, divisor.toString())
								.cmp(new MpqScalar(0));
						if (cmp == 0 || cmp == 1) {
							return false;
						}

					} else {
						// TODO handle the case where divisor is not a constant
						todo("divisor is not a constant " + rightOp.getClass());
						return false;
					}
				}
			}
		}

		// Return false if the method may have division by zero errors
		return true;
	}

	private static boolean verifyBounds(SootMethod method, Analysis fixPoint,
			PAG pointsTo) throws ApronException {

		// TODO: Create a list of all allocation sites for PrinterArray
		// @andrinadenzler 2016-06-03 implemented

		Map<PointsToSet, Integer> allocationSites = new HashMap<PointsToSet, Integer>();

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

			if (u instanceof JInvokeStmt
					&& ((JInvokeStmt) u).getInvokeExpr() instanceof JSpecialInvokeExpr) {

				// TODO: Get the size of the PrinterArray given as argument to
				// the constructor
				// @andrinadenzler 2016-06-03 changed implementation from @limenet 2016-05-23 17:37

				JInvokeStmt invokeStmt = (JInvokeStmt) u;

				JSpecialInvokeExpr invokeExpr = (JSpecialInvokeExpr) invokeStmt
						.getInvokeExpr();

				Local base = (Local) invokeExpr.getBase();
				DoublePointsToSet pts = (DoublePointsToSet) pointsTo
						.reachingObjects(base);

				Value argValue = invokeExpr.getArg(0);

				if (argValue instanceof IntConstant) {
					int argInt = ((IntConstant) argValue).value;
					allocationSites.put(pts, argInt);

					debug("SpecialInvokeExpr with argument: " + argInt);
				} else {
					unhandled("SpecialInvokeExpr with non-constant arg.");
				}

			}

			if (u instanceof JInvokeStmt
					&& ((JInvokeStmt) u).getInvokeExpr() instanceof JVirtualInvokeExpr) {

				JInvokeStmt invokeStmt = (JInvokeStmt) u;

				JVirtualInvokeExpr invokeExpr = (JVirtualInvokeExpr) invokeStmt
						.getInvokeExpr();

				Local base = (Local) invokeExpr.getBase();
				if (pointsTo.reachingObjects(base) instanceof EmptyPointsToSet) {
					return false;
				}
				DoublePointsToSet pts = (DoublePointsToSet) pointsTo
						.reachingObjects(base);

				if (invokeExpr.getMethod().getName()
						.equals(Analysis.functionName)) {

					// TODO: Check whether the 'sendJob' method's argument is
					// within bounds
					// @andrinadenzler 2016-06-03 implemented

					Value argValue = invokeExpr.getArg(0);
					Interval argInterval = Analysis
							.getInterval(state, argValue);

					debug("VirtualInvokeExpr with argument: " + argInterval);

					// Visit all allocation sites that the base pointer may
					// reference
					MyP2SetVisitor visitor = new MyP2SetVisitor(
							allocationSites, argInterval);
					pts.forall(visitor);

					if (!visitor.getReturnValue()) {
						return false;
					}
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

	Map<PointsToSet, Integer> allocationSites;
	Interval argumentInterval;

	public MyP2SetVisitor(Map<PointsToSet, Integer> allocationSites,
			Interval methodCallArg) {
		this.allocationSites = allocationSites;
		this.argumentInterval = methodCallArg;
	}

	@Override
	public void visit(Node arg0) {
		// TODO: Check whether the argument given to sendJob is within bounds
		// @andrinadenzler 2016-06-03 implemented

		AllocNode allocNode = (AllocNode) arg0;

		this.returnValue = true;

		for (Entry<PointsToSet, Integer> maxArg : allocationSites.entrySet()) {
			if (((DoublePointsToSet) maxArg.getKey()).contains(allocNode)) {
				if (!argumentInterval.isLeq(new Interval(0, maxArg.getValue() - 1))) {
					this.returnValue = false;
					return;
				}
			}
		}
	}

	static void debug(Object what) {
		Verifier.debug(what);
	}
}
