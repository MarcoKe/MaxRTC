package cplexutil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LPWriter {
	private boolean max = true; 
	private StringBuilder objective = new StringBuilder(); 
	private StringBuilder constraints = new StringBuilder(); 
	private int numConstraints = 0; 
	private StringBuilder integerVars = new StringBuilder(); 
	private StringBuilder bounds = new StringBuilder();
	private String filename; 
	private BufferedWriter writer; 
	
	
	public LPWriter(String filename) throws IOException {
		this.filename = filename;
		this.writer = new BufferedWriter(new FileWriter(filename));
	}
	
	public void addObjective(boolean max, String obj) throws IOException {
		this.max = max; 
		this.objective.append(obj);
		
		String program = ""; 
		if (max) 
			program += "Maximize \n"; 
		else 
			program += "Minimize \n"; 
		
		program += " obj: " + objective + "\n"; 
		program += "Subject To \n";
		writer.write(program);
	}
	
	public void addConstraint(String constraint) {
		constraints.append(" c" + (numConstraints+1) + ": " + constraint + "\n");
		numConstraints++; 
	}
	
	public void addConstraint(int id, String constraint) throws IOException {
		String constraintw = " c" + id + "_" + (numConstraints+1) + ": " + constraint + "\n";
		writer.write(constraintw);
		numConstraints++; 
	}
	
	public void addBound(String var, double min, double max) {
		bounds.append(min +  " <= " + var + " <= " + max + "\n");
	}
	
	public void addIntegerVars(boolean binary, String vars) {
		if (binary) 
			integerVars.append("Binaries \n"); 
		else 
			integerVars.append("Generals \n"); 
		
		integerVars.append(" " + vars + "\n");
		
	}
	
	public String toString() {
		// objective 
		StringBuilder program = new StringBuilder(); 
//		if (max) 
//			program += "Maximize \n"; 
//		else 
//			program += "Minimize \n"; 
//		
//		program += " obj: " + objective + "\n"; 
//		
//		// constraints 
//		program += "Subject To \n";
//		program += constraints; 
//		
		// bounds 
		
		// integer vars 
		
		if (bounds.length() != 0) {
			bounds.insert(0,"Bounds \n");
			
		}
		program.append(bounds.toString());
		program.append(integerVars.toString());
		
		
		
		program.append("End");
		
		return program.toString(); 
	}
	
	public void writeFile(String name) throws IOException {
//		    BufferedWriter writer = new BufferedWriter(new FileWriter(name));
		    writer.write(this.toString());		     
		    writer.close();		
	}
	
}
