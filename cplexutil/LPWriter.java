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
	
	public void addObjective(boolean max, String obj) {
		this.max = max; 
		this.objective += obj;
	}
	
	public void addConstraint(String constraint) {
		constraints += " c" + (numConstraints+1) + ": " + constraint + "\n";
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
		if (max) 
			program += "Maximize \n"; 
		else 
			program += "Minimize \n"; 
		
		program += " obj: " + objective + "\n"; 
		
		// constraints 
		program += "Subject To \n";
		program += constraints; 
		
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
		    BufferedWriter writer = new BufferedWriter(new FileWriter(name));
		    writer.write(this.toString());		     
		    writer.close();		
	}
	
	public static void main(String[] args) {
		String s = "testest"; 
		System.out.println(s.substring(0, s.length()-2));
	}

}
