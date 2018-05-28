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

	public List<RootedTriplet> getTriplets(String fileName) {
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
//					System.out.println(var[loop].getName() + " = " + x[loop]);
					if (x[loop] == 1.0) {
						String varname = var[loop].getName();
						int a = Integer.parseInt(varname.split("t")[1].split(",")[0]); 
						int b = Integer.parseInt(varname.split("t")[1].split(",")[1].split(";")[0]); 
						int c = Integer.parseInt(varname.split(";")[1]); 

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
	
	public String getValues(IloCplex cplex) throws UnknownObjectException, IloException {
		IloNumVar[] var = parse(cplex);

		//! this lists all the variables in the program, and their values at optimality			
		double x[] = cplex.getValues(var);
		String values = ""; 
		for(int loop=0; loop<x.length; loop++ ) {
			values += var[loop].getName() + " = " + x[loop] + "\n";
		}
		
		return values; 
	}
	
	public String getOnlyValues(IloCplex cplex) throws UnknownObjectException, IloException {
		IloNumVar[] var = parse(cplex);

		//! this lists all the variables in the program, and their values at optimality			
		double x[] = cplex.getValues(var);
		String values = ""; 
		for(int loop=0; loop<x.length; loop++ ) {
			values += x[loop] + " ";
		}
		
		return values; 
	}
	
	public boolean fractional(IloCplex cplex) throws IloException {
		double eps = 1E-5;

		IloNumVar[] var = parse(cplex);
		
		for (double val : cplex.getValues(var)) {
			if (!(Math.abs(1.0 - val) < eps || Math.abs(0.0 - val) < eps)) {
				return true;
			}
			
		}
		
		return false; 
	}
	
	



}		