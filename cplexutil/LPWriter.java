package cplexutil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LPWriter {
	private boolean max = true; 
	private String objective = ""; 
	private String constraints = ""; 
	private int numConstraints = 0; 
	private String integerVars = ""; 
	private String bounds = "";
	private String filename; 
	private BufferedWriter writer; 
	
	
	public LPWriter(String filename) throws IOException {
		this.filename = filename;
		this.writer = new BufferedWriter(new FileWriter(filename));
	}
	
	public void addObjective(boolean max, String obj) throws IOException {
		this.max = max; 
		this.objective += obj;
		
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
		constraints += " c" + (numConstraints+1) + ": " + constraint + "\n";
		numConstraints++; 
	}
	
	public void addConstraint(int id, String constraint) throws IOException {
		String constraintw = " c" + id + "_" + (numConstraints+1) + ": " + constraint + "\n";
		writer.write(constraintw);
		numConstraints++; 
	}
	
	public void addBound(String var, double min, double max) {
		bounds += min +  " <= " + var + " <= " + max + "\n";
	}
	
	public void addIntegerVars(boolean binary, String vars) {
		if (binary) 
			integerVars += "Binaries \n"; 
		else 
			integerVars += "Generals \n"; 
		
		integerVars += " " + vars + "\n";
		
	}
	
	public String toString() {
		// objective 
		String program = ""; 
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
		program += integerVars;
		if (bounds.length() != 0) {
			bounds = "Bounds \n" + bounds;
		}
		program += bounds;
		
		
		program += "End";
		
		return program; 
	}
	
	public void writeFile(String name) throws IOException {
//		    BufferedWriter writer = new BufferedWriter(new FileWriter(name));
		    writer.write(this.toString());		     
		    writer.close();		
	}
	
}
