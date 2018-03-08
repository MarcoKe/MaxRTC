package cplexutil;

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

public class ILPCreator {
	List<RootedTriplet> inputTriplets; 
	List<Integer> labels; 
	

	public ILPCreator(List<RootedTriplet> triplets) {
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
	
	public IloCplex createILP() throws IloException {
		writeILP(false); 
		IloCplex cplex; 
		
		cplex = new IloCplex(); 
		cplex.setOut(null); 
		cplex.setParam(IloCplex.Param.ClockType, 2);
		cplex.importModel("ilp.lp");
		
		return cplex;
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
	


	
}
