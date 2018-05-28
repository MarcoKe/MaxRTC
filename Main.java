import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
	
//	public static void main(String[] args) {
//		RandomTreeGenerator gen = new RandomTreeGenerator();
//		TripletCorrupter corrupter = new TripletCorrupter();
//		
//		int numTrees = 5; 
//		List<RootedTriplet> triplets = new ArrayList<>(); 
//		PhylogeneticTree tree = gen.generateTree(8);
//		triplets.addAll(tree.findAllTriplets());
//
////		
////		for (int i = 0; i < numTrees; i++) {
////			triplets.addAll(corrupter.corrupt(tree.findAllTriplets(), 0.66));
////		}
//
//		for (int i = 0; i < 20; i++) {
//			RootedTriplet t = triplets.get(i); 
//			RootedTriplet t2 = new RootedTriplet(t.b, t.c, t.a); 
//			RootedTriplet t3 = new RootedTriplet(t.c, t.a, t.b); 
//			triplets.add(t2); 
//			triplets.add(t3); 
//		}
//		
//		
//		BufferedWriter writer;
//		try {
//			writer = new BufferedWriter(new FileWriter("test.trips"));
//		
//			for (RootedTriplet triplet : triplets) { 
//				writer.write(triplet.a + " " + triplet.b + " " + triplet.c +"\n");
//			}
//			writer.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//
//
//	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("canontests/t8_c30_4.lp")); 
		reader.readLine(); 
		String obj = reader.readLine(); 
		obj = obj.substring(6, obj.length());
		String[] tripletStrings = obj.split("\\+");
		List<RootedTriplet> triplets = new ArrayList<>(); 
		for (String t : tripletStrings) {
			t = t.trim();
			t = t.substring(1, t.length()); 
			int a = Integer.parseInt(t.split(",")[0]);
			int b = Integer.parseInt(t.split(",")[1].split(";")[0]);
			int c = Integer.parseInt(t.split(";")[1].trim());
			triplets.add(new RootedTriplet(a, b, c)); 
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("canontests/t8_c30_4.trips")); 
		for (RootedTriplet t : triplets) {
			writer.write(t.a + " " +t.b + " " +t.c +"\n");
		}
		
		writer.close();
	}
	
}
