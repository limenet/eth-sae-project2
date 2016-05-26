package ch.ethz.sae;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import apron.Abstract1;
import apron.ApronException;
import apron.Environment;
import apron.Interval;
import apron.Manager;
import apron.MpqScalar;
import apron.Polka;
import apron.Texpr1CstNode;
import apron.Texpr1Intern;
import apron.Texpr1Node;
import apron.Texpr1VarNode;
import soot.ArrayType;
import soot.DoubleType;
import soot.IntType;
import soot.IntegerType;
import soot.Local;
import soot.PrimType;
import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.AbstractBinopExpr;
import soot.jimple.internal.JAddExpr;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JDivExpr;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JGeExpr;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JLeExpr;
import soot.jimple.internal.JLtExpr;
import soot.jimple.internal.JMulExpr;
import soot.jimple.internal.JNeExpr;
import soot.jimple.internal.JSubExpr;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.graph.LoopNestTree;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;
import soot.util.Chain;

public class Analysis extends ForwardBranchedFlowAnalysis<AWrapper> {

	public static Manager man;

	private Environment env;

	public UnitGraph g;

	public String local_ints[]; // integer local variables of the method

	public static String reals[] = { "x" };

	public SootClass jclass;

	private String class_ints[]; // integer class variables where the method is

	public static String resourceArrayName = "PrinterArray";

	public static String functionName = "sendJob";

	private static final int WIDENING_THRESHOLD = 6;

	private HashMap<Unit, Counter> loopHeads, backJumps;

	private void recordIntLocalVars() {

		Chain<Local> locals = g.getBody().getLocals();
		int count = 0;
		Iterator<Local> it = locals.iterator();
		while (it.hasNext()) {
			JimpleLocal next = (JimpleLocal) it.next();
			if (next.getType() instanceof IntegerType)
				count += 1;
		}

		local_ints = new String[count];

		int i = 0;
		it = locals.iterator();
		while (it.hasNext()) {
			JimpleLocal next = (JimpleLocal) it.next();
			String name = next.getName();
			if (next.getType() instanceof IntegerType)
				local_ints[i++] = name;
		}
	}

	private void recordIntClassVars() {

		Chain<SootField> ifields = jclass.getFields();

		int count = 0;
		Iterator<SootField> it = ifields.iterator();
		while (it.hasNext()) {
			SootField next = it.next();
			if (next.getType() instanceof IntegerType)
				count += 1;
		}

		class_ints = new String[count];

		int i = 0;
		it = ifields.iterator();
		while (it.hasNext()) {
			SootField next = it.next();
			String name = next.getName();
			if (next.getType() instanceof IntegerType)
				class_ints[i++] = name;
		}
	}

	/* Build an environment with integer variables. */
	public void buildEnvironment() {

		recordIntLocalVars();
		recordIntClassVars();

		String ints[] = new String[local_ints.length + class_ints.length];

		/* add local ints */
		for (int i = 0; i < local_ints.length; i++) {
			ints[i] = local_ints[i];
		}

		/* add class ints */
		for (int i = 0; i < class_ints.length; i++) {
			ints[local_ints.length + i] = class_ints[i];
		}

		env = new Environment(ints, reals);
	}

	/* Instantiate a domain. */
	private void instantiateDomain() {
		man = new Polka(true);
	}

	/* === Constructor === */
	public Analysis(UnitGraph g, SootClass jc) {
		super(g);

		this.g = g;
		this.jclass = jc;

		buildEnvironment();
		instantiateDomain();

		loopHeads = new HashMap<Unit, Counter>();
		backJumps = new HashMap<Unit, Counter>();
		for (Loop l : new LoopNestTree(g.getBody())) {
			loopHeads.put(l.getHead(), new Counter(0));
			backJumps.put(l.getBackJumpStmt(), new Counter(0));
		}
	}

	void run() {
		doAnalysis();
	}

	static void unhandled(String what) {
		System.err.println("Can't handle " + what);
	}

