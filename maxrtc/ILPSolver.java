package maxrtc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cplexutil.LPWriter;
import data.PhylogeneticTree;
import data.RootedTriplet;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;

public class ILPSolver {
	List<RootedTriplet> inputTriplets; 
	List<Integer> labels; 
	double duration = 0.0;
	

	public ILPSolver(List<RootedTriplet> triplets) {
		this.inputTriplets = triplets; 
		Set<Integer> labels = getLabelSet(triplets); 
		this.labels = new ArrayList<>(labels); 

		

	}
	
	public Set<Integer> getLabelSet(List<RootedTriplet> triplets) {
		Set<Integer> labels = new HashSet<>(); 
		
		for (RootedTriplet t : triplets) {
			labels.add(t.a);
			labels.add(t.b);
			labels.add(t.c);
		}
		
		return labels; 
	}

	
	public List<RootedTriplet> solve(boolean relax) {
		writeILP(relax); 
		IloCplex cplex;
		
		try {
			cplex = new IloCplex();
			cplex.setOut(null);
			cplex.setParam(IloCplex.Param.ClockType, 2);
			cplex.importModel("ilp.lp");
			double startTime = cplex.getCplexTime(); 
			cplex.solve(); 
			double finishTime = cplex.getCplexTime(); 
			double duration = finishTime - startTime; 
//			cplex.writeSolution("test.sol");
//			RunCplexPrintOutput reader = new RunCplexPrintOutput(); 
			this.duration = duration;
//			return reader.getTriplets();

		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// builds ilp and writes to file 
	public void writeILP(boolean relax) {
		LPWriter lpwriter = new LPWriter(); 

		String obj = "";
		String binaries = ""; 
		
		for (int i : labels) {
			for (int j : labels) {
				for (int k : labels) {

					
					if (i != j && i != k && j != k) {
						binaries += "t"+i+j+k + " ";
						if (relax) {
							lpwriter.addBound("t"+i+j+k, 0.0, 1.0);
						}
					}
					

				}
			}
		}
		
		for (RootedTriplet t : inputTriplets) { 
			obj += "t"+t.a+t.b+t.c + " + "; 
		}

		obj = obj.substring(0, obj.length()-3);
		lpwriter.addObjective(true, obj);
		if (!relax)
			lpwriter.addIntegerVars(true, binaries);  

		// constraints 
		for (int i : labels) {
			for (int j : labels) {
				for (int k : labels) {
					if (!(i == j || i == k 
							|| j == k)) {
						lpwriter.addConstraint("t"+i+j+k + " + t" + i+k+j + " + t" + j+k+i + " = 1" );
					}
				}
			}
		}

		for (int i : labels) {
			for (int j : labels) {
				for (int k : labels) {
					for (int l : labels) {
						if (!(i == j || i == k || i == l
								|| j == k || j == l
								|| l == k)) {
							lpwriter.addConstraint("t" + i+j+k + " + t" +j+k+l + " - t" + i+k+l + " <= 1");
							lpwriter.addConstraint("t" + i+j+k + " + t" +j+k+l + " - t" + i+j+l + " <= 1");
							lpwriter.addConstraint("t" + i+j+l + " + t" +j+k+l + " - t" + i+k+l + " <= 1");
							lpwriter.addConstraint("t" + i+j+l + " + t" +i+k+l + " - t" + j+k+l + " <= 1");
						}
					}
				}
			}
		}   
		
		try {
			lpwriter.writeFile("ilp.lp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public double getDuration() {
		return duration; 
	}


	public static void main(String[] args) {
		PhylogeneticTree t = new PhylogeneticTree(new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8)));
		t.addEdge(0, 1);
		t.addEdge(0, 2);
		t.addEdge(1, 3);
		t.addEdge(1, 4);
		t.addEdge(2, 5);
		t.addEdge(2, 6);
		t.addEdge(3, 7);
		t.addEdge(3, 8);

		ILPSolver solver = new ILPSolver(t.findAllTriplets());
		solver.solve(true);
	}

}
