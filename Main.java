import java.util.ArrayList;
import java.util.List;

import cplexutil.RunCplexPrintOutput;
import data.PhylogeneticTree;
import data.RandomTreeGenerator;
import data.RootedTriplet;
import data.TripletCorrupter;
import ilog.concert.*;
import ilog.cplex.*;
import maxrtc.ILPSolver;

public class Main {
//	public static void main(String[] args) {
//		RandomTreeGenerator gen = new RandomTreeGenerator();
//		PhylogeneticTree tree = gen.generateTree(10); 
////		
////		List<RootedTriplet> triplets = tree.findAllTriplets(); 
////		triplets.remove(triplets.size()-1);
////		tree.isDense(triplets);
//		
//		int numTrials = 100; 
//		
//		double nonDenseDuration = 0.0;
//		for (int i = 0; i < numTrials; i++) {
//			List<RootedTriplet> triplets = gen.getNonDenseTreeTriplets(10);
//			ILPSolver solver = new ILPSolver(triplets);
//			solver.solve(false);
//			double dur = solver.getDuration();
//			System.out.println(dur);
//			nonDenseDuration += dur; 
//		}
//		nonDenseDuration /= numTrials;
//		
//		System.out.println("----------------------------");
//		
//		double denseDuration = 0.0; 
//		for (int i = 0; i < numTrials; i++) {
//			List<RootedTriplet> triplets = gen.getRandomTreeTriplets(10);
//			ILPSolver solver = new ILPSolver(triplets);
//			solver.solve(false);
//			double dur = solver.getDuration();
//			System.out.println(dur);
//			denseDuration += dur; 
//		}
//		denseDuration /= numTrials; 
//		
//		System.out.println("dense avg: " + denseDuration + ", non-dense avg: " + nonDenseDuration);
//
//	}
	
	public static void main(String[] args) throws IloException {
		RandomTreeGenerator gen = new RandomTreeGenerator(); 
		PhylogeneticTree tree = gen.generateTree(6); 
		List<RootedTriplet> triplets = tree.findAllTriplets();
		TripletCorrupter corrupter = new TripletCorrupter();
		triplets = corrupter.corrupt(triplets, 0.6);
		ILPSolver solver = new ILPSolver(triplets);
		IloCplex cplex = solver.solve(true);
		RunCplexPrintOutput thing = new RunCplexPrintOutput(); 
		System.out.println(thing.getValues(cplex));
		
		
	}
}
