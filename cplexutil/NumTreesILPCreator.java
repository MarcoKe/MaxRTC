package cplexutil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cplexutil.LPWriter;
import data.PhylogeneticTree;
import data.RandomTreeGenerator;
import data.RootedTriplet;
import data.TripletCorrupter;
import data.TripsReader;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;

public class NumTreesILPCreator {
	List<RootedTriplet> inputTriplets; 
	List<Integer> labels; 
	String filename; 
	private int numTrees; 
	

	public NumTreesILPCreator(List<RootedTriplet> triplets, int numTrees, String filename) {
		this.inputTriplets = triplets; 
		Set<Integer> labels = getLabelSet(triplets); 
		this.labels = new ArrayList<>(labels); 
		this.numTrees = numTrees; 
		this.filename = filename;

		

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
	
	public IloCplex createILP(boolean relax) throws IloException, IOException {
//		System.out.println("-creating lp");
		writeILP(relax); 
//		System.out.println("-done");
		IloCplex cplex; 
		
		cplex = new IloCplex(); 
		cplex.setOut(null); 
		cplex.setParam(IloCplex.Param.ClockType, 2);
//		System.out.println("-importing lp");
		cplex.importModel(filename);
//		System.out.println("-done");
		
		return cplex;
	}

	
	public String tripletString(int a, int b,int c, int tree) {
		String s = "t"; 
		
		if (a < b) {
			s += a+","+b;
		}
		else {
			s += b+","+a;
		}
		
		s+= ";"+c;
		s+= "_"+tree;
		
		return s; 
	}

	// builds ilp and writes to file 
	public void writeILP(boolean relax) throws IOException {
		LPWriter lpwriter = new LPWriter(filename); 

		String binaries = ""; 
		
		
		
		
		for (int tr = 0; tr < numTrees; tr++) {
			for (int i : labels) {
				for (int j : labels) {
					for (int k : labels) {
	
						
						if (i != j && i != k && j != k) {
	//					if (i < j && i != k && j != k) {
	
							binaries += tripletString(i,j,k, tr) + " \n";
							if (relax) {
								lpwriter.addBound(tripletString(i,j,k, tr), 0.0, 1.0);
							}
						}
						
	
					}
				}
			}
		}
		
		lpwriter.addObjective(true, "0");
		if (!relax)
			lpwriter.addIntegerVars(true, binaries);  
		
		
		for (RootedTriplet t : inputTriplets) { 
			String constraintString = ""; 
			for (int tr = 0; tr < numTrees; tr++) {
				constraintString += tripletString(t.a, t.b, t.c, tr) + " + ";
			}
			constraintString = constraintString.substring(0, constraintString.length()-3);
			lpwriter.addConstraint(0, constraintString + " >= 1");
		}

		// constraints 
		for (int tr = 0; tr < numTrees; tr++) {
			for (int i : labels) {
				for (int j : labels) {
					for (int k : labels) {
						if (!(i == j || i == k 
								|| j == k)) {
	//					if (i < j && i != k && j != k) {
	
							lpwriter.addConstraint(1, tripletString(i,j,k, tr) + " + " + tripletString(i,k,j, tr) + " + " + tripletString(j,k,i, tr) + " = 1" );
	//						lpwriter.addConstraint(1, "t"+i+","+j+";"+k + " + t" + i+","+k+";"+j + " + t" + j+","+k+";"+i + " > 0.0001" );
	
						}
					}
				}
			}
		}
		
		
		for (int tr = 0; tr < numTrees; tr++) {
			for (int i : labels) {
				for (int j : labels) {
					for (int k : labels) {
						for (int l : labels) {
							if (!(i == j || i == k || i == l
									|| j == k || j == l
									|| l == k)) {
	//							if (i < j && i != k && j != k && i != l && j != l && k != l) {
	
								lpwriter.addConstraint(2, tripletString(i,j,k, tr) + " + " +tripletString(j,k,l, tr) + " - " + tripletString(i,k,l, tr) + " <= 1");
								lpwriter.addConstraint(3, tripletString(i,j,k, tr) + " + " +tripletString(j,k,l, tr) + " - " + tripletString(i,j,l, tr) + " <= 1");
								lpwriter.addConstraint(4, tripletString(i,j,k, tr) + " + " +tripletString(k,l,i,tr) + " <= 1");
	
							}
						}
					}
				}
			}   
		}
		
		try {
			lpwriter.writeFile(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
//	public static void main(String[] args) throws IloException, IOException {
//		for (int numTrees = 3; numTrees <= 3; numTrees++) {
//			for (int numTaxa = 11; numTaxa <= 11; numTaxa++) {
//				System.out.println("# taxa: " + numTaxa);
//				for (double corr = 0.00; corr < 0.66; corr+= 0.02) {
//					RandomTreeGenerator treegen = new RandomTreeGenerator(); 
//					PhylogeneticTree tree = treegen.generateTree(numTaxa); 
//					TripletCorrupter corrupter = new TripletCorrupter();
//					List<RootedTriplet> corruptedTriplets = corrupter.corruptNonMinimal(tree.findAllTriplets(), corr);
//					NumTreesILPCreator ilpcreator = new NumTreesILPCreator(corruptedTriplets, numTrees, "test");
//					IloCplex cplex = ilpcreator.createILP(false);
//					if (!cplex.solve()) {
//						System.out.println("issue! numtrees: " + numTrees + " , numtaxa: " + numTaxa + " , corruption: " + corr);
//					}
//				}
//			}
//		}
//		
//		RandomTreeGenerator treegen = new RandomTreeGenerator(); 
//		PhylogeneticTree tree = treegen.generateTree(11); 
//		TripletCorrupter corrupter = new TripletCorrupter();
//		List<RootedTriplet> corruptedTriplets = corrupter.corrupt(tree.findAllTriplets(), 0.6);
//		NumTreesILPCreator ilpcreator = new NumTreesILPCreator(corruptedTriplets, 3, "test");
//		IloCplex cplex = ilpcreator.createILP(false);
//		if (!cplex.solve()) {
//			System.out.println("issue");
//		}
		
		
		
//	}
//	
//	public static void main(String[] args) throws IloException, IOException {
//		RandomTreeGenerator treegen = new RandomTreeGenerator(); 
//		int NUMTREES = 5;
//		List<RootedTriplet> triplets = new ArrayList<>();
//		
//		for (int numTrees = 1; numTrees <= NUMTREES; numTrees++) {
//			PhylogeneticTree tree = treegen.generateTree(6);
//			triplets.addAll(tree.findAllTriplets());
//		}
//		
//		NumTreesILPCreator ilpcreator = new NumTreesILPCreator(triplets, 3, "test");
//		IloCplex cplex = ilpcreator.createILP(false); 
//		if (!cplex.solve()) {
//			System.out.println("issue");
//		}
//	}
	
//	public static void main(String[] args) throws IOException {
//		for (int numTaxa = 6; numTaxa <= 11; numTaxa++) {
//			System.out.println("# taxa: " + numTaxa);
//			for (double corr = 0.00; corr < 0.66; corr+= 0.02) {
//				
//				RandomTreeGenerator treegen = new RandomTreeGenerator(); 
//				PhylogeneticTree tree = treegen.generateTree(numTaxa); 
//				TripletCorrupter corrupter = new TripletCorrupter();
//				List<RootedTriplet> corruptedTriplets = corrupter.corruptNonMinimal(tree.findAllTriplets(), corr);
//				
//				BufferedWriter writer = new BufferedWriter(new FileWriter("t"+numTaxa+"c"+Math.round(corr*100)+".trips"));
//				for (RootedTriplet triplet : corruptedTriplets) { 
//					writer.write(triplet.a + " " + triplet.b + " " + triplet.c +"\n");
//				}
//			}
//		}
//	}
//	
	
//	public static void main(String[] args) throws IOException {
//		RandomTreeGenerator treegen = new RandomTreeGenerator(); 
//		PhylogeneticTree tree = treegen.generateTree(10); 
//		TripletCorrupter corrupter = new TripletCorrupter();
//		List<RootedTriplet> corruptedTriplets = tree.findAllTriplets();
//		
//		BufferedWriter writer = new BufferedWriter(new FileWriter("clean.trips"));
//		for (RootedTriplet triplet : corruptedTriplets) { 
//			System.out.println(triplet.a + " " + triplet.b + " " + triplet.c +"\n");
//			writer.write(triplet.a + " " + triplet.b + " " + triplet.c +"\n");
//		}
//		
//		writer.close();
//		
//		TripsReader tripsreader = new TripsReader("clean.trips");
//		tripsreader.write(tripsreader.getTriplets());
//	}
	
	public static void main(String[] args) throws IOException {
		for (int numTaxa = 6; numTaxa <= 11; numTaxa++) {
			System.out.println("# taxa: " + numTaxa);
			for (double corr = 0.00; corr < 0.66; corr+= 0.02) {
				RandomTreeGenerator treegen = new RandomTreeGenerator(); 
				PhylogeneticTree tree = treegen.generateTree(numTaxa); 
				TripletCorrupter corrupter = new TripletCorrupter();
				List<RootedTriplet> corruptedTriplets = corrupter.corruptNonMinimal(tree.findAllTriplets(), corr);
				
				BufferedWriter writer = new BufferedWriter(new FileWriter("t"+numTaxa+"c"+Math.round(corr*100)+".trips"));
				for (RootedTriplet triplet : corruptedTriplets) { 
					writer.write(triplet.a + " " + triplet.b + " " + triplet.c +"\n");
				}
				writer.close();
				TripsReader tripsreader = new TripsReader("t"+numTaxa+"c"+Math.round(corr*100)+".trips");
				tripsreader.write(tripsreader.getTriplets());
			}
		}
	}


	
}
