package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripsReader {
	private String filename; 
	private Map<String, Integer> nameToId;
	private Map<Integer, String> idToName;
	private int currentId = 0; 
	
	public TripsReader(String filename) {
		this.filename = filename; 
		this.nameToId = new HashMap<String, Integer>(); 
		this.idToName = new HashMap<Integer, String>(); 

	}
	
	public List<RootedTriplet> getTriplets() throws IOException {
		List<RootedTriplet> triplets = new ArrayList<>(); 
		
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		
		String line; 
	    while ((line = reader.readLine()) != null) {
	    	String[] leaves = line.split(" ");
	    	triplets.add(new RootedTriplet(getId(leaves[0].trim()), getId(leaves[1].trim()), getId(leaves[2].trim())));
	    }

		
		reader.close();
		
		return triplets; 
	}
	
	public int getId(String name) { 
		if (!nameToId.containsKey(name)) {
			nameToId.put(name, currentId++); 
			idToName.put(currentId, name);
		}
		
		return nameToId.get(name);
	}
	
	public void writeConversionMap() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename+".map")); 
		
		for (int key : idToName.keySet()) {
			writer.write(key + " " + idToName.get(key)+"\n");
		}
		
		writer.close();
	}

}
