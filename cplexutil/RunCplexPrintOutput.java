package cplexutil; 


import ilog.concert.*;
import ilog.cplex.*;
import ilog.cplex.IloCplex.UnknownObjectException;

import java.util.*;

import data.RootedTriplet;

public class RunCplexPrintOutput
{


	private IloNumVar[] parse(IloCplex cplex) throws IloException {
		HashSet<IloNumVar> vars = new HashSet<IloNumVar>();
		Iterator it = cplex.iterator();
		IloLinearNumExpr expr;
		IloLinearNumExprIterator it2;
		while (it.hasNext()) {
			IloAddable thing = (IloAddable) it.next();
			if (thing instanceof IloRange) {
				expr = (IloLinearNumExpr) ((IloRange) thing).getExpr();
				it2 = expr.linearIterator();
				while (it2.hasNext()) {
					vars.add(it2.nextNumVar());
				}
			} else if (thing instanceof IloObjective) {
				expr = (IloLinearNumExpr) ((IloObjective) thing).getExpr();
				it2 = expr.linearIterator();
				while (it2.hasNext()) {
					vars.add(it2.nextNumVar());
				}
			} else if (thing instanceof IloSOS1) {
				vars.addAll(Arrays.asList(((IloSOS1) thing).getNumVars()));
			} else if (thing instanceof IloSOS2) {
				vars.addAll(Arrays.asList(((IloSOS2) thing).getNumVars()));
			} else if (thing instanceof IloLPMatrix) {
				vars.addAll(Arrays.asList(((IloLPMatrix) thing).getNumVars()));
			}
		}
		IloNumVar[] varray = vars.toArray(new IloNumVar[1]);
		return varray;
	}

	public List<RootedTriplet> getTriplets() {
		String fileName = "ilp.lp";	//! The LP we are going to solve
		List<RootedTriplet> triplets = new ArrayList<>();; 

		try
		{

			IloCplex cplex = new IloCplex();

			//! fileName is the name of the file where your ILP is
			cplex.importModel(fileName);

			//! uncomment this to suppress visual output from cplex
			//! cplex.setOut(null);  

			//! this is the solving bit
			if ( cplex.solve() )
			{
				IloNumVar[] var = parse(cplex);

				//! this lists all the variables in the program, and their values at optimality			
				double x[] = cplex.getValues(var);
				for(int loop=0; loop<x.length; loop++ )
				{
					System.out.println(var[loop].getName() + " = " + x[loop]);
					if (x[loop] == 1.0) {
						String varname = var[loop].getName();
						int a = Character.getNumericValue(varname.charAt(1)); 
						int b = Character.getNumericValue(varname.charAt(2)); 
						int c = Character.getNumericValue(varname.charAt(3)); 

						triplets.add(new RootedTriplet(a,b,c));
					}
				}
			}

			//! this gets the objective function value, here I truncate it to be an int
			int objfunc = (int) Math.round(cplex.getObjValue());		

			//! this deallocates the CPLEX resources
			cplex.end();

		} catch( IloException e )
		{
			System.out.println("Something went wrong with CPLEX.");
			System.exit(0);
		}

		return triplets; 

	}
	
	public void getValues(IloCplex cplex) throws UnknownObjectException, IloException {
		IloNumVar[] var = parse(cplex);

		//! this lists all the variables in the program, and their values at optimality			
		double x[] = cplex.getValues(var);
		for(int loop=0; loop<x.length; loop++ ) {
			System.out.println(var[loop].getName() + " = " + x[loop]);
		}
	}
	
	



}		