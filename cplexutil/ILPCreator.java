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
	String filename; 
	List<RootedTriplet> fix;
	

	public ILPCreator(List<RootedTriplet> triplets, String filename) {
		this.inputTriplets = triplets; 
		Set<Integer> labels = getLabelSet(triplets); 
		this.labels = new ArrayList<>(labels); 
		this.filename = filename;

		

	}
	
	public ILPCreator(List<RootedTriplet> triplets, String filename, List<RootedTriplet> fix) {
		this.inputTriplets = triplets; 
		Set<Integer> labels = getLabelSet(triplets); 
		this.labels = new ArrayList<>(labels); 
		this.filename = filename;
		this.fix = fix; 

		

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

	
	public String tripletString(int a, int b,int c) {
		StringBuilder s = new StringBuilder(); 
		s.append("t");
		
		if (a < b) {
			s.append(a+","+b);
		}
		else {
			s.append(b+","+a);
		}
		
		s.append(";"+c);
		
		return s.toString(); 
	}

	// builds ilp and writes to file 
	public void writeILP(boolean relax) throws IOException {
		LPWriter lpwriter = new LPWriter(filename); 

		String obj = "";
		String binaries = ""; 
		List<String> extraCons = new ArrayList<>();
		
		for (int i : labels) {
			for (int j : labels) {
				for (int k : labels) {
					if (i != j && i != k && j != k) {
						lpwriter.addBound(tripletString(i,j,k), 0.0, 1.0);
					}
					

				}
			}
		}
		
		for (RootedTriplet t : inputTriplets) { 
			obj += tripletString(t.a, t.b, t.c) + " + "; 
		}

		obj = obj.substring(0, obj.length()-3);
		lpwriter.addObjective(true, obj);
		if (!relax)
			lpwriter.addIntegerVars(true, binaries);  
		lpwriter.addIntegerVars(true, binaries);

//		lpwriter.addConstraint(10, obj + " >= " + inputTriplets.size()/3);
//		lpwriter.addConstraint(10, obj + " = " + 102);

		
		
		for (int i : labels) {
			for (int j : labels) {
				for (int k : labels) {
					if (i != j && i != k && j != k) {
						boolean special = false;
						if (fix != null) {
							for (RootedTriplet fixTriplet : fix) {
								if (fixTriplet.a == i && fixTriplet.b == j && fixTriplet.c == k) {
									special = true; 
								}
							}
						}
						if (relax && fix != null && special) {
							lpwriter.addConstraint(100, tripletString(i,j,k) + " = 1");
						}
					}
					

				}
			}
		}
		

		// constraints 
		for (int i : labels) {
			for (int j : labels) {
				for (int k : labels) {
					if (!(i == j || i == k 
							|| j == k)) {
//					if (i < j && i != k && j != k) {

						lpwriter.addConstraint(1, tripletString(i,j,k) + " + " + tripletString(i,k,j) + " + " + tripletString(j,k,i) + " = 1" );
//						lpwriter.addConstraint(1, "t"+i+","+j+";"+k + " + t" + i+","+k+";"+j + " + t" + j+","+k+";"+i + " > 0.0001" );

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
//							if (i < j && i != k && j != k && i != l && j != l && k != l) {

							lpwriter.addConstraint(2, tripletString(i,j,k) + " + " +tripletString(j,k,l) + " - " + tripletString(i,k,l) + " <= 1");
							lpwriter.addConstraint(3, tripletString(i,j,k) + " + " +tripletString(j,k,l) + " - " + tripletString(i,j,l) + " <= 1");
//							lpwriter.addConstraint(4, tripletString(i,j,l) + " + " +tripletString(j,k,l) + " - " + tripletString(i,k,l) + " <= 1");							
//							lpwriter.addConstraint(5, tripletString(i,j,l) + " + " +tripletString(i,k,l) + " - " + tripletString(j,k,l) + " <= 1");

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
	


	
}