	private void handleDef(Abstract1 o, Value left, Value right)
			throws ApronException {

		Texpr1Node lAr = null;
		Texpr1Node rAr = null;
		Texpr1Intern xp = null;

		System.out.println(right.getType());
		if (left instanceof JimpleLocal) {
			String varName = ((JimpleLocal) left).getName();

			if (right instanceof IntConstant) {
				IntConstant constant = (IntConstant) right;
				rAr = new Texpr1CstNode(new MpqScalar(constant.value));
				xp = new Texpr1Intern(env, rAr);
				o.assign(man, varName, xp, null);
			} else if (right instanceof JimpleLocal && ((JimpleLocal) right).getType() instanceof PrimType) {
				JimpleLocal local = (JimpleLocal) right;
				rAr = new Texpr1VarNode(local.getName());
				xp = new Texpr1Intern(env, rAr);
				o.assign(man, varName, xp, null);
				// TODO: ParameterRef as well?
			} else if (right instanceof JMulExpr) {
				JMulExpr mulExpr = (JMulExpr) right;
				Interval op1 = null, op2 = null;
				if (mulExpr.getOp1() instanceof IntConstant) {
					int op1Value = ((IntConstant) mulExpr.getOp1()).value;
					op1 = new Interval(op1Value, op1Value);
				} else if (mulExpr.getOp1() instanceof JimpleLocal) {
					op1 = o.getBound(man, ((JimpleLocal) mulExpr.getOp1()).getName());
				} else {
					System.out.println("// TODO: handle MulExpr: " + mulExpr.getOp1().getClass());
				}
				
				if (mulExpr.getOp2() instanceof IntConstant) {
					int op2Value = ((IntConstant) mulExpr.getOp2()).value;
					op2 = new Interval(op2Value, op2Value);
				} else if (mulExpr.getOp2() instanceof JimpleLocal) {
					op2 = o.getBound(man, ((JimpleLocal) mulExpr.getOp2()).getName());
				} else {
					System.out.println("// TODO: handle MulExpr: " + mulExpr.getOp2().getClass());
				}
				
				System.out.println(op1);
				System.out.println(op2);
				// TODO
			} else if (right instanceof JSubExpr) {
				JSubExpr subExpr = (JSubExpr) right;
				// TOOD
			} else if (right instanceof JAddExpr) {
				JAddExpr addExpr = (JAddExpr) right;
				// TODO
			} else if (right instanceof JDivExpr) {
				JDivExpr divExpr = (JDivExpr) right;
				// TODO
			}
			// TODO: Handle other kinds of assignments (e.g. x = y * z)
			else {
				if (o.getEnvironment().hasVar(varName)) {
					o.forget(man, varName, false);
				}
			}
		}
	}

	private void handleIf(AbstractBinopExpr eqExpr, Abstract1 in, AWrapper ow,
			AWrapper ow_branchout) throws ApronException {

		Value left = eqExpr.getOp1();
		Value right = eqExpr.getOp2();

		Texpr1Node lAr = null;

		if (left instanceof IntConstant) {
			lAr = new Texpr1CstNode(new MpqScalar(((IntConstant) left).value));
		} else if (left instanceof JimpleLocal) {
			if (left.getType().toString().equals("PrinterArray")) {
				ow.set(new Abstract1(man, in));
				ow_branchout.set(new Abstract1(man, in));
				return;
			}
			lAr = new Texpr1VarNode(((JimpleLocal) left).getName());
		} else {
			System.out.println("left: unexpected:" + left.getClass() + " name:"
					+ ((JimpleLocal) left).getName());
		}

		// TODO: Handle required conditional expressions
	}

