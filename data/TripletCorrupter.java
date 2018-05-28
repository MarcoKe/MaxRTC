package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import maxrtc.ILPSolver;

public class TripletCorrupter {

	public List<RootedTriplet> corrupt2(List<RootedTriplet> triplets, double corruptionRate) {
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
	
	public List<RootedTriplet> corrupt(List<RootedTriplet> triplets, double corruptionRate) {
		List<RootedTriplet> corruptedTriplets = new ArrayList<>(triplets); 
		Random random = new Random(); 		
		List<Integer> indices = selectRandomIndices(triplets.size(), corruptionRate); 
		
		for (int index : indices) {
			RootedTriplet t = corruptedTriplets.get(index);
			RootedTriplet newTriplet; 
			
			if (random.nextFloat() <= 0.5) {
				newTriplet = new RootedTriplet(t.a, t.c, t.b);
			}
			else {
				newTriplet = new RootedTriplet(t.c, t.b, t.a); 
			}
			
			corruptedTriplets.set(index, newTriplet);			
			
		}
		
		return corruptedTriplets;	
	}
	
	// returns a dense set instead of a minimally dense set 
	public List<RootedTriplet> corruptNonMinimal(List<RootedTriplet> triplets, double corruptionRate) {
		List<RootedTriplet> corruptedTriplets = new ArrayList<>(triplets); 
		Random random = new Random(); 		
		List<Integer> indices = selectRandomIndices(triplets.size(), corruptionRate); 
		
		for (int index : indices) {
			RootedTriplet t = corruptedTriplets.get(index);
			RootedTriplet newTriplet; 
			
			if (random.nextFloat() <= 0.5) {
				newTriplet = new RootedTriplet(t.a, t.c, t.b);
			}
			else {
				newTriplet = new RootedTriplet(t.c, t.b, t.a); 
			}
			
			corruptedTriplets.add(newTriplet);			
			
		}
		
		return corruptedTriplets;	
	}
	
	public List<Integer> selectRandomIndices(int size, double fraction) {
		List<Integer> indices = new ArrayList<>(); 
		for (int i = 0; i < size; i++) {
			indices.add(i); 
		}
		
		Collections.shuffle(indices);
		int numElements = (int) Math.round(size*fraction);
		
		return indices.subList(0, numElements); 
	}
	
	public static void main(String[] args) {
		RandomTreeGenerator gen = new RandomTreeGenerator(); 
		List<RootedTriplet> triplets = gen.generateTree(10).findAllTriplets();
		
		TripletCorrupter c = new TripletCorrupter(); 
		List<RootedTriplet> corrupted = c.corrupt2(triplets, 0.5); 
//		ILPSolver solver = new ILPSolver(corrupted);
//		solver.solve();
		
		System.out.println(corrupted);
	}
}
