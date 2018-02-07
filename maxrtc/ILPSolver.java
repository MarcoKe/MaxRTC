package maxrtc;

import java.util.List;

import data.PhylogeneticTree;
import data.RootedTriplet;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloMPModeler;
import ilog.cplex.IloCplex;

public class ILPSolver {
	boolean tripletIndicator[][][]; 
	
	public ILPSolver(List<RootedTriplet> triplets, int numTaxa) {
		this.tripletIndicator = new boolean[numTaxa][numTaxa][numTaxa]; 
		
		for (RootedTriplet t : triplets) {
			this.tripletIndicator[t.a][t.b][t.c] = true; 
		}
		
	}
	
	
	
	public void solve() {
        try {
        	IloCplex cplex = new IloCplex();
        	
        	IloMPModeler model = cplex; 
        	int numVars = tripletIndicator.length * tripletIndicator.length * tripletIndicator.length; 
    		int lb = 0;
            int ub = 1;
            String[]    varname = {"x1", "x2", "x3"};
            
            IloIntVar[][][] x = new IloIntVar[tripletIndicator.length][tripletIndicator.length][tripletIndicator.length];
            IloLinearIntExpr obj = model.linearIntExpr();

            
            for (int i = 0; i < x.length; i++) {
            	for (int j = 0; j < x[0].length; j++) {
            		for (int k = 0; k < x[0][0].length; k++) {
            			x[i][j][k] = model.intVar(lb, ub, "t"+Integer.toString(i)+","+Integer.toString(j)+";"+Integer.toString(k)); 
            			
            			if (tripletIndicator[i][j][k])
            				obj.addTerm(1, x[i][j][k]);
            			
            			
            		}
            	}
            }
            
            model.addMaximize(obj); 

            
            // constraints 
            for (int i = 0; i < x.length; i++) {
            	for (int j = 0; j < x[0].length; j++) {
            		for (int k = 0; k < x[0][0].length; k++) {
            			if (!(i == j || i == k 
        						|| j == k)) {
            				model.addEq(model.sum(x[i][j][k], x[i][k][j], x[j][k][i]), 1);
            			}
            		}
            	}
            }
            
            for (int i = 0; i < x.length; i++) {
            	for (int j = 0; j < x[0].length; j++) {
//            		if (i == j) continue; 
            		for (int k = 0; k < x[0][0].length; k++) {
//            			if (i == k || j == k) continue; 
            			for (int l = 0; l < x.length; l++) {
//            				if (i == l || j == l || k == l) continue; 
            				if (!(i == j || i == k || i == l
            						|| j == k || j == l
            						|| l == k)) {
            					model.addLe(model.sum(x[i][j][k], x[j][k][l], model.negative(x[i][k][l])), 1);
                				model.addLe(model.sum(x[i][j][k], x[j][k][l], model.negative(x[i][j][l])), 1); 
                				model.addLe(model.sum(x[i][j][l], x[j][k][l], model.negative(x[i][k][l])), 1); 
                				model.addLe(model.sum(x[i][j][l], x[i][k][l], model.negative(x[j][k][l])), 1);
                			
            					
            				}
            			}
            		}
            	}
            }
            
            cplex.exportModel("lpex1.lp");

            
            if ( cplex.solve() ) {
//                double[][][] sol     = cplex.getValues(x);
//                double[] dj    = cplex.getReducedCosts(var[0]);
//                double[] pi    = cplex.getDuals(rng[0]);
//                double[] slack = cplex.getSlacks(rng[0]);
//
//                cplex.output().println("Solution status = " + cplex.getStatus());
//                cplex.output().println("Solution value  = " + cplex.getObjValue());
//
//                int nvars = x.length;
//                for (int j = 0; j < nvars; ++j) {
//                   cplex.output().println("Variable " + j +
//                                          ": Value = " + x[j] +
//                                          " Reduced cost = " + dj[j]);
//                }
//
//                int ncons = slack.length;
//                for (int i = 0; i < ncons; ++i) {
//                   cplex.output().println("Constraint " + i +
//                                         ": Slack = " + slack[i] +
//                                         " Pi = " + pi[i]);
//                }
             }
            
            
        	
        	
        	cplex.end(); 
        }
        
        catch (IloException exc) {
            System.err.println("Concert exception '" + exc + "' caught");
         }

	}
	
	public void populate(IloMPModeler model) {
		
       
	}
	
	public static void main(String[] args) {
		PhylogeneticTree t = new PhylogeneticTree(9);
		t.addEdge(0, 1);
		t.addEdge(0, 2);
		t.addEdge(1, 3);
		t.addEdge(1, 4);
		t.addEdge(2, 5);
		t.addEdge(2, 6);
		t.addEdge(3, 7);
		t.addEdge(3, 8);
		
		ILPSolver solver = new ILPSolver(t.findAllTriplets(), 9);
		solver.solve();
	}

}