	@Override
	protected void flowThrough(AWrapper current, Unit op,
			List<AWrapper> fallOut, List<AWrapper> branchOuts) {

		Stmt s = (Stmt) op;

		Abstract1 in = ((AWrapper) current).get();

		Abstract1 o;
		try {
			o = new Abstract1(man, in);
			Abstract1 o_branchout = new Abstract1(man, in);

			if (s instanceof DefinitionStmt) {
				DefinitionStmt ds = (DefinitionStmt) s;
				Value left = ds.getLeftOp();
				Value right = ds.getRightOp();

				// You do not need to handle these cases:
				if (!(left instanceof JimpleLocal)) {
					unhandled("1: Assignment to non-variables is not handled.");
				} else if ((left instanceof JArrayRef)
						&& (!((((JArrayRef) left).getBase()) instanceof JimpleLocal))) {
					unhandled("2: Assignment to a non-local array variable is not handled.");
				}

				if (left instanceof JArrayRef
						|| left instanceof JInstanceFieldRef) {
					return;
				}

				if (left.getType() instanceof DoubleType) {
					return;
				}

				if ((left.getType() instanceof RefType && !left.getType()
						.toString().equals(resourceArrayName))
						|| left.getType() instanceof ArrayType) {
					return;
				}

				// Make sure you support all definition statements
				handleDef(o, left, right);

			} else if (s instanceof JIfStmt) {
				IfStmt ifs = (JIfStmt) s;
				Value condition = ifs.getCondition();

				if (condition instanceof JEqExpr
						|| condition instanceof JNeExpr
						|| condition instanceof JGeExpr
						|| condition instanceof JLeExpr
						|| condition instanceof JLtExpr
						|| condition instanceof JGtExpr) {

					AWrapper ow = new AWrapper(null);
					AWrapper ow_branchout = new AWrapper(null);

					AbstractBinopExpr eqExpr = (AbstractBinopExpr) condition;

					// Make sure handleIf supports the conditional expressions
					// above
					handleIf(eqExpr, in, ow, ow_branchout);

					o = ow.get();
					o_branchout = ow_branchout.get();
				}
			}

			for (Iterator<AWrapper> it = fallOut.iterator(); it.hasNext();) {
				AWrapper op1 = it.next();

				if (o != null) {
					op1.set(o);
					op1.setStatement(s);
				}
			}

			for (Iterator<AWrapper> it = branchOuts.iterator(); it.hasNext();) {
				AWrapper op1 = it.next();

				if (o_branchout != null) {
					op1.set(o_branchout);
					op1.setStatement(s);
				}
			}

		} catch (ApronException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void copy(AWrapper source, AWrapper dest) {
		dest.copy(source);
	}

	@Override
	protected AWrapper entryInitialFlow() {

		Abstract1 top = null;

		try {
			top = new Abstract1(man, env);
		} catch (ApronException e) {
		}

		AWrapper a = new AWrapper(top);
		a.man = man;
		return a;
	}

	private static class Counter {
		int value;

		Counter(int v) {
			value = v;
		}
	}

	@Override
	protected void merge(Unit succNode, AWrapper x, AWrapper y, AWrapper u) {
		Counter count = loopHeads.get(succNode);

		Abstract1 a1 = x.get();
		Abstract1 a2 = y.get();
		Abstract1 a3 = null;

		try {
			if (count != null) {
				++count.value;
				if (count.value < WIDENING_THRESHOLD) {
					a3 = a1.joinCopy(man, a2);
				} else {
					a3 = a1.widening(man, a2);
				}
			} else {
				a3 = a1.joinCopy(man, a2);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		u.set(a3);

	}

	@Override
	protected void merge(AWrapper src1, AWrapper src2, AWrapper trg) {

		Abstract1 a1 = src1.get();
		Abstract1 a2 = src2.get();
		Abstract1 a3 = null;

		try {
			a3 = a1.joinCopy(man, a2);
		} catch (ApronException e) {
			e.printStackTrace();
		}

		trg.set(a3);
	}

	@Override
	protected AWrapper newInitialFlow() {
		Abstract1 bot = null;

		try {
			bot = new Abstract1(man, env, true);
		} catch (ApronException e) {
		}
		AWrapper a = new AWrapper(bot);
		a.man = man;
		return a;

	}

	public static final boolean isIntValue(Value val) {
		return val.getType().toString().equals("int")
				|| val.getType().toString().equals("short")
				|| val.getType().toString().equals("byte");
	}

	public static final Interval getInterval(AWrapper state, Value val) {
		Interval top = new Interval();
		top.setTop();
		if (!isIntValue(val)) {
			return top;
		}
		if (val instanceof IntConstant) {
			int value = ((IntConstant) val).value;
			return new Interval(value, value);
		}
		if (val instanceof Local) {
			String var = ((Local) val).getName();
			Interval interval = null;
			try {
				interval = state.get().getBound(man, var);
			} catch (ApronException e) {
				e.printStackTrace();
			}
			return interval;
		}
		if (val instanceof InvokeExpr) {
			return top;
		}
		return top;
	}
}
