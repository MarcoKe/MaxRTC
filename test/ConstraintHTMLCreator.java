package test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cplexutil.LPWriter;
import data.PhylogeneticTree;
import data.RootedTriplet;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;

public class ConstraintHTMLCreator {
	List<RootedTriplet> inputTriplets; 
	List<Integer> labels; 
	String filename; 
	

	public ConstraintHTMLCreator(List<RootedTriplet> triplets, String filename) {
		this.inputTriplets = triplets; 
		Set<Integer> labels = getLabelSet(triplets); 
		this.labels = new ArrayList<>(labels); 
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
	
	public void createILP(boolean relax) throws IloException, IOException {
		writeILP(relax); 
		
	}

	
	

	// builds ilp and writes to file 
	public void writeILP(boolean relax) throws IOException {
//		LPWriter lpwriter = new LPWriter(); 

//		String obj = "";
//		String binaries = ""; 
//		
//		for (int i : labels) {
//			for (int j : labels) {
//				for (int k : labels) {
//
//					
//					if (i != j && i != k && j != k) {
////					if (i < j && i != k && j != k) {
//
//						binaries += "t"+i+","+j+";"+k + " ";
//						if (relax) {
//							lpwriter.addBound("t"+i+","+j+";"+k, 0.0, 1.0);
//						}
//					}
//					
//
//				}
//			}
//		}
//		
//		for (RootedTriplet t : inputTriplets) { 
//			obj += "t"+t.a+","+t.b+";"+t.c + " + "; 
//		}
//
//		obj = obj.substring(0, obj.length()-3);
//		lpwriter.addObjective(true, obj);
//		if (!relax)
//			lpwriter.addIntegerVars(true, binaries);  
//
//		// constraints 
//		for (int i : labels) {
//			for (int j : labels) {
//				for (int k : labels) {
//					if (!(i == j || i == k 
//							|| j == k)) {
////					if (i < j && i != k && j != k) {
//
//						lpwriter.addConstraint(1, "t"+i+","+j+";"+k + " + t" + i+","+k+";"+j + " + t" + j+","+k+";"+i + " = 1" );
//					}
//				}
//			}
//		}
		
		Map<String, Boolean> inObj = new HashMap<>();
		for (RootedTriplet t : inputTriplets) {
			inObj.put(t.a+","+t.b+"|"+t.c, true);
		}
		
		
	    BufferedWriter writer = new BufferedWriter(new FileWriter(filename+".html"));
	    writer.write("<html><head><style type='text/css'>.constraintgroup {margin-left: 200px; margin-right: 200px}  .inObj {background-color: lightblue} .groupheader {background-color: lightgreen; text-align: center}</style></head><body>");


		for (int i : labels) {
			for (int j : labels) {
				for (int k : labels) {
					for (int l : labels) {
						if (!(i == j || i == k || i == l
								|| j == k || j == l
								|| l == k)) {
//							if (i < j && i != k && j != k && i != l && j != l && k != l) {
							String constraintGroup = "<div class='constraintgroup'><div class='groupheader'> i=" +i +", j=" + j +", k=" + k + ", l=" +l +"</div><br>";
							constraintGroup += writeTriplet(i, j, k, inObj) + " + " +writeTriplet(j, k, l, inObj) + " - " + writeTriplet(i, k, l, inObj) + " <= 1 <br>";
							constraintGroup += writeTriplet(i, j, k, inObj) + " + " +writeTriplet(j, k, l, inObj) + " - " + writeTriplet(i, j, l, inObj) + " <= 1 <br>";
							constraintGroup += writeTriplet(i, j, l, inObj) + " + " +writeTriplet(j, k, l, inObj) + " - " + writeTriplet(i, k, l, inObj) + " <= 1 <br>";
							constraintGroup += writeTriplet(i, j, l, inObj) + " + " +writeTriplet(i, k, l, inObj) + " - " + writeTriplet(j, k, l, inObj) + " <= 1 <br>";
							constraintGroup += "</div> <br>";
							
							writer.write(constraintGroup);

						}
					}
				}
			}
		}   
		
		writer.write("</body></html>");
		writer.close();
	}
	
	public String writeTriplet(int a, int b, int c, Map<String, Boolean> isInObj) {
		String s = a+","+b+"|"+c; 
		String alternative = b+","+a+"|"+c; 
		if (isInObj.get(s) != null && isInObj.get(s) == true) {
			s = "<span class='inObj'>" + s + "</span>"; 
		}
		else if (isInObj.get(alternative) != null && isInObj.get(alternative) == true) {
			s = "<span class='inObj'>" + s + "</span>"; 
		}
		
		return s; 
	}
	
	public static void main(String[] args) throws IOException {
		PhylogeneticTree tree = new PhylogeneticTree();
		tree.addEdge(0, 5);
		tree.addEdge(0, 6);
		tree.addEdge(5, 1);
		tree.addEdge(5, 2);
		tree.addEdge(2, 3);
		tree.addEdge(2, 4);
		
		ConstraintHTMLCreator c = new ConstraintHTMLCreator(tree.findAllTriplets(), "constrainthtmlvis");
		c.writeILP(true);
	}
	


	
}
