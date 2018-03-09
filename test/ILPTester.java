package test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import cplexutil.RunCplexPrintOutput;
import data.PhylogeneticTree;
import data.RandomTreeGenerator;
import data.TripletCorrupter;
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
	}
	
	public ILPTester() {
		this(0.0, 0.66, 0.01, 7, 15, 1, 1, false);
	}
	
	public void gridTest() throws IloException {
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
	
	public void gridTestFrac() throws IloException {
		RandomTreeGenerator gen = new RandomTreeGenerator(); 
		TripletCorrupter cor = new TripletCorrupter(); 
		
		
		for (int taxa = taxaMin; taxa <= taxaMax; taxa += taxaStep) {
			System.out.println("TAXA: " + taxa);
			for (int corruption = 0; corruption < corruptionArr.length; corruption++) {
				for (int rep = 0; rep < repetitions; rep++) {
					PhylogeneticTree tree = gen.generateTree(taxa);
					ILPSolver solver = new ILPSolver(cor.corrupt(tree.findAllTriplets(), corruptionArr[corruption]));
					IloCplex cplex = solver.solve(relax); 
					RunCplexPrintOutput thing = new RunCplexPrintOutput(); 
					
				    BufferedWriter writer;
					try {
						String filename = "tests/t" + taxa + "_c" + ((int) Math.round(corruptionArr[corruption]*100)) + "_" + rep;
						writer = new BufferedWriter(new FileWriter(filename+".frac.txt"));
						writer.write(thing.getValues(cplex));
					    writer.close();  
					    writer = new BufferedWriter(new FileWriter(filename+".dur.txt"));
					    writer.write(solver.getDuration() + " \n");
					    writer.close();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					System.out.println(thing.getValues(cplex));

					
					
				}

			}
		}
		
		
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
	
	
	public static void main(String[] args) throws IloException {
//		ILPTester tester = new ILPTester(0.0, 0.66, 0.02, 7, 10, 1, 10);
		ILPTester tester = new ILPTester(0.0, 0.6, 0.1, 6, 11, 1, 10, true); 
		tester.gridTestFrac();
	}

}
