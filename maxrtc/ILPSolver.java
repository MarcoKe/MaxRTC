package maxrtc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cplexutil.ILPCreator;
import cplexutil.LPWriter;
import data.PhylogeneticTree;
import data.RootedTriplet;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;

public class ILPSolver {
	List<RootedTriplet> inputTriplets; 
	List<Integer> labels; 
	double duration = 0.0;
	String filename;
	

	public ILPSolver(List<RootedTriplet> triplets, String filename) {
		this.inputTriplets = triplets; 
		Set<Integer> labels = getLabelSet(triplets); 
		this.labels = new ArrayList<>(labels); 
		this.filename = filename;	

	}
	
	public ILPSolver(List<RootedTriplet> triplets) {
		this(triplets, "ilp.lp");
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

	
	public IloCplex solve(boolean relax) throws IloException, IOException {
		ILPCreator creator = new ILPCreator(inputTriplets, filename);
		IloCplex cplex = creator.createILP(relax);
		try {
			
			
			double startTime = cplex.getCplexTime(); 
			System.out.println("-solving");
			cplex.solve(); 
			System.out.println("-done");
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
		return cplex;
	}
	
	

	
	
	public double getDuration() {
		return duration; 
	}


	public static void main(String[] args) throws IloException, IOException {
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
