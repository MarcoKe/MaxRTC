package test;

import data.PhylogeneticTree;
import data.RandomTreeGenerator;
import maxrtc.ILPSolver;

public class ILPTester {
	double corruptionMin; 
	double corruptionMax; 
	double corruptionStep; 
	int taxaMin; 
	int taxaMax; 
	int taxaStep; 
	int repetitions;
	
	public ILPTester(double corruptionMin, double corruptionMax, double corruptionStep, int taxaMin, int taxaMax, int taxaStep, int repetitions) {
		this.corruptionMin = corruptionMin; 
		this.corruptionMax = corruptionMax; 
		this.corruptionStep = corruptionStep; 	
		this.taxaMin = taxaMin; 
		this.taxaMax = taxaMax; 
		this.taxaStep = taxaStep; 
		this.repetitions = repetitions; 
	}
	
	public ILPTester() {
		this(0.0, 0.66, 0.01, 7, 15, 1, 1);
	}
	
	public void gridTest() {
		RandomTreeGenerator gen = new RandomTreeGenerator(); 
		int count = 0;
		
		for (int taxa = taxaMin; taxa <= taxaMax; taxa += taxaStep) {
			for (double corruption = corruptionMin; corruption <= corruptionMax; corruption += corruptionStep) {
				for (int rep = 0; rep < repetitions; rep++) {
//					PhylogeneticTree tree = gen.generateTree(taxa);
//					ILPSolver solver = new ILPSolver(tree.findAllTriplets());
//					solver.solve(); 
//					solver.getDuration();
					System.out.println(taxa + " " + corruption + "  "  + rep);
					count++;
				}
			}
		}
		
		System.out.println(count);
	}
	
	public static void main(String[] args) {
		ILPTester tester = new ILPTester(0.0, 0.3, 0.01, 10, 10, 1, 1);
		tester.gridTest();
	}

}
