package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cplexutil.ILPCreator;
import cplexutil.RunCplexPrintOutput;
import data.PhylogeneticTree;
import data.RandomTreeGenerator;
import data.RootedTriplet;
import data.TripletCorrupter;
import data.TripsReader;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import maxrtc.ILPSolver;

public class ILPTester {
	double corruptionMin; 
	double corruptionMax; 
	double corruptionStep; 
	double[] corruptionArr;
	int taxaMin; 
	int taxaMax; 
	int taxaStep; 
	int repetitions;
	boolean relax; 
	BufferedWriter writer;
	
	public ILPTester(double corruptionMin, double corruptionMax, double corruptionStep, int taxaMin, int taxaMax, int taxaStep, int repetitions, boolean relax) {
		this.corruptionMin = corruptionMin; 
		this.corruptionMax = corruptionMax; 
		this.corruptionStep = corruptionStep; 	
		this.taxaMin = taxaMin; 
		this.taxaMax = taxaMax; 
		this.taxaStep = taxaStep; 
		this.repetitions = repetitions; 
		this.relax = relax; 
		corruptionArr = new double[(int) Math.round((corruptionMax-corruptionMin)/corruptionStep)+1];
		
		double c = corruptionMin;
		for (int i = 0; i < corruptionArr.length; i++) {
			corruptionArr[i] = c;
			c += corruptionStep; 
		}
		
		try {
			writer = new BufferedWriter(new FileWriter("summaryfile2.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void finaliseFile() {
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeToFile(String filename, int val, int runLength, int total) {
		try {
			writer.write(filename + " " + val + " " + runLength + " " + total + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ILPTester() {
		this(0.0, 0.66, 0.01, 7, 15, 1, 1, false);
	}
	
	public void gridTest() throws IloException, IOException {
		RandomTreeGenerator gen = new RandomTreeGenerator(); 
		TripletCorrupter cor = new TripletCorrupter(); 
		int count = 0;
		
		double[][][] results = new double[(taxaMax-(taxaMin-1))/taxaStep][corruptionArr.length][repetitions];
		
		for (int taxa = taxaMin; taxa <= taxaMax; taxa += taxaStep) {
			System.out.println("TAXA: " + taxa);
			for (int corruption = 0; corruption < corruptionArr.length; corruption++) {
				for (int rep = 0; rep < repetitions; rep++) {
					PhylogeneticTree tree = gen.generateTree(taxa);
					ILPSolver solver = new ILPSolver(cor.corrupt(tree.findAllTriplets(), corruptionArr[corruption]));
					solver.solve(relax); 
					
					results[taxa-taxaMin][corruption][rep] = solver.getDuration();
					
				}

			}
		}
		
		try {
			printResults(results);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void gridTestFrac() throws IloException, IOException {
		RandomTreeGenerator gen = new RandomTreeGenerator(); 
		TripletCorrupter cor = new TripletCorrupter(); 
		
		
		for (int taxa = taxaMin; taxa <= taxaMax; taxa += taxaStep) {
			System.out.println("TAXA: " + taxa);
			for (int corruption = 0; corruption < corruptionArr.length; corruption++) {
				for (int rep = 0; rep < repetitions; rep++) {
					String filename = "tests/t" + taxa + "_c" + ((int) Math.round(corruptionArr[corruption]*100)) + "_" + rep;

					PhylogeneticTree tree = gen.generateTree(taxa);
					ILPSolver solver = new ILPSolver(cor.corrupt(tree.findAllTriplets(), corruptionArr[corruption]), filename+".lp", this);
					IloCplex cplex = solver.solve(relax); 
					cplex.writeSolution(filename + ".sol");

					RunCplexPrintOutput thing = new RunCplexPrintOutput(); 
					
					
					BufferedWriter writer;
				    String relaxValues = thing.getValues(cplex);
				    double objValue = cplex.getObjValue();
				    cplex.end();
				    System.gc();
//				    if (checkFractionality(relaxValues)) {
//				    	writer = new BufferedWriter(new FileWriter(filename+".frac.txt"));
//						writer.write(thing.getValues(cplex));
//					    writer.close();  
//					    writer = new BufferedWriter(new FileWriter(filename+".dur.txt"));
//					    writer.write(solver.getDuration() + " \n");
//					    writer.close();						
//						double relaxObjValue = readTester(filename+".lp");
//						if (Math.abs(relaxObjValue - objValue) < 0.001) {
//							System.out.println("we got a winner: " + filename + " with relax val: " + relaxObjValue + " and ilp val: " + objValue);
//						}
//				    }
//					System.out.println(thing.getValues(cplex));

					
					
				}

			}
		}
		
		
	}
	

	public void generateLPs() throws IloException, IOException {
		RandomTreeGenerator gen = new RandomTreeGenerator(); 
		TripletCorrupter cor = new TripletCorrupter(); 
		
		
		for (int taxa = taxaMin; taxa <= taxaMax; taxa += taxaStep) {
			System.out.println("TAXA: " + taxa);
			for (int corruption = 0; corruption < corruptionArr.length; corruption++) {
				for (int rep = 0; rep < repetitions; rep++) {
					String filename = "canontests/t" + taxa + "_c" + ((int) Math.round(corruptionArr[corruption]*100)) + "_" + rep;

					PhylogeneticTree tree = gen.generateTree(taxa);
					ILPCreator creator = new ILPCreator(cor.corrupt(tree.findAllTriplets(), corruptionArr[corruption]), filename+".lp");
					creator.writeILP(relax);

//					System.out.println(thing.getValues(cplex));

					
					
				}

			}
		}
		
		
	}
	
	public void gridTestFracUnder() throws IloException, IOException {
		RandomTreeGenerator gen = new RandomTreeGenerator(); 
		TripletCorrupter cor = new TripletCorrupter(); 
		
		
		for (int taxa = taxaMin; taxa <= taxaMax; taxa += taxaStep) {
			System.out.println("TAXA: " + taxa);
			for (int corruption = 0; corruption < corruptionArr.length; corruption++) {
				for (int rep = 0; rep < repetitions; rep++) {
					String filename = "tests/t" + taxa + "_c" + ((int) Math.round(corruptionArr[corruption]*100)) + "_" + rep;

					PhylogeneticTree tree = gen.generateTree(taxa);
					List<RootedTriplet> triplets = tree.findAllTriplets(); 
					Collections.shuffle(triplets);
				    triplets.subList(0, triplets.size()/2).clear();

					ILPSolver solver = new ILPSolver(cor.corrupt(triplets, corruptionArr[corruption]), filename+".lp", this);
					IloCplex cplex = solver.solve(relax); 
					cplex.writeSolution(filename + ".sol");

					RunCplexPrintOutput thing = new RunCplexPrintOutput(); 
					
					
				    BufferedWriter writer;
				    String relaxValues = thing.getValues(cplex);
				    double objValue = cplex.getObjValue();
				    if (checkFractionality(relaxValues)) {
				    	writer = new BufferedWriter(new FileWriter(filename+".frac.txt"));
						writer.write(thing.getValues(cplex));
					    writer.close();  
					    writer = new BufferedWriter(new FileWriter(filename+".dur.txt"));
					    writer.write(solver.getDuration() + " \n");
					    writer.close();						
						readTester(filename+".lp");
				    }
					
					

//					System.out.println(thing.getValues(cplex));

					
					
				}

			}
		}
		
		
	}
	
	public boolean checkFractionality(String values) {
		double eps = 0.001;
		String[] lines = values.split("\\\n"); 
		
		for (String line : lines) {
			double value = Double.parseDouble(line.split("=")[1].trim());
			if (!(Math.abs(1.0 - value) < eps || Math.abs(0.0 - value) < eps)) {
				System.out.println("Fractional: " + value);
		        return true;
			}
		}
		return false;
	}
	
	
	
	public void printResults(double[][][] results) throws IOException {
	    BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt"));
	    writer.write("Taxa (min/max/step) [ROWS]: " + taxaMin + ", " + taxaMax + ", " + taxaStep + "\n");
	    writer.write("Corruption (min/max/step) [COLS]: " + corruptionMin + ", " + corruptionMax + ", " + corruptionStep + "\n");
	    writer.write("Repetitions: " + repetitions + "\n");
	    
	    for (int rep = 0; rep < results[0][0].length; rep++) {
	    	writer.write("\nRepetition " + rep + "\n");
	    	
	    	for (int i = 0; i < results.length; i++) {  // taxa
	    		for (int j = 0; j < results[0].length; j++)  {// corruption 
	    			writer.write(results[i][j][rep] + "  ");
	    		}
	    		writer.write("\n");
	    	}
	    }

	     
	    writer.close();    
	}
	
	
//	public static void main(String[] args) throws IloException {
////		ILPTester tester = new ILPTester(0.0, 0.66, 0.02, 7, 10, 1, 10);
//		ILPTester tester = new ILPTester(0, 0.66, 0.02, 6, 11, 1, 10, false); 
//		tester.generateLPs();
//	}
	
	
	public double readTester(String filename) throws IOException, IloException {
		BufferedReader reader = new BufferedReader(new FileReader(filename)); 
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
//		System.out.println("#triplets: "  + triplets.size() + " 1/3: " + triplets.size()/3);
		
		ILPSolver solver = new ILPSolver(triplets, filename.split("\\.")[0]+"ilp.lp", this);
		IloCplex cplex = solver.solve(false); 
//		System.out.println("final obj val: " + cplex.getObjValue() + " duration: " + solver.getDuration());
//		cplex.writeSolution(filename.split("\\.")[0]+"ilp.sol");
		return cplex.getObjValue();
		
	}
	
	public double readTesterFrac(String filename) throws IOException, IloException {
		Random rand = new Random();
		BufferedReader reader = new BufferedReader(new FileReader(filename)); 
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
		
		List<RootedTriplet> fixTriplet = new ArrayList<>(); 
		fixTriplet.add(triplets.get(rand.nextInt(triplets.size())));
		fixTriplet.add(triplets.get(rand.nextInt(triplets.size())));

		
		ILPSolver solver = new ILPSolver(triplets, filename.split("\\.")[0]+".lp", this, fixTriplet);
		IloCplex cplex = solver.solve(true); 
		cplex.writeSolution(filename.split("\\.")[0]+"fix.sol");
		return cplex.getObjValue();
		
	}
	
	public void tripletsTestFrac(List<RootedTriplet> triplets, String filename) throws IloException, IOException {
		System.out.println("Creating & Solving LP-relaxation: " + filename.split("\\.")[0]+".lp" + " ...");
		ILPSolver solver = new ILPSolver(triplets, filename.split("\\.")[0]+".lp", this);
		IloCplex cplex = solver.solve(true); 
		System.out.println("Writing solution...");
		cplex.writeSolution(filename.split("\\.")[0]+".sol");
		cplex.end(); 
		System.gc();
	}
	
//	public static void main(String[] args) throws IloException {
////		6,7;1 + t1,8;6 + t6,9;1 + t7,9;6 + t6,9;8 + t6,10;1 + t6,10;7 + t6,10;8 + t1,7;8 + t7,8;6 + t8,9;7 + t7,8;10 + 
////		t1,9;7 + t7,10;1 + t8,9;1 + t8,10;1 + t9,10;1 + t6,10;9 + t9,10;7 + t9,10;8
//
//		List<RootedTriplet> triplets = Arrays.asList(new RootedTriplet(6,7,1), new RootedTriplet(1,8,6), new RootedTriplet(6,9,1), new RootedTriplet(7,9,6), 
//				new RootedTriplet(6,9,8), new RootedTriplet(6,10,1), new RootedTriplet(6,10,7), new RootedTriplet(6,10,8), new RootedTriplet(1,7,8), new RootedTriplet(7,8,6), 
//				new RootedTriplet(8,9,7), new RootedTriplet(7,8,10), new RootedTriplet(1,9,7), new RootedTriplet(7,10,1), new RootedTriplet(8,9,1), 
//				new RootedTriplet(8,10,1), new RootedTriplet(9,10,1), new RootedTriplet(6,10,9), new RootedTriplet(9,10,7), new RootedTriplet(9,10,8));
//		
//		ILPSolver solver = new ILPSolver(triplets, "test.ilp");
//		IloCplex cplex = solver.solve(false);
//		cplex.writeSolution("test.sol");
//
//	}
	
//	public static void main(String[] args) throws IOException, IloException {
//				ILPTester tester = new ILPTester(0, 0.2, 0.02, 6, 6, 1, 1000, true); 

//		String filename = "threetrees2.trips"; 
//		
//		System.out.println("Reading triplets...");
//		TripsReader tripsReader = new TripsReader(filename); 
//		List<RootedTriplet> triplets = tripsReader.getTriplets();
//		tripsReader.writeConversionMap();
//		
//		tester.tripletsTestFrac(triplets, filename);
//	}
	
	
//	public static void main(String[] args) throws IOException, IloException {
//		ILPTester tester = new ILPTester(0, 0.2, 0.02, 6, 6, 1, 1000, true); 
//		
//		File folder = new File("20pairs/pairs2/a");
//		File[] listOfFiles = folder.listFiles();
//
//		for (int i = listOfFiles.length-1; i >= 0; i--) {
//			File file = listOfFiles[i];
//		    if (file.isFile()) {
//		        System.out.println(file.getName());
//		        System.out.println("Reading triplets...");
//				TripsReader tripsReader = new TripsReader("20pairs/pairs2/"+ file.getName()); 
//				List<RootedTriplet> triplets = tripsReader.getTriplets();
//				tripsReader.writeConversionMap();
//				
//				tester.tripletsTestFrac(triplets, "20pairs/pairs2/"+ file.getName());
//		    }
//		}
//	}
	
	
//	
//	public static void main(String[] args) throws IOException, IloException {
//		ILPTester tester = new ILPTester();
//
//		for (int taxa = 10; taxa <= 11; taxa++) {
//			System.out.println(taxa);
//			for (double corr = 0.00; corr <= 0.67; corr += 0.02) {
//				for (int rep = 0; rep <= 9; rep++) {
//					String filename = "newtreetests/t"+ taxa +"_c" + Math.round(corr*100) + "_" + rep +".lp";
//					double val = tester.readTester(filename);
//
//				}
//			}
//		}
//		
//		tester.finaliseFile();
//
//		
//	}
	
//	public static void main(String[] args) throws IloException, IOException {
//		ILPTester tester = new ILPTester(0, 0.66, 0.02, 6, 11, 1, 10, true);
//		tester.generateLPs();
//	}
	
//	public static void main(String[] args) throws IOException, IloException {
//		ILPTester tester = new ILPTester();
//
//		for (int taxa = 11; taxa <= 11; taxa++) {
//			System.out.println(taxa);
//			for (double corr = 0.20; corr <= 0.67; corr += 0.02) {
//				for (int rep = 0; rep <= 9; rep++) {
//					String filename = "canontests/t"+ taxa +"_c" + Math.round(corr*100) + "_" + rep +".lp";
//					double val = tester.readTesterFrac(filename);
//
//				}
//			}
//		}
//		
////		tester.finaliseFile();
//	}
	
	
//	public static void main(String[] args) throws IOException, IloException {
//		ILPTester tester = new ILPTester(); 
//		double val = tester.readTester("canontests/t10_c10_4.lp");
//		System.out.println(val);
//	}
	
	public boolean isFractional(String values) {
		double eps = 1E-5;
		for (String valString : values.split(" ")) {
			
			double val = Double.parseDouble(valString.trim()); 
			if (!(Math.abs(1.0 - val) < eps || Math.abs(0.0 - val) < eps)) {
				return true;
			}
			
		}
		
		return false; 
	}
	
//	public static void main(String[] args) throws IOException, IloException {
//		ILPTester tester = new ILPTester(); 
//		RunCplexPrintOutput thing = new RunCplexPrintOutput();
//		
//		int numInstances = 0; 
//		int numIntegral = 0; 
//		for (int taxa = 6; taxa <= 7; taxa++) {
//			System.out.println(taxa);
//			for (double corr = 0.00; corr <= 0.67; corr += 0.02) {
//				for (int rep = 0; rep <= 9; rep++) {
//					String filename = "canontests/t"+ taxa +"_c" + Math.round(corr*100) + "_" + rep +".lp";
//						Random rand = new Random();
//						BufferedReader reader = new BufferedReader(new FileReader(filename)); 
//						reader.readLine(); 
//						String obj = reader.readLine(); 
//						obj = obj.substring(6, obj.length());
//						String[] tripletStrings = obj.split("\\+");
//						List<RootedTriplet> triplets = new ArrayList<>(); 
//						for (String t : tripletStrings) {
//							t = t.trim();
//							t = t.substring(1, t.length()); 
//							int a = Integer.parseInt(t.split(",")[0]);
//							int b = Integer.parseInt(t.split(",")[1].split(";")[0]);
//							int c = Integer.parseInt(t.split(";")[1].trim());
//							triplets.add(new RootedTriplet(a, b, c)); 
//						}
//						
//						
//						List<RootedTriplet> fixTriplet = new ArrayList<>(); 
//						fixTriplet.add(triplets.get(rand.nextInt(triplets.size())));
//						fixTriplet.add(triplets.get(rand.nextInt(triplets.size())));
//						fixTriplet.add(triplets.get(rand.nextInt(triplets.size())));
//						fixTriplet.add(triplets.get(rand.nextInt(triplets.size())));
//
//					
//						
//						ILPSolver solver = new ILPSolver(triplets, filename.split("\\.")[0]+".lp", tester, fixTriplet);
//						IloCplex cplex = solver.solve(true); 
//						String values = thing.getOnlyValues(cplex);
//						
//						numInstances++; 
//						if (!tester.isFractional(values)) {
//							System.out.println(filename + " integral");
//							numIntegral++; 
//						}
//						else {
//							System.out.println(filename + " fractional");
//						}
//						cplex.writeSolution(filename.split("\\.")[0]+"fix.sol");
//
//				}
//			}
//		}
//		
//		System.out.println(numIntegral + " / " + numInstances + " integral");
//	
//		
//	}
//	
//	public static void main(String[] args) throws IloException, IOException {
//		ILPTester tester = new ILPTester(); 
//		RunCplexPrintOutput thing = new RunCplexPrintOutput();
//		String filename = "canontests/t6_c56_5.lp";
//		Random rand = new Random();
//		BufferedReader reader = new BufferedReader(new FileReader(filename)); 
//		reader.readLine(); 
//		String obj = reader.readLine(); 
//		obj = obj.substring(6, obj.length());
//		String[] tripletStrings = obj.split("\\+");
//		List<RootedTriplet> triplets = new ArrayList<>(); 
//		for (String t : tripletStrings) {
//			t = t.trim();
//			t = t.substring(1, t.length()); 
//			int a = Integer.parseInt(t.split(",")[0]);
//			int b = Integer.parseInt(t.split(",")[1].split(";")[0]);
//			int c = Integer.parseInt(t.split(";")[1].trim());
//			triplets.add(new RootedTriplet(a, b, c)); 
//		}
//		
//		
//		
//		for (int fixindex = 0; fixindex < triplets.size()-1; fixindex++) {
//			for (int fixindex2 = fixindex+1; fixindex2 < triplets.size(); fixindex2++) {
//				for (int fixindex3 = fixindex+2; fixindex3 < triplets.size(); fixindex3++) {
//					List<RootedTriplet> fixTriplet = new ArrayList<>(); 
//					fixTriplet.add(triplets.get(fixindex));
//					fixTriplet.add(triplets.get(fixindex2));
//					fixTriplet.add(triplets.get(fixindex3));
//					int numIntegral = 0;
//					
//					for (int rep = 0; rep < 2; rep++) {
//			
//					
//						
//						ILPSolver solver = new ILPSolver(triplets, filename.split("\\.")[0]+".lp", tester, fixTriplet);
//						IloCplex cplex = solver.solve(true); 
//						String values = thing.getOnlyValues(cplex);
//						
//						if (!tester.isFractional(values)) {
//		//					System.out.println(filename + " integral");
//							numIntegral++;
//						}
//					}
//					
//					if (numIntegral > 0) {
//						System.out.println("Triplet: " + triplets.get(fixindex).toString() + ", " + triplets.get(fixindex2).toString() + ", "  + triplets.get(fixindex3).toString() + ", "+ numIntegral + " / 3 integral");
//					}
//				}
//			}
//		}
////		cplex.writeSolution(filename.split("\\.")[0]+"fix.sol");
//	}
	
	
	
	
	
	public static int solveInstance(String filename) throws IloException, IOException {
		ILPTester tester = new ILPTester(); 
		RunCplexPrintOutput thing = new RunCplexPrintOutput();
	
		BufferedReader reader = new BufferedReader(new FileReader(filename)); 
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
		
		reader.close();
		List<RootedTriplet> fixTriplet = new ArrayList<>(); 
		
		
		
	
		
		ILPSolver solver = new ILPSolver(triplets, filename.split("\\.")[0]+".lp", tester, fixTriplet);
		IloCplex cplex = solver.solve(true); 
//		String values = thing.getOnlyValues(cplex);
		
		
		
		if (!thing.fractional(cplex)) {
			System.out.println(filename + " integral solution lp-relax");
			cplex.end();
			System.gc();
			return 0;
		}
		else {
			boolean solutionFound = false; 
			cplex.end();
			System.gc();
			for (int i = 0; i < triplets.size(); i++) {
				fixTriplet = new ArrayList<>(); 
				fixTriplet.add(triplets.get(i));
				
				solver = new ILPSolver(triplets, filename.split("\\.")[0]+".lp", tester, fixTriplet);
				cplex = solver.solve(true); 
//				values = thing.getOnlyValues(cplex);
				
				if (!thing.fractional(cplex)) {
					System.out.println(filename + " integral solution 1 var fixed: " + triplets.get(i).toString());
					solutionFound = true; 
					cplex.writeSolution(filename + "fix");
					cplex.end();
					System.gc();
					return 1; 
				}
			}
			
			if (!solutionFound) {
				cplex.end();
				System.gc();
				for (int i = 0; i < triplets.size()-1; i++) {
					for (int j = i+1; j < triplets.size(); j++) {
						fixTriplet = new ArrayList<>(); 
						fixTriplet.add(triplets.get(i));
						fixTriplet.add(triplets.get(j));
						
						solver = new ILPSolver(triplets, filename.split("\\.")[0]+".lp", tester, fixTriplet);
						cplex = solver.solve(true); 
						
						if (!thing.fractional(cplex)) {
							System.out.println(filename + " integral solution 2 var fixed: " + triplets.get(i).toString() + ", " + triplets.get(j).toString());
							solutionFound = true; 
							cplex.writeSolution(filename + "fix");

							cplex.end();
							System.gc();
							return 2;
						}
					}
				}
			}
			
//			if (!solutionFound) {
//				for (int i = 0; i < triplets.size()-1; i++) {
//					for (int j = i+1; j < triplets.size(); j++) {
//						for (int k = j+1; k < triplets.size(); k++) {
//							fixTriplet = new ArrayList<>(); 
//							fixTriplet.add(triplets.get(i));
//							fixTriplet.add(triplets.get(j));
//							fixTriplet.add(triplets.get(k));
//							
//							solver = new ILPSolver(triplets, filename.split("\\.")[0]+".lp", tester, fixTriplet);
//							cplex = solver.solve(true); 
//							values = thing.getOnlyValues(cplex);
//							
//							if (!tester.isFractional(values)) {
//								System.out.println(filename + " integral solution 3 var fixed: " + triplets.get(i).toString() + ", " + triplets.get(j).toString() + ", " + triplets.get(k).toString());
//								solutionFound = true; 
//								cplex.end();
//								System.gc();
//								return 3;
//							}
//						}
//					}
//				}
//			}
			
				
				
			
		}
		System.out.println(filename + " , NO SOLUTION FOUND!");
		cplex.end();
		System.gc();
		return -1;
		
	}
	
//	public static void main(String[] args) throws IOException, IloException {
//		ILPTester tester = new ILPTester(); 
//		RunCplexPrintOutput thing = new RunCplexPrintOutput();
//		String filename = "canontests/t6_c58_0.lp";
//		Random rand = new Random();
//		BufferedReader reader = new BufferedReader(new FileReader(filename)); 
//		reader.readLine(); 
//		String obj = reader.readLine(); 
//		obj = obj.substring(6, obj.length());
//		String[] tripletStrings = obj.split("\\+");
//		List<RootedTriplet> triplets = new ArrayList<>(); 
//		for (String t : tripletStrings) {
//			t = t.trim();
//			t = t.substring(1, t.length()); 
//			int a = Integer.parseInt(t.split(",")[0]);
//			int b = Integer.parseInt(t.split(",")[1].split(";")[0]);
//			int c = Integer.parseInt(t.split(";")[1].trim());
//			triplets.add(new RootedTriplet(a, b, c)); 
//		}
//		
//		
//		
//		
//		List<RootedTriplet> fixTriplet = new ArrayList<>(); 
////		fixTriplet.add(new RootedTriplet(1, 10,9));
//		fixTriplet.add(new RootedTriplet(1,7,8));
//		fixTriplet.add(new RootedTriplet(3,9,10));
//
//
//				
//		
//		ILPSolver solver = new ILPSolver(triplets, filename.split("\\.")[0]+".lp", tester, fixTriplet);
//		IloCplex cplex = solver.solve(true); 
//		String values = thing.getOnlyValues(cplex);
//		System.out.println(thing.getValues(cplex));
//		System.out.println(filename + " " + tester.isFractional(values));
//					
//			
//					
//						
//						
//	}
	
//	public static void main(String[] args) throws IloException, IOException {
//		solveInstance("canontests/t6_c58_0.lp");
//	}
	
	
		
//		System.out.println("0: " + fixed[0] +", 1: " + fixed[1] +", 2: " + fixed[2] +", 3: " + fixed[3] + ", not: " + numNotSolved);

		
	
//	
//	public static void main(String[] args) throws IOException, IloException {
//		ILPTester tester = new ILPTester(); 
//		double val = tester.readTester("canontests/t6_c58_0.lp");
//		System.out.println(val);
//	}

public static void main(String[] args) throws IOException, IloException {
//	int[] fixed = new int[4];	
//	int numNotSolved = 0; 
	
	for (int taxa = 9; taxa <= 9; taxa++) {
		for (double corr = 0.50; corr <= 0.60 ; corr += 0.02) {
			for (int rep = 3; rep <= 9; rep++) {
				String filename = "canontests/t"+ taxa +"_c" + Math.round(corr*100) + "_" + rep +".lp";
				int num = solveInstance(filename);
//				if (num == -1) 
//					numNotSolved++;
//				else
//					fixed[num]++;
			}
		}
	} 
}
//	
//	public static void main(String[] args) throws IloException, IOException {
//		ILPTester tester = new ILPTester(0, 0.66, 0.02, 6, 11, 1, 10, true);
//		tester.gridTestFrac();
//	}


} 
