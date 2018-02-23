package maxrtc;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import cplexutil.LPWriter;
import data.PhylogeneticTree;
import data.RootedTriplet;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;

public class ILPSolver {
	boolean tripletIndicator[][][]; 
	int numTaxa; 

	public ILPSolver(List<RootedTriplet> triplets, int numTaxa) {
		this.tripletIndicator = new boolean[numTaxa][numTaxa][numTaxa]; 
		this.numTaxa = numTaxa; 

		for (RootedTriplet t : triplets) {
			this.tripletIndicator[t.a][t.b][t.c] = true; 
		}

	}

	
	public void solve() {
		writeILP(); 
		IloCplex cplex;
		try {
			cplex = new IloCplex();
			cplex.importModel("ilp.lp");
			cplex.solve(); 
			cplex.writeSolution("test.sol");

		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// builds ilp and writes to file 
	public void writeILP() {
		LPWriter lpwriter = new LPWriter(); 

		String obj = "";
		String binaries = ""; 
		for (int i = 0; i < numTaxa; i++) {
			for (int j = 0; j < numTaxa; j++) {
				for (int k = 0; k < numTaxa; k++) {

					if (tripletIndicator[i][j][k]) {
						obj += "t"+i+j+k + " + ";
						binaries += "t"+i+j+k + " ";
					}

				}
			}
		}

		obj = obj.substring(0, obj.length()-3);
		lpwriter.addObjective(true, obj);
		lpwriter.addIntegerVars(true, binaries);  

		// constraints 
		for (int i = 0; i < numTaxa; i++) {
			for (int j = 0; j < numTaxa; j++) {
				for (int k = 0; k < numTaxa; k++) {
					if (!(i == j || i == k 
							|| j == k)) {
						lpwriter.addConstraint("t"+i+j+k + " + t" + i+k+j + " + t" + j+k+i + " = 1" );
					}
				}
			}
		}

		for (int i = 0; i < numTaxa; i++) {
			for (int j = 0; j < numTaxa; j++) {
				for (int k = 0; k < numTaxa; k++) {
					for (int l = 0; l < numTaxa; l++) {
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

		ILPSolver solver = new ILPSolver(t.findAllTriplets(), 9);
		solver.solve();
	}

}
