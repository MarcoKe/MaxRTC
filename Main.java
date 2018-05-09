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
	
//	public static void main(String[] args) throws IloException {
//		RandomTreeGenerator gen = new RandomTreeGenerator(); 
//		PhylogeneticTree tree = gen.generateTree(6); 
//		List<RootedTriplet> triplets = tree.findAllTriplets();
//		TripletCorrupter corrupter = new TripletCorrupter();
//		triplets = corrupter.corrupt(triplets, 0.6);
//		ILPSolver solver = new ILPSolver(triplets);
//		IloCplex cplex = solver.solve(true);
//		RunCplexPrintOutput thing = new RunCplexPrintOutput(); 
//		System.out.println(thing.getValues(cplex));
//		
//		
//	}
	
//	public static void main(String[] args) {
//		RandomTreeGenerator gen = new RandomTreeGenerator(); 
//		
//		for (int c = 0; c <= 100; c++) {
//			System.out.println("\n________________\n" + c + "% corruption:");
//			PhylogeneticTree tree = gen.generateTree(50); 
//			List<RootedTriplet> triplets = tree.findAllTriplets(); 
//			TripletCorrupter corrupter = new TripletCorrupter(); 
//			List<RootedTriplet> corrupted = corrupter.corrupt(triplets, c/100.0);
//			
//			int top1 = 0; 
//			int top2 = 0; 
//			int top3 = 0; 
//			for (int i = 0; i < triplets.size(); i++) {
//				RootedTriplet original = triplets.get(i); 
//				RootedTriplet alt = corrupted.get(i);
//				if (alt.c == original.c) {
//					top1++; 
//				}
//				else if (alt.c == original.a) {
//					top2++; 
//				}
//				else if (alt.c == original.b) {
//					top3++; 
//				}
//			}
//			
//			double frac1 = (double)top1/triplets.size();
//			double frac2 = (double)top2/triplets.size();
//			double frac3 = (double)top3/triplets.size();
//			System.out.println(frac1 + " " + frac2 + " " + frac3);
//		}
//	}
	
//	public static void main(String[] args) {
//		RandomTreeGenerator gen = new RandomTreeGenerator();
////		int left = 0; 
////		int right = 0; 
////		
//		String sl = "["; 
//		String sr = "["; 
//
//		
//		for (int i = 0; i < 10000; i++) {
//			PhylogeneticTree tree = gen.generateTree(20);
//			int left = tree.getLeftCount(); 
//			int right = tree.getRightCount();
//			System.out.println((double) left/right);
//			sl += left + ", ";
//			sr += right + ", ";
//
//		}
//		
//		sr += "]"; 
//		sl += "]";
//		
//		System.out.println(sl);
//		System.out.println(sr);
////		System.out.println(left + " " + right);
//
//	}
	
	public static void main(String[] args) {
		PhylogeneticTree tree = new PhylogeneticTree(); 
		tree.addEdge(0, 7);
		tree.addEdge(0, 8);
		tree.addEdge(7, 1);
		tree.addEdge(7, 2);
		tree.addEdge(8, 3);
		tree.addEdge(8, 9);
		tree.addEdge(9, 10);
		tree.addEdge(9, 6);
		tree.addEdge(10, 4);
		tree.addEdge(10, 5);
		
		System.out.println(tree.findAllTriplets());
	}
}
