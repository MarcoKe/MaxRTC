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
				System.out.println("corrupting this bitch");
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
		List<RootedTriplet> triplets = new ArrayList<>(); 
		triplets.add(new RootedTriplet(1, 2, 3)); 
		triplets.add(new RootedTriplet(2, 3, 4)); 
		triplets.add(new RootedTriplet(1, 2, 3)); 
		triplets.add(new RootedTriplet(2, 3, 4)); 
		triplets.add(new RootedTriplet(1, 2, 3)); 
		triplets.add(new RootedTriplet(2, 3, 4)); 
		triplets.add(new RootedTriplet(1, 2, 3)); 
		triplets.add(new RootedTriplet(2, 3, 4)); 
		triplets.add(new RootedTriplet(1, 2, 3)); 
		triplets.add(new RootedTriplet(2, 3, 4)); 
		triplets.add(new RootedTriplet(1, 2, 3)); 
		triplets.add(new RootedTriplet(2, 3, 4)); 
		triplets.add(new RootedTriplet(1, 2, 3)); 
		triplets.add(new RootedTriplet(2, 3, 4)); 
		triplets.add(new RootedTriplet(1, 2, 3)); 
		triplets.add(new RootedTriplet(2, 3, 4)); 
		triplets.add(new RootedTriplet(1, 2, 3)); 
		triplets.add(new RootedTriplet(2, 3, 4)); 
		triplets.add(new RootedTriplet(1, 2, 3)); 
		triplets.add(new RootedTriplet(2, 3, 4)); 
		triplets.add(new RootedTriplet(1, 2, 3)); 
		triplets.add(new RootedTriplet(2, 3, 4)); 
		triplets.add(new RootedTriplet(1, 2, 3)); 
		triplets.add(new RootedTriplet(2, 3, 4)); 
		
		TripletCorrupter c = new TripletCorrupter(); 
		List<RootedTriplet> whatup = c.corrupt(triplets, 0.1); 
		System.out.println(whatup);
	}
}
