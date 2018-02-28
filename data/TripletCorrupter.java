package data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TripletCorrupter {

	public List<RootedTriplet> corrupt(List<RootedTriplet> triplets, double corruptionRate) {
		List<RootedTriplet> corruptedTriplets = new ArrayList<>(); 
		Random random = new Random(); 
		
		for (RootedTriplet t : triplets) {
			if (random.nextFloat() < corruptionRate) {
				RootedTriplet newTriplet; 
				
				if (random.nextFloat() <= 0.5) {
					newTriplet = new RootedTriplet(t.a, t.c, t.b);
				}
				else {
					newTriplet = new RootedTriplet(t.c, t.b, t.a); 
				}
				
				corruptedTriplets.add(newTriplet); 
			}
			else {
				corruptedTriplets.add(t); 
			}
		}
		return corruptedTriplets;
		
	}
	
	public static void main(String[] args) {
		RandomTreeGenerator gen = new RandomTreeGenerator(); 
		List<RootedTriplet> triplets = gen.generateTree(10).findAllTriplets();
		
		TripletCorrupter c = new TripletCorrupter(); 
		List<RootedTriplet> corrupted = c.corrupt(triplets, 0.1); 
		System.out.println(corrupted);
	}
}
